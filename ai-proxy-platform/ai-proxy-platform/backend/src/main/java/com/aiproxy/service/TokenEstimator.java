package com.aiproxy.service;

import org.springframework.stereotype.Component;

@Component
public class TokenEstimator {

    private static final double CHARS_PER_TOKEN = 4.0;

    public long estimate(String text) {
        if (text == null || text.isBlank()) {
            return 0;
        }
        return (long) Math.ceil(text.trim().length() / CHARS_PER_TOKEN);
    }
}
