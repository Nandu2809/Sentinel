package com.sentinel.auth.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.Set;
import java.util.UUID;

public record CreateRoleRequest(
        @NotBlank @Size(max = 80) @Pattern(regexp = "^[A-Z0-9_]+$") String name,
        @Size(max = 255) String description,
        Set<UUID> permissionIds
) {
}

