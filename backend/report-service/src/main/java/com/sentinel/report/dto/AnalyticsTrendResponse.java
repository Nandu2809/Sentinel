package com.sentinel.report.dto;

import java.util.List;
import java.util.Map;

public record AnalyticsTrendResponse(
        String category,
        String timeFrame,
        List<Map<String, Object>> points
) {
}
