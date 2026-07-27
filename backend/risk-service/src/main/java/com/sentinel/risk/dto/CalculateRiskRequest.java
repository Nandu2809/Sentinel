package com.sentinel.risk.dto;

import com.sentinel.risk.domain.model.EntityType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CalculateRiskRequest(
        @NotNull(message = "Entity type is required")
        EntityType entityType,

        @NotBlank(message = "Entity ID is required")
        String entityId,

        String threatType,
        String severity,
        Boolean isAuthFailure,
        Boolean isCriticalAsset,
        Boolean isKnownBot
) {
}
