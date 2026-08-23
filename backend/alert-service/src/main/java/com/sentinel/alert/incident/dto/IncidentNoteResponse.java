package com.sentinel.alert.incident.dto;

import java.time.Instant;
import java.util.UUID;

public record IncidentNoteResponse(
        UUID id,
        UUID incidentId,
        String author,
        String content,
        Instant createdAt
) {}
