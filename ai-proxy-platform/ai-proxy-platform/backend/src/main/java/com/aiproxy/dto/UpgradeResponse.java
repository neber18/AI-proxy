package com.aiproxy.dto;

import com.aiproxy.model.Plan;

import java.time.Instant;

public record UpgradeResponse(
        String transactionId,
        Plan previousPlan,
        Plan currentPlan,
        double amountCharged,
        Instant processedAt,
        QuotaStatusResponse status
) {
}
