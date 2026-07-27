package com.sentinel.risk.controller;

import com.sentinel.common.api.ApiResponse;
import com.sentinel.risk.dto.SecurityScoreResponse;
import com.sentinel.risk.intelligence.OrganizationScoreService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/security-score")
public class SecurityScoreController {
    private final OrganizationScoreService organizationScoreService;

    public SecurityScoreController(OrganizationScoreService organizationScoreService) {
        this.organizationScoreService = organizationScoreService;
    }

    @GetMapping
    public ApiResponse<SecurityScoreResponse> getOrganizationSecurityScore() {
        return ApiResponse.success(HttpStatus.OK.value(), "Organization security score retrieved", organizationScoreService.getLatestOrganizationScore());
    }

    @PostMapping("/recalculate")
    public ApiResponse<SecurityScoreResponse> recalculateOrganizationSecurityScore() {
        return ApiResponse.success(HttpStatus.OK.value(), "Organization security score recalculated", organizationScoreService.calculateAndRecordOrganizationScore());
    }
}
