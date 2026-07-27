package com.sentinel.alert.dto;

import com.sentinel.alert.domain.model.AlertSeverity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateAlertRequest(
        @NotBlank(message = "Title is required")
        String title,

        String description,

        @NotBlank(message = "Threat type is required")
        String threatType,

        @NotNull(message = "Severity is required")
        AlertSeverity severity,

        Double riskScore,

        @NotBlank(message = "Source service is required")
        String sourceService,

        String affectedApi,
        String affectedUser,
        String affectedIp,
        String correlationId,
        String evidenceJson
) {
}
