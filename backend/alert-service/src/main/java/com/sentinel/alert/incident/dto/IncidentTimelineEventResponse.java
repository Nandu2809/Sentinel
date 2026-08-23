package com.sentinel.alert.incident.dto;

import java.time.Instant;
import java.util.UUID;

public record IncidentTimelineEventResponse(
        UUID id,
        UUID incidentId,
        String eventType,
        String source,
        String summary,
        String detailsJson,
        Instant timestamp
) {}
