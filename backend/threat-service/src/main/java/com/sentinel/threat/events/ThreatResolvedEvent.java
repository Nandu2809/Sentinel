package com.sentinel.threat.events;

import java.time.Instant;

public record ThreatResolvedEvent(
        String threatCode,
        String resolverUserId,
        String resolutionNotes,
        Instant timestamp
) {
}
