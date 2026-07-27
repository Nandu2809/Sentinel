package com.sentinel.alert.dto;

import java.util.Map;

public record AlertStatisticsResponse(
        long totalAlerts,
        long newAlerts,
        long openAlerts,
        long resolvedAlerts,
        long criticalAlerts,
        Map<String, Long> severityDistribution,
        Map<String, Long> statusDistribution
) {
}
