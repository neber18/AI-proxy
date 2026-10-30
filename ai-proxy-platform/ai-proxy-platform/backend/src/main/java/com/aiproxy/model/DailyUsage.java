package com.aiproxy.model;

import java.time.LocalDate;

public class DailyUsage {

    private final LocalDate date;
    private long tokens;
    private int requests;

    public DailyUsage(LocalDate date) {
        this.date = date;
    }

    public synchronized void add(long tokens) {
        this.tokens += tokens;
        this.requests++;
    }

    public LocalDate getDate() {
        return date;
    }

    public synchronized long getTokens() {
        return tokens;
    }

    public synchronized int getRequests() {
        return requests;
    }
}
