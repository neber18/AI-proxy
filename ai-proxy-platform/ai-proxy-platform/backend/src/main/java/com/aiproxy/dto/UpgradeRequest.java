package com.aiproxy.dto;

import com.aiproxy.model.Plan;
import jakarta.validation.constraints.NotNull;

public record UpgradeRequest(
        @NotNull Plan targetPlan,
        String cardHolder,
        String cardLastFour
) {
}
