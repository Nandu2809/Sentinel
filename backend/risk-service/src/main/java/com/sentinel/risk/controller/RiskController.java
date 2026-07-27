package com.sentinel.risk.controller;

import com.sentinel.common.api.ApiResponse;
import com.sentinel.risk.analytics.RiskAnalyticsService;
import com.sentinel.risk.domain.entity.EndpointRiskEntity;
import com.sentinel.risk.domain.entity.IpRiskEntity;
import com.sentinel.risk.domain.entity.RiskHistoryEntity;
import com.sentinel.risk.domain.entity.UserRiskEntity;
import com.sentinel.risk.dto.CalculateRiskRequest;
import com.sentinel.risk.dto.RiskProfileResponse;
import com.sentinel.risk.dto.RiskScoreResponse;
import com.sentinel.risk.dto.RiskTrendResponse;
import com.sentinel.risk.intelligence.SecurityIntelligenceService;
import com.sentinel.risk.service.RiskEvaluationService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/risk")
public class RiskController {
    private final RiskEvaluationService riskEvaluationService;
    private final SecurityIntelligenceService intelligenceService;
    private final RiskAnalyticsService analyticsService;

    public RiskController(RiskEvaluationService riskEvaluationService,
                          SecurityIntelligenceService intelligenceService,
                          RiskAnalyticsService analyticsService) {
        this.riskEvaluationService = riskEvaluationService;
        this.intelligenceService = intelligenceService;
        this.analyticsService = analyticsService;
    }

    @PostMapping("/calculate")
    public ApiResponse<RiskScoreResponse> calculateRisk(@Valid @RequestBody CalculateRiskRequest request) {
        return ApiResponse.success(HttpStatus.OK.value(), "Risk calculation completed", riskEvaluationService.evaluateRisk(request));
    }

    @GetMapping
    public ApiResponse<List<RiskProfileResponse>> getProfiles() {
        return ApiResponse.success(HttpStatus.OK.value(), "Risk profiles retrieved", riskEvaluationService.getAllProfiles());
    }

    @GetMapping("/users")
    public ApiResponse<List<UserRiskEntity>> getTopRiskUsers() {
        return ApiResponse.success(HttpStatus.OK.value(), "Top risk users retrieved", intelligenceService.getTopRiskUsers());
    }

    @GetMapping("/apis")
    public ApiResponse<List<EndpointRiskEntity>> getTopRiskApis() {
        return ApiResponse.success(HttpStatus.OK.value(), "Top risk APIs retrieved", intelligenceService.getTopRiskApis());
    }

    @GetMapping("/ip")
    public ApiResponse<List<IpRiskEntity>> getTopRiskIps() {
        return ApiResponse.success(HttpStatus.OK.value(), "Top risk IPs retrieved", intelligenceService.getTopRiskIps());
    }

    @GetMapping("/history")
    public ApiResponse<List<RiskHistoryEntity>> getHistory() {
        return ApiResponse.success(HttpStatus.OK.value(), "Risk history retrieved", analyticsService.getRecentHistory());
    }

    @GetMapping("/trends")
    public ApiResponse<RiskTrendResponse> getTrends(@RequestParam(required = false, defaultValue = "24") Integer hours) {
        return ApiResponse.success(HttpStatus.OK.value(), "Risk trends retrieved", analyticsService.getRiskTrends(hours));
    }
}
