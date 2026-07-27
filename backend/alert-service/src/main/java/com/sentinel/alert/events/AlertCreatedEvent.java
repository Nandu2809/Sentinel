package com.sentinel.alert.events;

import com.sentinel.alert.domain.model.AlertSeverity;
import java.util.UUID;

public record AlertCreatedEvent(
        UUID alertId,
        String alertCode,
        String title,
        AlertSeverity severity,
        double riskScore,
        String sourceService
) {
}
