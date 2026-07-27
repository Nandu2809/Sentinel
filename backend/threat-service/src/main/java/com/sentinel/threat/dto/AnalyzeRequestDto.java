package com.sentinel.threat.dto;

import jakarta.validation.constraints.NotBlank;
import java.time.Instant;
import java.util.Map;

public record AnalyzeRequestDto(
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

        Map<String, String> headers,
        Map<String, String> queryParams,
        String body,
        String authToken,
        String userId,
        Integer statusCode,
        Long latencyMs,
        Instant timestamp
) {
}
