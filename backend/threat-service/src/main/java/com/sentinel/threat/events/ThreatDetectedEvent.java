package com.sentinel.threat.events;

import com.sentinel.threat.domain.model.ThreatSeverity;
import com.sentinel.threat.domain.model.ThreatType;
import java.time.Instant;

public record ThreatDetectedEvent(
        String threatCode,
        String correlationId,
        String requestId,
        String clientIp,
        String endpoint,
        ThreatType threatType,
        ThreatSeverity severity,
        double riskScore,
        Instant timestamp
) {
}
