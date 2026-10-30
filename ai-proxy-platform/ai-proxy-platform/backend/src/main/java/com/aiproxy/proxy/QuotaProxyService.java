package com.aiproxy.proxy;

import com.aiproxy.dto.GenerationRequest;
import com.aiproxy.dto.GenerationResponse;
import com.aiproxy.exception.QuotaExceededException;
import com.aiproxy.model.Plan;
import com.aiproxy.model.UserAccount;
import com.aiproxy.repository.UserAccountRepository;
import com.aiproxy.service.AIGenerationService;
import com.aiproxy.service.TimeWindowService;
import com.aiproxy.service.TokenEstimator;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service("quotaProxyService")
public class QuotaProxyService implements AIGenerationService {

    private final AIGenerationService realService;
    private final UserAccountRepository repository;
    private final TokenEstimator tokenEstimator;
    private final TimeWindowService timeWindowService;

    public QuotaProxyService(@Qualifier("mockAIGenerationService") AIGenerationService realService,
                             UserAccountRepository repository,
                             TokenEstimator tokenEstimator,
                             TimeWindowService timeWindowService) {
        this.realService = realService;
        this.repository = repository;
        this.tokenEstimator = tokenEstimator;
        this.timeWindowService = timeWindowService;
    }

    @Override
    public GenerationResponse generate(GenerationRequest request) {
        UserAccount account = repository.findOrCreate(request.userId());
        Plan plan = account.getPlan();

        if (plan.hasTokenQuota()) {
            long remaining = account.getRemainingTokens();
            long estimatedPromptTokens = tokenEstimator.estimate(request.prompt());
            if (remaining <= 0) {
                throw new QuotaExceededException("Monthly token quota exhausted for plan " + plan + ". Upgrade your plan to continue.");
            }
            if (estimatedPromptTokens > remaining) {
                throw new QuotaExceededException("Prompt requires about " + estimatedPromptTokens + " tokens but only " + remaining + " remain this month.");
            }
        }

        GenerationResponse response = realService.generate(request);

        account.addTokens(response.totalTokens());
        account.recordUsage(timeWindowService.today(), response.totalTokens());

        return response;
    }
}
