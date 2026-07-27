package com.sentinel.alert.dto;

import com.sentinel.alert.domain.model.AlertStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ResolveAlertRequest(
        @NotNull(message = "Status is required")
        AlertStatus status,

        @NotBlank(message = "Resolution notes are required")
        String resolutionNotes,

        @NotBlank(message = "Resolved by is required")
        String resolvedBy
) {
}
