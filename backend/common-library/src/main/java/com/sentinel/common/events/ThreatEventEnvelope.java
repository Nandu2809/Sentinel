package com.sentinel.common.events;

import java.time.Instant;
import java.util.UUID;

public record ThreatEventEnvelope(
        UUID threatId,
        UUID eventId,
        String threatType,
        String severity,
        UUID userId,
        String email,
        String ipAddress,
        String description,
        double riskScore,
        Instant timestamp
) {}
