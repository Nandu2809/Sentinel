package com.sentinel.common.events;

import java.time.Instant;
import java.util.UUID;

public record AlertEventEnvelope(
        UUID eventId,
        UUID userId,
        String alertType,
        String severity,
        Integer riskScore,
        String message,
        Instant timestamp
) {}
