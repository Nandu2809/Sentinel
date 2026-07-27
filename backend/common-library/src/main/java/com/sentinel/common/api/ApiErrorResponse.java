package com.sentinel.common.api;

import java.time.Instant;
import java.util.List;

public record ApiErrorResponse(
        boolean success,
        Instant timestamp,
        int status,
        String error,
        String message,
        String path,
        List<FieldViolation> violations
) {
    public static ApiErrorResponse of(int status, String error, String message, String path) {
        return new ApiErrorResponse(false, Instant.now(), status, error, message, path, List.of());
    }

    public static ApiErrorResponse of(int status, String error, String message, String path, List<FieldViolation> violations) {
        return new ApiErrorResponse(false, Instant.now(), status, error, message, path, violations);
    }
}
