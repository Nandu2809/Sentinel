package com.sentinel.threat.events;

import java.time.Instant;

public record RiskScoreCalculatedEvent(
        String clientIp,
        String userId,
        String endpoint,
        double riskScore,
        Instant timestamp
) {
}
