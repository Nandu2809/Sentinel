package com.sentinel.auth.api.dto;

import com.sentinel.auth.domain.model.UserStatus;
import java.time.Instant;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String email,
        String username,
        String firstName,
        String lastName,
        UserStatus status,
        boolean emailVerified,
        RoleSummary role,
        Instant createdAt,
        Instant updatedAt
) {
}

