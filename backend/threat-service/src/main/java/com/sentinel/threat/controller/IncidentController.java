package com.sentinel.threat.controller;

import com.sentinel.common.api.ApiResponse;
import com.sentinel.threat.domain.model.IncidentStatus;
import com.sentinel.threat.dto.IncidentResponse;
import com.sentinel.threat.incident.IncidentManagerService;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/incidents")
public class IncidentController {
    private final IncidentManagerService incidentManagerService;

    public IncidentController(IncidentManagerService incidentManagerService) {
        this.incidentManagerService = incidentManagerService;
    }

    @GetMapping
    public ApiResponse<Page<IncidentResponse>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return ApiResponse.success(HttpStatus.OK.value(), "Incidents retrieved", incidentManagerService.findAll(pageable));
    }

    @GetMapping("/{id}")
    public ApiResponse<IncidentResponse> get(@PathVariable UUID id) {
        return ApiResponse.success(HttpStatus.OK.value(), "Incident retrieved", incidentManagerService.get(id));
    }

    @PutMapping("/{id}/status")
    public ApiResponse<IncidentResponse> updateStatus(@PathVariable UUID id, @RequestParam IncidentStatus status) {
        return ApiResponse.success(HttpStatus.OK.value(), "Incident status updated", incidentManagerService.updateStatus(id, status));
    }
}
