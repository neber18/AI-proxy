package com.aiproxy.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

@Component
public class TimeWindowService {

    private final ZoneId zoneId;

    public TimeWindowService(@Value("${app.timezone:America/Bogota}") String timezone) {
        this.zoneId = ZoneId.of(timezone);
    }

    public ZoneId getZoneId() {
        return zoneId;
    }

    public LocalDate today() {
        return LocalDate.now(zoneId);
    }

    public long secondsUntilNextMinute() {
        Instant now = Instant.now();
        Instant nextMinute = now.truncatedTo(ChronoUnit.MINUTES).plus(1, ChronoUnit.MINUTES);
        return Math.max(1, (long) Math.ceil(Duration.between(now, nextMinute).toMillis() / 1000.0));
    }

    public LocalDate nextMonthlyResetDate() {
        return today().withDayOfMonth(1).plusMonths(1);
    }

    public Instant nextMonthlyResetInstant() {
        return nextMonthlyResetDate().atStartOfDay(zoneId).toInstant();
    }

    public long secondsUntilMonthlyReset() {
        return Math.max(0, Duration.between(ZonedDateTime.now(zoneId).toInstant(), nextMonthlyResetInstant()).getSeconds());
    }
}
