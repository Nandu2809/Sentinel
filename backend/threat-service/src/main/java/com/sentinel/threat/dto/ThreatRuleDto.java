package com.sentinel.threat.dto;

import com.sentinel.threat.domain.model.ThreatSeverity;
import com.sentinel.threat.domain.model.ThreatType;
import java.time.Instant;
import java.util.UUID;

public record ThreatRuleDto(
        UUID id,
        String ruleCode,
        String name,
        String description,
        ThreatType threatType,
        ThreatSeverity severity,
        boolean enabled,
        int threshold,
        String recommendation,
        Instant createdAt,
        Instant updatedAt
) {
}
