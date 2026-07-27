package com.sentinel.monitoring.service;

import jakarta.servlet.http.HttpServletRequest;

public record RequestMetadata(
        String ipAddress,
        String userAgent
) {
    public static RequestMetadata from(HttpServletRequest request) {
        if (request == null) {
            return new RequestMetadata("unknown", "unknown");
        }
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isBlank()) {
            ip = request.getRemoteAddr();
        } else {
            ip = ip.split(",")[0].trim();
        }
        String agent = request.getHeader("User-Agent");
        return new RequestMetadata(
                ip != null ? ip : "unknown",
                agent != null ? agent : "unknown"
        );
    }
}
