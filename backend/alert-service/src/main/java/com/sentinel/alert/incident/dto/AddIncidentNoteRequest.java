package com.sentinel.alert.incident.dto;

import jakarta.validation.constraints.NotBlank;

public record AddIncidentNoteRequest(
        @NotBlank(message = "Note content is required")
        String content,
        String author
) {}
