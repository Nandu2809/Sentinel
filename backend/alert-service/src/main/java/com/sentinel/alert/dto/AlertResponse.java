package com.sentinel.alert.dto;

import com.sentinel.alert.domain.model.AlertSeverity;
import com.sentinel.alert.domain.model.AlertStatus;
import java.time.Instant;
import java.util.UUID;

public record AlertResponse(
        UUID id,
        String alertCode,
        String title,
        String description,
        String threatType,
        AlertSeverity severity,
        double riskScore,
        String sourceService,
        String affectedApi,
        String affectedUser,
        String affectedIp,
        String correlationId,
        String evidenceJson,
        Instant createdAt,
        Instant updatedAt,
        String assignedAnalyst,
        AlertStatus status,
        String resolutionNotes
) {
}
