package com.sentinel.monitoring.api.controller;

import com.sentinel.common.api.ApiResponse;
import com.sentinel.monitoring.api.dto.ApiMetricsSummaryResponse;
import com.sentinel.monitoring.service.MetricsAggregationService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/metrics")
public class MetricsController {
    private final MetricsAggregationService metricsAggregationService;

    public MetricsController(MetricsAggregationService metricsAggregationService) {
        this.metricsAggregationService = metricsAggregationService;
    }

    @GetMapping
    public ApiResponse<ApiMetricsSummaryResponse> getMetricsSummary(@RequestParam(required = false, defaultValue = "24") Integer hours) {
        return ApiResponse.success(HttpStatus.OK.value(), "API metrics summary retrieved",
                metricsAggregationService.getMetricsSummary(hours));
    }
}
