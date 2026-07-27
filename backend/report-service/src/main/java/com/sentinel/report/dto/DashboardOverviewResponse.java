package com.sentinel.report.dto;

import com.sentinel.report.domain.model.RiskLevel;
import java.time.Instant;

public record DashboardOverviewResponse(
        long totalApisMonitored,
        long totalRequests,
        long totalThreatsDetected,
        long activeIncidents,
        long criticalAlerts,
        double currentSecurityScore,
        RiskLevel riskLevel,
        Instant capturedAt
) {
}
