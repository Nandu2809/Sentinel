package com.sentinel.alert.dto;

import com.sentinel.alert.domain.model.AlertSeverity;
import java.time.Instant;
import java.util.UUID;

public record NotificationPreferenceResponse(
        UUID id,
        String userId,
        boolean emailEnabled,
        boolean websocketEnabled,
        String webhookUrl,
        AlertSeverity minSeverity,
        Instant updatedAt
) {
}
