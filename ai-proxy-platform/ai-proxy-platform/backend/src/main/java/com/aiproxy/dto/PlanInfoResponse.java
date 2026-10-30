package com.aiproxy.dto;

import com.aiproxy.model.Plan;

public record PlanInfoResponse(Plan plan, int requestsPerMinute, long monthlyTokens, double monthlyPrice, boolean unlimited) {

    public static PlanInfoResponse from(Plan plan) {
        return new PlanInfoResponse(plan, plan.getRequestsPerMinute(), plan.getMonthlyTokens(), plan.getMonthlyPrice(), plan.isUnlimited());
    }
}
