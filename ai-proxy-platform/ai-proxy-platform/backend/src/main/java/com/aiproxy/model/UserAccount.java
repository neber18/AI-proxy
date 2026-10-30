package com.aiproxy.model;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UserAccount {

    private final String userId;
    private final Instant createdAt;
    private final Map<LocalDate, DailyUsage> dailyUsage = new ConcurrentHashMap<>();
    private Plan plan;
    private long tokensUsed;
    private int requestsInCurrentWindow;

    public UserAccount(String userId) {
        this.userId = userId;
        this.plan = Plan.FREE;
        this.createdAt = Instant.now();
    }

    public String getUserId() {
        return userId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public synchronized Plan getPlan() {
        return plan;
    }

    public synchronized void setPlan(Plan plan) {
        this.plan = plan;
    }

    public synchronized long getTokensUsed() {
        return tokensUsed;
    }

    public synchronized void addTokens(long tokens) {
        this.tokensUsed += tokens;
    }

    public synchronized void resetTokens() {
        this.tokensUsed = 0;
    }

    public synchronized int getRequestsInCurrentWindow() {
        return requestsInCurrentWindow;
    }

    public synchronized void incrementRequests() {
        this.requestsInCurrentWindow++;
    }

    public synchronized void resetRequestWindow() {
        this.requestsInCurrentWindow = 0;
    }

    public synchronized long getRemainingTokens() {
        if (!plan.hasTokenQuota()) {
            return -1;
        }
        return Math.max(0, plan.getMonthlyTokens() - tokensUsed);
    }

    public void recordUsage(LocalDate date, long tokens) {
        dailyUsage.computeIfAbsent(date, DailyUsage::new).add(tokens);
    }

    public DailyUsage getUsageFor(LocalDate date) {
        return dailyUsage.getOrDefault(date, new DailyUsage(date));
    }
}
