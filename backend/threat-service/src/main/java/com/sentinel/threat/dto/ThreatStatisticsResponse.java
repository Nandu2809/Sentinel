package com.sentinel.threat.dto;

import java.util.List;
import java.util.Map;

public record ThreatStatisticsResponse(
        long totalThreatCount,
        long criticalIncidentCount,
        double averageRiskScore,
        List<Map<String, Object>> topThreatTypes,
        List<Map<String, Object>> topAttackingIps,
        List<Map<String, Object>> topTargetedEndpoints
) {
}
