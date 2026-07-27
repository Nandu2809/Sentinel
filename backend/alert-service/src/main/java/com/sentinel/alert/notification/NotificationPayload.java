package com.sentinel.alert.notification;

import com.sentinel.alert.domain.model.AlertSeverity;
import java.util.UUID;

public record NotificationPayload(
        UUID alertId,
        String alertCode,
        String title,
        String description,
        AlertSeverity severity,
        double riskScore,
        String affectedApi,
        String recipient
) {
}
