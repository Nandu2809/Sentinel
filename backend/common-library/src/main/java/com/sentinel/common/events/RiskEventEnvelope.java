package com.sentinel.common.events;

import java.time.Instant;
import java.util.UUID;

public record RiskEventEnvelope(
        UUID eventId,
        UUID userId,
        String email,
        int riskScore,
        String riskLevel,
        String decision,
        Instant timestamp
) {}
