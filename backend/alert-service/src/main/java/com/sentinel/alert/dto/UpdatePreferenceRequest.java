package com.sentinel.alert.dto;

import com.sentinel.alert.domain.model.AlertSeverity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdatePreferenceRequest(
        @NotBlank(message = "User ID is required")
        String userId,

        Boolean emailEnabled,
        Boolean websocketEnabled,
        String webhookUrl,
        AlertSeverity minSeverity
) {
}
