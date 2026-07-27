package com.sentinel.risk.dto;

import java.util.List;
import java.util.Map;

public record RiskTrendResponse(
        String timeFrame,
        double averageRiskScore,
        List<Map<String, Object>> trendDataPoints
) {
}
