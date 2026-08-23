package com.sentinel.alert.incident.dto;

import com.sentinel.alert.domain.model.AlertSeverity;
import com.sentinel.alert.incident.domain.model.IncidentStatus;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record IncidentResponse(
        UUID id,
        String incidentNumber,
        UUID alertId,
        String title,
        String description,
        AlertSeverity severity,
        IncidentStatus status,
        String assignedAnalyst,
        double riskScore,
        double aiConfidence,
        String affectedUser,
        String affectedIp,
        String affectedDevice,
        String evidenceJson,
        Instant createdAt,
        Instant updatedAt,
        Instant resolvedAt,
        List<IncidentTimelineEventResponse> timeline,
        List<IncidentActionResponse> actions,
        List<IncidentNoteResponse> notes
) {}
