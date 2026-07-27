package com.sentinel.risk.events;

public record SecurityScoreUpdatedEvent(
        double securityScore,
        double threatHeatIndex,
        long activeCriticalIncidents
) {
}
