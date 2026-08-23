package com.sentinel.alert.incident.controller;

import com.sentinel.alert.incident.dto.ThreatHuntingQueryRequest;
import com.sentinel.alert.incident.dto.ThreatHuntingSearchResponse;
import com.sentinel.alert.incident.service.ThreatHuntingService;
import com.sentinel.common.api.ApiResponse;
import java.time.Instant;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/threat-hunting")
public class ThreatHuntingController {
    private final ThreatHuntingService threatHuntingService;

    public ThreatHuntingController(ThreatHuntingService threatHuntingService) {
        this.threatHuntingService = threatHuntingService;
    }

    @GetMapping("/search")
    public ApiResponse<List<ThreatHuntingSearchResponse>> search(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String ipAddress,
            @RequestParam(required = false) String device,
            @RequestParam(required = false) String eventType,
            @RequestParam(required = false) Double minRiskScore,
            @RequestParam(required = false) Double maxRiskScore,
            @RequestParam(required = false) Instant startDate,
            @RequestParam(required = false) Instant endDate
    ) {
        ThreatHuntingQueryRequest query = new ThreatHuntingQueryRequest(
                username, email, ipAddress, device, eventType, minRiskScore, maxRiskScore, startDate, endDate
        );
        return ApiResponse.success(HttpStatus.OK.value(), "Threat hunting search completed", threatHuntingService.search(query));
    }
}
