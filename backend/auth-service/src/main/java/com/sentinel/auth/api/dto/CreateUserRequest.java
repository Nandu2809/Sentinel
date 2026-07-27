package com.sentinel.auth.api.dto;

import com.sentinel.auth.domain.model.UserStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.UUID;

public record CreateUserRequest(
        @NotBlank @Email @Size(max = 320) String email,
        @NotBlank @Size(min = 3, max = 80) @Pattern(regexp = "^[a-zA-Z0-9._-]+$") String username,
        @NotBlank @Size(min = 12, max = 128) String password,
        @NotBlank @Size(max = 80) String firstName,
        @NotBlank @Size(max = 80) String lastName,
        @NotNull UUID roleId,
        UserStatus status
) {
}

