package com.sentinel.monitoring.api.dto;

import java.time.Instant;
import java.util.UUID;

public record ApiResponseDto(
        UUID id,
        String name,
        String pathPattern,
        String method,
        boolean enabled,
        int rateLimitPerMin,
        String description,
        Instant createdAt,
        Instant updatedAt
) {
}
