package com.aiproxy.scheduler;

import com.aiproxy.service.QuotaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class QuotaResetScheduler {

    private static final Logger log = LoggerFactory.getLogger(QuotaResetScheduler.class);

    private final QuotaService quotaService;

    public QuotaResetScheduler(QuotaService quotaService) {
        this.quotaService = quotaService;
    }

    @Scheduled(cron = "0 * * * * *", zone = "${app.timezone:America/Bogota}")
    public void resetRateLimits() {
        quotaService.resetAllRateLimits();
    }

    @Scheduled(cron = "0 0 0 1 * *", zone = "${app.timezone:America/Bogota}")
    public void resetMonthlyQuotas() {
        quotaService.resetAllMonthlyQuotas();
        log.info("Monthly token quotas reset");
    }
}
