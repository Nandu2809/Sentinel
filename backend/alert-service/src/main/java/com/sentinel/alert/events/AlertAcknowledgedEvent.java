package com.sentinel.alert.events;

import java.util.UUID;

public record AlertAcknowledgedEvent(
        UUID alertId,
        String alertCode,
        String acknowledgedBy
) {
}
