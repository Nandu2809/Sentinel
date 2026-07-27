package com.sentinel.monitoring.event;

import java.time.Instant;

public record ApiErrorOccurredEvent(
        String requestId,
        String correlationId,
        int statusCode,
        String errorMessage,
        String uri,
        Instant timestamp
) {
}
