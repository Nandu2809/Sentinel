package com.sentinel.monitoring.event;

import java.time.Instant;

public record ApiResponseCompletedEvent(
        String requestId,
        String correlationId,
        int statusCode,
        long latencyMs,
        long responseSize,
        Instant timestamp
) {
}
