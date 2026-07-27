package com.sentinel.monitoring.api.dto;

import java.time.Instant;
import java.util.UUID;

public record ApiHealthResponse(
        UUID id,
        String serviceName,
        String status,
        long responseTimeMs,
        Instant lastCheckedAt
) {
}
