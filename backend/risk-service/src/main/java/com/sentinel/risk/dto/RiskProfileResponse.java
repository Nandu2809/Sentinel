package com.sentinel.risk.dto;

import com.sentinel.risk.domain.model.EntityType;
import com.sentinel.risk.domain.model.RiskClassification;
import com.sentinel.risk.domain.model.RiskTrend;
import java.time.Instant;
import java.util.UUID;

public record RiskProfileResponse(
        UUID id,
        EntityType entityType,
        String entityId,
        double currentRiskScore,
        double maxRiskScore,
        RiskClassification riskClassification,
        long threatCount,
        long incidentCount,
        RiskTrend riskTrend,
        Instant lastUpdatedAt
) {
}
