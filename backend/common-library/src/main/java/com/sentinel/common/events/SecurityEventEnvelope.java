package com.sentinel.common.events;

import com.sentinel.common.security.SecurityEventType;
import java.time.Instant;
import java.util.UUID;

public record SecurityEventEnvelope(
        UUID eventId,
        Instant timestamp,
        SecurityEventType eventType,
        UUID userId,
        String email,
        String ipAddress,
        String userAgent,
        String outcome,
        String message
) {}
