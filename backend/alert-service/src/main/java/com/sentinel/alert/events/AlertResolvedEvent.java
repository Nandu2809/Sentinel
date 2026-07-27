package com.sentinel.alert.events;

import java.util.UUID;

public record AlertResolvedEvent(
        UUID alertId,
        String alertCode,
        String resolvedBy,
        String resolutionNotes
) {
}
