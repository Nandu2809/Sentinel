package com.sentinel.auth.api.dto;

import jakarta.validation.constraints.Size;
import java.util.Set;
import java.util.UUID;

public record UpdateRoleRequest(
        @Size(max = 255) String description,
        Set<UUID> permissionIds
) {
}

