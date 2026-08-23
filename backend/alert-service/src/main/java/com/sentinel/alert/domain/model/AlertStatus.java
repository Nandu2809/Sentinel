package com.sentinel.alert.domain.model;

public enum AlertStatus {
    NEW,
    OPEN,
    ACKNOWLEDGED,
    INVESTIGATING,
    RESOLVED,
    FALSE_POSITIVE,
    CLOSED
}
