package com.sentinel.monitoring.api.dto;

import java.util.List;
import java.util.Map;

public record ApiMetricsSummaryResponse(
        long totalRequests,
        double averageLatencyMs,
        List<Map<String, Object>> topUsedApis,
        List<Map<String, Object>> slowestApis,
        List<Map<String, Object>> highestErrorRateApis
) {
}
