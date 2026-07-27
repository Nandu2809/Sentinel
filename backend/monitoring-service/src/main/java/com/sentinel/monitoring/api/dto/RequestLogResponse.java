package com.sentinel.monitoring.api.dto;

import java.time.Instant;
import java.util.UUID;

public record RequestLogResponse(
        UUID id,
        UUID apiId,
        String apiName,
        String requestId,
        String correlationId,
        String clientIp,
        String userAgent,
        String httpMethod,
        String uri,
        int statusCode,
        long latencyMs,
        long requestSize,
        long responseSize,
        String userId,
        Instant timestamp
) {
}
