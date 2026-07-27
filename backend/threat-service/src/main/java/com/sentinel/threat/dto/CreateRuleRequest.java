package com.sentinel.threat.dto;

import com.sentinel.threat.domain.model.ThreatSeverity;
import com.sentinel.threat.domain.model.ThreatType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateRuleRequest(
        @NotBlank(message = "Rule code is required")
        @Size(max = 80, message = "Rule code cannot exceed 80 characters")
        String ruleCode,

        @NotBlank(message = "Rule name is required")
        @Size(max = 120, message = "Rule name cannot exceed 120 characters")
        String name,

        String description,

        @NotNull(message = "Threat type is required")
        ThreatType threatType,

        @NotNull(message = "Severity is required")
        ThreatSeverity severity,

        boolean enabled,

        @Min(value = 1, message = "Threshold must be at least 1")
        int threshold,

        String recommendation
) {
}
