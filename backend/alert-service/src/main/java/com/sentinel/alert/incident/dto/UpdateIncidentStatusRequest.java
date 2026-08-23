package com.sentinel.alert.incident.dto;

import com.sentinel.alert.incident.domain.model.IncidentStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateIncidentStatusRequest(
        @NotNull(message = "Status is required")
        IncidentStatus status,
        String notes,
        String updatedBy
) {}
