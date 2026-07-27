package com.sentinel.auth.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreatePermissionRequest(
        @NotBlank @Size(max = 100) @Pattern(regexp = "^[A-Z0-9_]+$") String name,
        @NotBlank @Size(max = 80) String resource,
        @NotBlank @Size(max = 80) String action,
        @Size(max = 255) String description
) {
}

