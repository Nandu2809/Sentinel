package com.sentinel.monitoring.event;

import java.time.Instant;

public record ApiRequestReceivedEvent(
        String requestId,
        String correlationId,
        String clientIp,
        String httpMethod,
        String uri,
        String userId,
        Instant timestamp
) {
}
