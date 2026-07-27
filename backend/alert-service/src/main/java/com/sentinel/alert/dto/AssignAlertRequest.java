package com.sentinel.alert.dto;

import jakarta.validation.constraints.NotBlank;

public record AssignAlertRequest(
        @NotBlank(message = "Analyst is required")
        String analyst,

        @NotBlank(message = "Assigned by is required")
        String assignedBy
) {
}
