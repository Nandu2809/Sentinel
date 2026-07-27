package com.sentinel.monitoring.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;

public record LogRequestDto(
        @NotBlank(message = "Request ID is required")
        String requestId,

        @NotBlank(message = "Correlation ID is required")
        String correlationId,

        @NotBlank(message = "Client IP is required")
        String clientIp,

        String userAgent,

        @NotBlank(message = "HTTP Method is required")
        String httpMethod,

        @NotBlank(message = "URI is required")
        String uri,

        @NotNull(message = "Status code is required")
        Integer statusCode,

        @NotNull(message = "Latency in ms is required")
        Long latencyMs,

        Long requestSize,
        Long responseSize,
        String userId,
        Instant timestamp
) {
}
