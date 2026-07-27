package com.sentinel.monitoring.api.controller;

import com.sentinel.common.api.ApiResponse;
import com.sentinel.monitoring.api.dto.LogRequestDto;
import com.sentinel.monitoring.api.dto.RequestLogResponse;
import com.sentinel.monitoring.service.TelemetryIngestionService;
import jakarta.validation.Valid;
import java.time.Instant;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/requests")
public class RequestLogController {
    private final TelemetryIngestionService telemetryIngestionService;

    public RequestLogController(TelemetryIngestionService telemetryIngestionService) {
        this.telemetryIngestionService = telemetryIngestionService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<RequestLogResponse>> logRequest(@Valid @RequestBody LogRequestDto request) {
        RequestLogResponse response = telemetryIngestionService.logRequest(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(HttpStatus.CREATED.value(), "Request telemetry logged", response));
    }

    @GetMapping
    public ApiResponse<Page<RequestLogResponse>> searchRequests(
            @RequestParam(required = false) UUID apiId,
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) String clientIp,
            @RequestParam(required = false) Integer statusCode,
            @RequestParam(required = false) Instant startTime,
            @RequestParam(required = false) Instant endTime,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "timestamp"));
        Page<RequestLogResponse> results = telemetryIngestionService.searchRequests(
                apiId, userId, clientIp, statusCode, startTime, endTime, pageable);
        return ApiResponse.success(HttpStatus.OK.value(), "Request telemetry retrieved", results);
    }
}
