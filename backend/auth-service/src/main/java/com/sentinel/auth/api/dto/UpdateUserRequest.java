package com.sentinel.auth.api.dto;

import com.sentinel.auth.domain.model.UserStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;

public record UpdateUserRequest(
        @NotBlank @Size(max = 80) String firstName,
        @NotBlank @Size(max = 80) String lastName,
        @NotNull UserStatus status,
        @NotNull UUID roleId
) {
}

