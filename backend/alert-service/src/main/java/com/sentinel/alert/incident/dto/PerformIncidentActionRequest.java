package com.sentinel.alert.incident.dto;

import com.sentinel.alert.incident.domain.model.IncidentActionType;
import jakarta.validation.constraints.NotNull;

public record PerformIncidentActionRequest(
        @NotNull(message = "Action type is required")
        IncidentActionType actionType,
        String performedBy,
        String target,
        String details
) {}
