package com.sentinel.threat.events;

import com.sentinel.threat.domain.model.ThreatSeverity;
import com.sentinel.threat.domain.model.ThreatType;
import java.time.Instant;

public record IncidentCreatedEvent(
        String incidentCode,
        ThreatType threatType,
        ThreatSeverity severity,
        double riskScore,
        String affectedEndpoint,
        Instant timestamp
) {
}
