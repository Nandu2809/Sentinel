package com.sentinel.risk.dto;

import com.sentinel.risk.domain.model.EntityType;
import com.sentinel.risk.domain.model.RiskClassification;
import java.time.Instant;
import java.util.UUID;

public record RiskScoreResponse(
        UUID id,
        EntityType entityType,
        String entityId,
        double riskScore,
        RiskClassification classification,
        String factorsJson,
        Instant calculatedAt
) {
}
