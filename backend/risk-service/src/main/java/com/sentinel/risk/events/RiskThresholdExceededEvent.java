package com.sentinel.risk.events;

import com.sentinel.risk.domain.model.EntityType;

public record RiskThresholdExceededEvent(
        EntityType entityType,
        String entityId,
        double riskScore,
        double threshold
) {
}
