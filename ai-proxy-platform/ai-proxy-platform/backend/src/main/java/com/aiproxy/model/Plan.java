package com.aiproxy.model;

public enum Plan {
    FREE(10, 50_000L, 0.0),
    PRO(60, 500_000L, 19.99),
    ENTERPRISE(-1, -1L, 99.99);

    private final int requestsPerMinute;
    private final long monthlyTokens;
    private final double monthlyPrice;

    Plan(int requestsPerMinute, long monthlyTokens, double monthlyPrice) {
        this.requestsPerMinute = requestsPerMinute;
        this.monthlyTokens = monthlyTokens;
        this.monthlyPrice = monthlyPrice;
    }

    public int getRequestsPerMinute() {
        return requestsPerMinute;
    }

    public long getMonthlyTokens() {
        return monthlyTokens;
    }

    public double getMonthlyPrice() {
        return monthlyPrice;
    }

    public boolean isUnlimited() {
        return requestsPerMinute < 0 && monthlyTokens < 0;
    }

    public boolean hasRateLimit() {
        return requestsPerMinute > 0;
    }

    public boolean hasTokenQuota() {
        return monthlyTokens > 0;
    }
}
