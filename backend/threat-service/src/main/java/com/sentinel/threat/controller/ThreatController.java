package com.sentinel.threat.controller;

import com.sentinel.common.api.ApiResponse;
import com.sentinel.threat.domain.model.ThreatSeverity;
import com.sentinel.threat.domain.model.ThreatType;
import com.sentinel.threat.dto.AnalyzeRequestDto;
import com.sentinel.threat.dto.ThreatEventResponse;
import com.sentinel.threat.service.ThreatAnalysisService;
import jakarta.validation.Valid;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/threats")
public class ThreatController {
    private final ThreatAnalysisService threatAnalysisService;

    public ThreatController(ThreatAnalysisService threatAnalysisService) {
        this.threatAnalysisService = threatAnalysisService;
    }

    @PostMapping("/analyze")
    public ApiResponse<List<ThreatEventResponse>> analyzeRequest(@Valid @RequestBody AnalyzeRequestDto request) {
        List<ThreatEventResponse> threats = threatAnalysisService.analyzeRequest(request);
        return ApiResponse.success(HttpStatus.OK.value(), "Threat analysis completed", threats);
    }

    @GetMapping
    public ApiResponse<Page<ThreatEventResponse>> searchThreats(
            @RequestParam(required = false) ThreatType threatType,
            @RequestParam(required = false) ThreatSeverity severity,
            @RequestParam(required = false) String clientIp,
            @RequestParam(required = false) Instant startTime,
            @RequestParam(required = false) Instant endTime,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<ThreatEventResponse> results = threatAnalysisService.searchThreats(threatType, severity, clientIp, startTime, endTime, pageable);
        return ApiResponse.success(HttpStatus.OK.value(), "Threat events retrieved", results);
    }

    @GetMapping("/{id}")
    public ApiResponse<ThreatEventResponse> getThreat(@PathVariable UUID id) {
        return ApiResponse.success(HttpStatus.OK.value(), "Threat event retrieved", threatAnalysisService.getThreat(id));
    }
}
