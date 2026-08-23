package com.sentinel.alert.incident.dto;

import com.sentinel.alert.incident.domain.model.IncidentActionType;
import java.time.Instant;
import java.util.UUID;

public record IncidentActionResponse(
        UUID id,
        UUID incidentId,
        IncidentActionType actionType,
        String performedBy,
        String target,
        String details,
        Instant timestamp
) {}
