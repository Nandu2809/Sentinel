package com.sentinel.risk.events;

import com.sentinel.risk.domain.model.EntityType;

public record RiskCalculatedEvent(
        EntityType entityType,
        String entityId,
        double riskScore,
        String classification
) {
}
