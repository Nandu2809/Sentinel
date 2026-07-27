package com.sentinel.threat.controller;

import com.sentinel.common.api.ApiResponse;
import com.sentinel.threat.dto.CreateRuleRequest;
import com.sentinel.threat.dto.ThreatRuleDto;
import com.sentinel.threat.dto.UpdateRuleRequest;
import com.sentinel.threat.service.ThreatRuleService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/rules")
public class RuleController {
    private final ThreatRuleService threatRuleService;

    public RuleController(ThreatRuleService threatRuleService) {
        this.threatRuleService = threatRuleService;
    }

    @GetMapping
    public ApiResponse<List<ThreatRuleDto>> findAll() {
        return ApiResponse.success(HttpStatus.OK.value(), "Threat rules retrieved", threatRuleService.findAll());
    }

    @GetMapping("/{id}")
    public ApiResponse<ThreatRuleDto> get(@PathVariable UUID id) {
        return ApiResponse.success(HttpStatus.OK.value(), "Threat rule retrieved", threatRuleService.get(id));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ThreatRuleDto>> create(@Valid @RequestBody CreateRuleRequest request) {
        ThreatRuleDto response = threatRuleService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(HttpStatus.CREATED.value(), "Threat rule created", response));
    }

    @PutMapping("/{id}")
    public ApiResponse<ThreatRuleDto> update(@PathVariable UUID id, @Valid @RequestBody UpdateRuleRequest request) {
        return ApiResponse.success(HttpStatus.OK.value(), "Threat rule updated", threatRuleService.update(id, request));
    }
}
