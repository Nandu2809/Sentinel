package com.sentinel.alert.incident.dto;

import java.time.Instant;

public record ThreatHuntingQueryRequest(
        String username,
        String email,
        String ipAddress,
        String device,
        String eventType,
        Double minRiskScore,
        Double maxRiskScore,
        Instant startDate,
        Instant endDate
) {}
