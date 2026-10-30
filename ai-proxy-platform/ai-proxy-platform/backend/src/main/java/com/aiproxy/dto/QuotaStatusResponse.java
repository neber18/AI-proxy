package com.aiproxy.dto;

import com.aiproxy.model.Plan;

import java.time.Instant;
import java.time.LocalDate;

public record QuotaStatusResponse(
        String userId,
        Plan plan,
        boolean unlimited,
        long tokensUsed,
        long tokensRemaining,
        long tokenLimit,
        LocalDate resetDate,
        Instant resetAt,
        long secondsUntilReset,
        int requestsThisMinute,
        int requestsPerMinuteLimit,
        long rateLimitResetSeconds
) {
}
