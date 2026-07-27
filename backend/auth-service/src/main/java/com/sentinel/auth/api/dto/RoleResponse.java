package com.sentinel.auth.api.dto;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

public record RoleResponse(
        UUID id,
        String name,
        String description,
        boolean systemRole,
        Set<PermissionResponse> permissions,
        Instant createdAt,
        Instant updatedAt
) {
}

