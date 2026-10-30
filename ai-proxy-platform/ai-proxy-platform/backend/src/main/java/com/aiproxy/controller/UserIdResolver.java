package com.aiproxy.controller;

final class UserIdResolver {

    static final String HEADER = "X-User-Id";
    private static final String DEFAULT_USER = "demo-user";
    private static final int MAX_LENGTH = 64;

    private UserIdResolver() {
    }

    static String resolve(String... candidates) {
        for (String candidate : candidates) {
            if (candidate != null && !candidate.isBlank()) {
                String clean = candidate.trim().replaceAll("[^A-Za-z0-9_.@-]", "");
                if (!clean.isEmpty()) {
                    return clean.length() > MAX_LENGTH ? clean.substring(0, MAX_LENGTH) : clean;
                }
            }
        }
        return DEFAULT_USER;
    }
}
