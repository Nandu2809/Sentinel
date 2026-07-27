package com.sentinel.alert.domain.model;

public enum AlertStatus {
    NEW,
    ACKNOWLEDGED,
    INVESTIGATING,
    RESOLVED,
    FALSE_POSITIVE,
    CLOSED
}
