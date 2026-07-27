package com.sentinel.threat.controller;

import com.sentinel.common.api.ApiResponse;
import com.sentinel.threat.dto.ThreatStatisticsResponse;
import com.sentinel.threat.service.ThreatStatisticsService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/statistics")
public class ThreatStatisticsController {
    private final ThreatStatisticsService threatStatisticsService;

    public ThreatStatisticsController(ThreatStatisticsService threatStatisticsService) {
        this.threatStatisticsService = threatStatisticsService;
    }

    @GetMapping
    public ApiResponse<ThreatStatisticsResponse> getStatistics(@RequestParam(required = false, defaultValue = "24") Integer hours) {
        return ApiResponse.success(HttpStatus.OK.value(), "Threat statistics retrieved", threatStatisticsService.getStatistics(hours));
    }
}
