package com.sentinel.alert.events;

import java.util.UUID;

public record NotificationSentEvent(
        UUID alertId,
        String channel,
        String recipient,
        String status
) {
}
