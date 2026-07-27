package com.sentinel.threat.dto;

import com.sentinel.threat.domain.model.ThreatSeverity;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateRuleRequest(
        @NotBlank(message = "Rule name is required")
        @Size(max = 120, message = "Rule name cannot exceed 120 characters")
        String name,

        String description,

        @NotNull(message = "Severity is required")
        ThreatSeverity severity,

        boolean enabled,

        @Min(value = 1, message = "Threshold must be at least 1")
        int threshold,

        String recommendation
) {
}
