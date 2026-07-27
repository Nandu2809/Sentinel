package com.sentinel.risk.dto;

import java.time.Instant;

public record SecurityScoreResponse(
        double securityScore,
        double threatHeatIndex,
        long activeCriticalIncidents,
        long totalEntitiesAtRisk,
        Instant calculatedAt
) {
}
