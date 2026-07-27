package com.sentinel.alert.controller;

import com.sentinel.alert.domain.model.AlertSeverity;
import com.sentinel.alert.domain.model.AlertStatus;
import com.sentinel.alert.dto.AlertResponse;
import com.sentinel.alert.dto.AlertStatisticsResponse;
import com.sentinel.alert.dto.AlertTrendResponse;
import com.sentinel.alert.dto.AssignAlertRequest;
import com.sentinel.alert.dto.CreateAlertRequest;
import com.sentinel.alert.dto.ResolveAlertRequest;
import com.sentinel.alert.service.AlertService;
import com.sentinel.common.api.ApiResponse;
import jakarta.validation.Valid;
import java.util.Map;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/alerts")
public class AlertController {
    private final AlertService alertService;

    public AlertController(AlertService alertService) {
        this.alertService = alertService;
    }

    @PostMapping
    public ApiResponse<AlertResponse> createAlert(@Valid @RequestBody CreateAlertRequest request) {
        return ApiResponse.success(HttpStatus.CREATED.value(), "Alert created", alertService.createAlert(request));
    }

    @GetMapping
    public ApiResponse<Page<AlertResponse>> getAlerts(
            @RequestParam(required = false) AlertStatus status,
            @RequestParam(required = false) AlertSeverity severity,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ApiResponse.success(HttpStatus.OK.value(), "Alerts retrieved", alertService.getAlerts(status, severity, PageRequest.of(page, size)));
    }

    @GetMapping("/{id}")
    public ApiResponse<AlertResponse> getAlertById(@PathVariable UUID id) {
        return ApiResponse.success(HttpStatus.OK.value(), "Alert details retrieved", alertService.getAlertById(id));
    }

    @PostMapping("/{id}/acknowledge")
    public ApiResponse<AlertResponse> acknowledgeAlert(@PathVariable UUID id, @RequestParam(defaultValue = "ANALYST") String analyst) {
        return ApiResponse.success(HttpStatus.OK.value(), "Alert acknowledged", alertService.acknowledgeAlert(id, analyst));
    }

    @PostMapping("/{id}/assign")
    public ApiResponse<AlertResponse> assignAlert(@PathVariable UUID id, @Valid @RequestBody AssignAlertRequest request) {
        return ApiResponse.success(HttpStatus.OK.value(), "Alert assigned", alertService.assignAlert(id, request));
    }

    @PostMapping("/{id}/resolve")
    public ApiResponse<AlertResponse> resolveAlert(@PathVariable UUID id, @Valid @RequestBody ResolveAlertRequest request) {
        return ApiResponse.success(HttpStatus.OK.value(), "Alert resolved", alertService.resolveAlert(id, request));
    }

    @PostMapping("/{id}/close")
    public ApiResponse<AlertResponse> closeAlert(@PathVariable UUID id, @RequestParam(defaultValue = "ANALYST") String closedBy) {
        return ApiResponse.success(HttpStatus.OK.value(), "Alert closed", alertService.closeAlert(id, closedBy));
    }

    @GetMapping("/statistics")
    public ApiResponse<AlertStatisticsResponse> getStatistics() {
        return ApiResponse.success(HttpStatus.OK.value(), "Alert statistics retrieved", alertService.getStatistics());
    }

    @GetMapping("/trends")
    public ApiResponse<AlertTrendResponse> getTrends() {
        return ApiResponse.success(HttpStatus.OK.value(), "Alert trends retrieved", alertService.getTrends());
    }

    @GetMapping("/severity-distribution")
    public ApiResponse<Map<String, Long>> getSeverityDistribution() {
        return ApiResponse.success(HttpStatus.OK.value(), "Severity distribution retrieved", alertService.getStatistics().severityDistribution());
    }
}
