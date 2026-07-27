package com.sentinel.monitoring.api.controller;

import com.sentinel.common.api.ApiResponse;
import com.sentinel.monitoring.api.dto.ApiHealthResponse;
import com.sentinel.monitoring.service.MonitoringHealthService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/health")
public class MonitoringHealthController {
    private final MonitoringHealthService monitoringHealthService;

    public MonitoringHealthController(MonitoringHealthService monitoringHealthService) {
        this.monitoringHealthService = monitoringHealthService;
    }

    @GetMapping
    public ApiResponse<List<ApiHealthResponse>> getHealthMetrics() {
        return ApiResponse.success(HttpStatus.OK.value(), "Service health metrics retrieved",
                monitoringHealthService.getHealthMetrics());
    }

    @PostMapping
    public ApiResponse<ApiHealthResponse> recordHealthCheck(
            @RequestParam String serviceName,
            @RequestParam(defaultValue = "UP") String status,
            @RequestParam(defaultValue = "0") long responseTimeMs
    ) {
        return ApiResponse.success(HttpStatus.OK.value(), "Health check recorded",
                monitoringHealthService.recordHealthCheck(serviceName, status, responseTimeMs));
    }
}
