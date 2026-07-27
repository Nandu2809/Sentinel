package com.sentinel.risk.events;

import com.sentinel.risk.domain.model.EntityType;

public record RiskUpdatedEvent(
        EntityType entityType,
        String entityId,
        double previousScore,
        double newScore,
        String trend
) {
}
