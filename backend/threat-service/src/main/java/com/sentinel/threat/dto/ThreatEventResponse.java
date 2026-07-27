package com.sentinel.threat.dto;

import com.sentinel.threat.domain.model.ThreatSeverity;
import com.sentinel.threat.domain.model.ThreatStatus;
import com.sentinel.threat.domain.model.ThreatType;
import java.time.Instant;
import java.util.UUID;

public record ThreatEventResponse(
        UUID id,
        String threatCode,
        String correlationId,
        String requestId,
        String userId,
        String clientIp,
        String endpoint,
        String httpMethod,
        ThreatType threatType,
        String matchedRuleCode,
        ThreatSeverity severity,
        double riskScore,
        String recommendation,
        ThreatStatus status,
        Instant createdAt
) {
}
