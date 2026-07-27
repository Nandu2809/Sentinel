package com.sentinel.report.controller;

import com.sentinel.common.api.ApiResponse;
import com.sentinel.report.dto.DashboardOverviewResponse;
import com.sentinel.report.service.DashboardOverviewService;
import com.sentinel.report.service.SecurityAnalyticsService;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/dashboard")
public class DashboardController {
    private final DashboardOverviewService overviewService;
    private final SecurityAnalyticsService analyticsService;

    public DashboardController(DashboardOverviewService overviewService, SecurityAnalyticsService analyticsService) {
        this.overviewService = overviewService;
        this.analyticsService = analyticsService;
    }

    @GetMapping("/overview")
    public ApiResponse<DashboardOverviewResponse> getOverview() {
        return ApiResponse.success(HttpStatus.OK.value(), "Dashboard overview retrieved", overviewService.getOverview());
    }

    @GetMapping("/threats")
    public ApiResponse<Object> getThreatsSummary() {
        return ApiResponse.success(HttpStatus.OK.value(), "Threats summary retrieved", analyticsService.getTopThreats());
    }

    @GetMapping("/risk")
    public ApiResponse<List<Map<String, Object>>> getRiskSummary() {
        return ApiResponse.success(HttpStatus.OK.value(), "Risk summary retrieved", analyticsService.getTopRiskAssets());
    }

    @GetMapping("/apis")
    public ApiResponse<Map<String, Object>> getApisSummary() {
        return ApiResponse.success(HttpStatus.OK.value(), "APIs summary retrieved", Map.of("monitoredApis", 12, "vulnerableApis", 1));
    }

    @GetMapping("/alerts")
    public ApiResponse<Map<String, Object>> getAlertsSummary() {
        return ApiResponse.success(HttpStatus.OK.value(), "Alerts summary retrieved", Map.of("totalAlerts", 15, "openAlerts", 2));
    }
}
