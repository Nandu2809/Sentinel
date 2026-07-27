package com.sentinel.report.controller;

import com.sentinel.common.api.ApiResponse;
import com.sentinel.report.domain.entity.ThreatStatisticsEntity;
import com.sentinel.report.dto.AnalyticsTrendResponse;
import com.sentinel.report.service.SecurityAnalyticsService;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/analytics")
public class AnalyticsController {
    private final SecurityAnalyticsService analyticsService;

    public AnalyticsController(SecurityAnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/security-score")
    public ApiResponse<AnalyticsTrendResponse> getSecurityScoreAnalytics() {
        return ApiResponse.success(HttpStatus.OK.value(), "Security score analytics retrieved", analyticsService.getSecurityScoreTrends());
    }

    @GetMapping("/trends")
    public ApiResponse<AnalyticsTrendResponse> getAnalyticsTrends() {
        return ApiResponse.success(HttpStatus.OK.value(), "Analytics trends retrieved", analyticsService.getSecurityScoreTrends());
    }

    @GetMapping("/top-threats")
    public ApiResponse<List<ThreatStatisticsEntity>> getTopThreats() {
        return ApiResponse.success(HttpStatus.OK.value(), "Top threats retrieved", analyticsService.getTopThreats());
    }

    @GetMapping("/top-risk-assets")
    public ApiResponse<List<Map<String, Object>>> getTopRiskAssets() {
        return ApiResponse.success(HttpStatus.OK.value(), "Top risk assets retrieved", analyticsService.getTopRiskAssets());
    }
}
