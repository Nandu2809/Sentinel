package com.sentinel.auth.api.dto;

import java.time.Instant;
import java.util.UUID;

public record PermissionResponse(
        UUID id,
        String name,
        String resource,
        String action,
        String description,
        Instant createdAt,
        Instant updatedAt
) {
}

