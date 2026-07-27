package com.sentinel.threat.ruleengine;

import java.time.Instant;
import java.util.Map;

public record RequestTelemetry(
        String requestId,
        String correlationId,
        String clientIp,
        String userAgent,
        String httpMethod,
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
    public String getHeader(String name) {
        if (headers == null || name == null) {
            return null;
        }
        return headers.entrySet().stream()
                .filter(e -> name.equalsIgnoreCase(e.getKey()))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(null);
    }
}
