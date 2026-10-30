package com.aiproxy.controller;

import com.aiproxy.dto.GenerationRequest;
import com.aiproxy.dto.GenerationResponse;
import com.aiproxy.service.AIGenerationService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ai")
public class AIController {

    private final AIGenerationService aiGenerationService;

    public AIController(AIGenerationService aiGenerationService) {
        this.aiGenerationService = aiGenerationService;
    }

    @PostMapping("/generate")
    public GenerationResponse generate(@RequestHeader(value = UserIdResolver.HEADER, required = false) String headerUserId,
                                       @Valid @RequestBody GenerationRequest request) {
        String userId = UserIdResolver.resolve(headerUserId, request.userId());
        return aiGenerationService.generate(request.withUserId(userId));
    }
}
