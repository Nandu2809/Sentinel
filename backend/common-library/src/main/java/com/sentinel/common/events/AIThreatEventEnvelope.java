package com.sentinel.common.events;

import java.time.Instant;
import java.util.UUID;

public record AIThreatEventEnvelope(
        UUID eventId,
        UUID userId,
        String email,
        Double anomalyScore,
        String prediction,
        Double confidence,
        String reason,
        Instant timestamp
) {}
