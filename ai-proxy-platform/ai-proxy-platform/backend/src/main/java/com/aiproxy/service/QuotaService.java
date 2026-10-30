package com.aiproxy.service;

import com.aiproxy.dto.DailyUsageResponse;
import com.aiproxy.dto.PlanInfoResponse;
import com.aiproxy.dto.QuotaStatusResponse;
import com.aiproxy.dto.UpgradeRequest;
import com.aiproxy.dto.UpgradeResponse;
import com.aiproxy.exception.InvalidUpgradeException;
import com.aiproxy.model.DailyUsage;
import com.aiproxy.model.Plan;
import com.aiproxy.model.UserAccount;
import com.aiproxy.repository.UserAccountRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class QuotaService {

    private static final int HISTORY_DAYS = 7;

    private final UserAccountRepository repository;
    private final TimeWindowService timeWindowService;

    public QuotaService(UserAccountRepository repository, TimeWindowService timeWindowService) {
        this.repository = repository;
        this.timeWindowService = timeWindowService;
    }

    public QuotaStatusResponse getStatus(String userId) {
        UserAccount account = repository.findOrCreate(userId);
        Plan plan = account.getPlan();
        return new QuotaStatusResponse(
                account.getUserId(),
                plan,
                plan.isUnlimited(),
                account.getTokensUsed(),
                account.getRemainingTokens(),
                plan.getMonthlyTokens(),
                timeWindowService.nextMonthlyResetDate(),
                timeWindowService.nextMonthlyResetInstant(),
                timeWindowService.secondsUntilMonthlyReset(),
                account.getRequestsInCurrentWindow(),
                plan.getRequestsPerMinute(),
                timeWindowService.secondsUntilNextMinute()
        );
    }

    public List<DailyUsageResponse> getHistory(String userId) {
        UserAccount account = repository.findOrCreate(userId);
        LocalDate today = timeWindowService.today();
        List<DailyUsageResponse> history = new ArrayList<>();
        for (int i = HISTORY_DAYS - 1; i >= 0; i--) {
            DailyUsage usage = account.getUsageFor(today.minusDays(i));
            history.add(new DailyUsageResponse(usage.getDate(), usage.getTokens(), usage.getRequests()));
        }
        return history;
    }

    public UpgradeResponse upgrade(String userId, UpgradeRequest request) {
        UserAccount account = repository.findOrCreate(userId);
        Plan current = account.getPlan();
        Plan target = request.targetPlan();

        if (target.ordinal() <= current.ordinal()) {
            throw new InvalidUpgradeException("Cannot upgrade from " + current + " to " + target + ".");
        }
        if (request.cardLastFour() == null || !request.cardLastFour().matches("\\d{4}")) {
            throw new InvalidUpgradeException("A valid simulated card is required to process the payment.");
        }

        account.setPlan(target);

        return new UpgradeResponse(
                "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(),
                current,
                target,
                target.getMonthlyPrice(),
                Instant.now(),
                getStatus(userId)
        );
    }

    public List<PlanInfoResponse> getPlans() {
        return Arrays.stream(Plan.values()).map(PlanInfoResponse::from).toList();
    }

    public void resetAllRateLimits() {
        repository.findAll().forEach(UserAccount::resetRequestWindow);
    }

    public void resetAllMonthlyQuotas() {
        repository.findAll().forEach(UserAccount::resetTokens);
    }
}
