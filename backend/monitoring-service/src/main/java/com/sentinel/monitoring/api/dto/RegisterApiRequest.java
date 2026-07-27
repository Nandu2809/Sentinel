package com.sentinel.monitoring.api.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterApiRequest(
        @NotBlank(message = "API name is required")
        @Size(max = 100, message = "API name cannot exceed 100 characters")
        String name,

        @NotBlank(message = "Path pattern is required")
        String pathPattern,

        @NotBlank(message = "HTTP method is required")
        @Pattern(regexp = "^(GET|POST|PUT|DELETE|PATCH|HEAD|OPTIONS)$", message = "Invalid HTTP method")
        String method,

        boolean enabled,

        @Min(value = 1, message = "Rate limit must be at least 1 req/min")
        int rateLimitPerMin,

        String description
) {
}
