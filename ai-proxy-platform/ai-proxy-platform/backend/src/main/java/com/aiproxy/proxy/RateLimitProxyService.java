package com.aiproxy.proxy;

import com.aiproxy.dto.GenerationRequest;
import com.aiproxy.dto.GenerationResponse;
import com.aiproxy.exception.RateLimitExceededException;
import com.aiproxy.model.Plan;
import com.aiproxy.model.UserAccount;
import com.aiproxy.repository.UserAccountRepository;
import com.aiproxy.service.AIGenerationService;
import com.aiproxy.service.TimeWindowService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Primary
@Service("rateLimitProxyService")
public class RateLimitProxyService implements AIGenerationService {

    private final AIGenerationService next;
    private final UserAccountRepository repository;
    private final TimeWindowService timeWindowService;

    public RateLimitProxyService(@Qualifier("quotaProxyService") AIGenerationService next,
                                 UserAccountRepository repository,
                                 TimeWindowService timeWindowService) {
        this.next = next;
        this.repository = repository;
        this.timeWindowService = timeWindowService;
    }

    @Override
    public GenerationResponse generate(GenerationRequest request) {
        UserAccount account = repository.findOrCreate(request.userId());
        Plan plan = account.getPlan();

        if (plan.hasRateLimit()) {
            synchronized (account) {
                if (account.getRequestsInCurrentWindow() >= plan.getRequestsPerMinute()) {
                    long retryAfter = timeWindowService.secondsUntilNextMinute();
                    throw new RateLimitExceededException(
                            "Rate limit of " + plan.getRequestsPerMinute() + " requests per minute exceeded for plan " + plan + ".",
                            retryAfter);
                }
                account.incrementRequests();
            }
        } else {
            account.incrementRequests();
        }

        return next.generate(request);
    }
}
