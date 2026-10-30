package com.aiproxy.dto;

import java.time.Instant;

public record ApiError(int status, String error, String message, Long retryAfterSeconds, Instant timestamp) {
}
