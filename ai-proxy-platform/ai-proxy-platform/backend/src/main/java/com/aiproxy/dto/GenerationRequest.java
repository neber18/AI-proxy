package com.aiproxy.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record GenerationRequest(
        String userId,
        @NotBlank @Size(max = 4000) String prompt
) {
    public GenerationRequest withUserId(String newUserId) {
        return new GenerationRequest(newUserId, prompt);
    }
}
