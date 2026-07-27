package com.sentinel.threat.dto;

import com.sentinel.threat.domain.model.IncidentStatus;
import com.sentinel.threat.domain.model.ThreatSeverity;
import com.sentinel.threat.domain.model.ThreatType;
import java.time.Instant;
import java.util.UUID;

public record IncidentResponse(
        UUID id,
        String incidentCode,
        ThreatType threatType,
        ThreatSeverity severity,
        double riskScore,
        String affectedEndpoint,
        String affectedUser,
        String evidenceJson,
        IncidentStatus status,
        String mitigationRecommendation,
        Instant createdAt,
        Instant updatedAt
) {
}
