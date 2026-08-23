package com.sentinel.alert.incident.controller;

import com.sentinel.alert.domain.entity.AlertEntity;
import com.sentinel.alert.domain.model.AlertSeverity;
import com.sentinel.alert.incident.domain.entity.IncidentEntity;
import com.sentinel.alert.incident.domain.model.IncidentStatus;
import com.sentinel.alert.incident.dto.AddIncidentNoteRequest;
import com.sentinel.alert.incident.dto.AssignIncidentRequest;
import com.sentinel.alert.incident.dto.IncidentNoteResponse;
import com.sentinel.alert.incident.dto.IncidentResponse;
import com.sentinel.alert.incident.dto.PerformIncidentActionRequest;
import com.sentinel.alert.incident.dto.UpdateIncidentStatusRequest;
import com.sentinel.alert.incident.service.IncidentService;
import com.sentinel.common.api.ApiResponse;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/incidents")
public class IncidentController {
    private final IncidentService incidentService;

    public IncidentController(IncidentService incidentService) {
        this.incidentService = incidentService;
    }

    @GetMapping
    public ApiResponse<Page<IncidentResponse>> getIncidents(
            @RequestParam(required = false) IncidentStatus status,
            @RequestParam(required = false) AlertSeverity severity,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return ApiResponse.success(HttpStatus.OK.value(), "Incidents retrieved", incidentService.getIncidents(status, severity, pageable));
    }

    @GetMapping("/{id}")
    public ApiResponse<IncidentResponse> getIncidentById(@PathVariable UUID id) {
        return ApiResponse.success(HttpStatus.OK.value(), "Incident retrieved", incidentService.getIncidentById(id));
    }

    @PostMapping("/{id}/assign")
    public ApiResponse<IncidentResponse> assignIncident(@PathVariable UUID id, @Valid @RequestBody AssignIncidentRequest request) {
        return ApiResponse.success(HttpStatus.OK.value(), "Incident assigned successfully", incidentService.assignIncident(id, request));
    }

    @PutMapping("/{id}/status")
    public ApiResponse<IncidentResponse> updateStatus(@PathVariable UUID id, @Valid @RequestBody UpdateIncidentStatusRequest request) {
        return ApiResponse.success(HttpStatus.OK.value(), "Incident status updated", incidentService.updateIncidentStatus(id, request));
    }

    @PostMapping("/{id}/notes")
    public ApiResponse<IncidentNoteResponse> addNote(@PathVariable UUID id, @Valid @RequestBody AddIncidentNoteRequest request) {
        return ApiResponse.success(HttpStatus.CREATED.value(), "Note added to incident", incidentService.addNote(id, request));
    }

    @PostMapping("/{id}/actions")
    public ApiResponse<IncidentResponse> performAction(@PathVariable UUID id, @Valid @RequestBody PerformIncidentActionRequest request) {
        return ApiResponse.success(HttpStatus.OK.value(), "Incident action recorded", incidentService.performAction(id, request));
    }

    /**
     * Test-only endpoint for Scenario Testing (Step 24).
     * Triggers Impossible Travel Attack scenario and creates a Critical Incident.
     */
    @PostMapping("/test/trigger-impossible-travel")
    public ApiResponse<IncidentResponse> triggerImpossibleTravelTestScenario() {
        String testUser = "victim_user_" + UUID.randomUUID().toString().substring(0, 4);
        String testIp = "203.0.113.195";

        AlertEntity testAlert = new AlertEntity(
                "ALT-IMP-TRAVEL",
                "CRITICAL: Impossible Travel Attack Detected",
                "User " + testUser + " authenticated from New York, then 5 mins later from Tokyo",
                "IMPOSSIBLE_TRAVEL_ATTACK",
                AlertSeverity.CRITICAL,
                98.5,
                "RISK_SERVICE",
                "/api/v1/auth/login",
                testUser,
                testIp,
                UUID.randomUUID().toString(),
                "{\"login1\":\"NYC\",\"login2\":\"TYO\",\"timeDeltaMinutes\":5}"
        );

        IncidentEntity created = incidentService.createIncidentFromAlert(testAlert);
        return ApiResponse.success(HttpStatus.CREATED.value(), "Test impossible travel attack incident created", incidentService.getIncidentById(created.getId()));
    }
}
