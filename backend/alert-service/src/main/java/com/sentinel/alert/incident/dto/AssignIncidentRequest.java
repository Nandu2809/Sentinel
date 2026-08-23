package com.sentinel.alert.incident.dto;

import jakarta.validation.constraints.NotBlank;

public record AssignIncidentRequest(
        @NotBlank(message = "Assigned analyst is required")
        String assignedAnalyst,
        String assignedBy
) {}
