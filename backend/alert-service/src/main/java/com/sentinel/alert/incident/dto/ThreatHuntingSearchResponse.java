package com.sentinel.alert.incident.dto;

import com.sentinel.alert.domain.model.AlertSeverity;
import java.time.Instant;
import java.util.UUID;

public record ThreatHuntingSearchResponse(
        UUID id,
        String incidentNumber,
        String title,
        String eventType,
        String source,
        String user,
        String ipAddress,
        String device,
        double riskScore,
        AlertSeverity severity,
        Instant timestamp,
        UUID incidentId
) {}
