package com.sentinel.auth.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdatePermissionRequest(
        @NotBlank @Size(max = 80) String resource,
        @NotBlank @Size(max = 80) String action,
        @Size(max = 255) String description
) {
}

