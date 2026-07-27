package com.sentinel.common.api;

import java.time.Instant;

public record ApiResponse<T>(
        boolean success,
        Instant timestamp,
        int status,
        String message,
        T data
) {
    public static <T> ApiResponse<T> success(int status, String message, T data) {
        return new ApiResponse<>(true, Instant.now(), status, message, data);
    }
}
