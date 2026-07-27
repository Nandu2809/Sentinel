package com.sentinel.alert.dto;

import java.util.List;
import java.util.Map;

public record AlertTrendResponse(
        String timeFrame,
        List<Map<String, Object>> trendData
) {
}
