package com.aiproxy.controller;

import com.aiproxy.dto.DailyUsageResponse;
import com.aiproxy.dto.PlanInfoResponse;
import com.aiproxy.dto.QuotaStatusResponse;
import com.aiproxy.dto.UpgradeRequest;
import com.aiproxy.dto.UpgradeResponse;
import com.aiproxy.service.QuotaService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/quota")
public class QuotaController {

    private final QuotaService quotaService;

    public QuotaController(QuotaService quotaService) {
        this.quotaService = quotaService;
    }

    @GetMapping("/status")
    public QuotaStatusResponse status(@RequestHeader(value = UserIdResolver.HEADER, required = false) String headerUserId,
                                      @RequestParam(value = "userId", required = false) String paramUserId) {
        return quotaService.getStatus(UserIdResolver.resolve(headerUserId, paramUserId));
    }

    @GetMapping("/history")
    public List<DailyUsageResponse> history(@RequestHeader(value = UserIdResolver.HEADER, required = false) String headerUserId,
                                            @RequestParam(value = "userId", required = false) String paramUserId) {
        return quotaService.getHistory(UserIdResolver.resolve(headerUserId, paramUserId));
    }

    @PostMapping("/upgrade")
    public UpgradeResponse upgrade(@RequestHeader(value = UserIdResolver.HEADER, required = false) String headerUserId,
                                   @Valid @RequestBody UpgradeRequest request) {
        return quotaService.upgrade(UserIdResolver.resolve(headerUserId), request);
    }

    @GetMapping("/plans")
    public List<PlanInfoResponse> plans() {
        return quotaService.getPlans();
    }
}
