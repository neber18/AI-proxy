package com.aiproxy.dto;

import java.time.Instant;

public record GenerationResponse(
        String text,
        long promptTokens,
        long completionTokens,
        long totalTokens,
        String model,
        long latencyMs,
        Instant createdAt
) {
}
