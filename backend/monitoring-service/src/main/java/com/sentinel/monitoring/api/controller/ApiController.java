package com.sentinel.monitoring.api.controller;

import com.sentinel.common.api.ApiResponse;
import com.sentinel.monitoring.api.dto.ApiResponseDto;
import com.sentinel.monitoring.api.dto.MessageResponse;
import com.sentinel.monitoring.api.dto.RegisterApiRequest;
import com.sentinel.monitoring.api.dto.UpdateApiRequest;
import com.sentinel.monitoring.service.ApiInventoryService;
import com.sentinel.monitoring.service.RequestMetadata;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/apis")
public class ApiController {
    private final ApiInventoryService apiInventoryService;

    public ApiController(ApiInventoryService apiInventoryService) {
        this.apiInventoryService = apiInventoryService;
    }

    @GetMapping
    public ApiResponse<List<ApiResponseDto>> findAll() {
        return ApiResponse.success(HttpStatus.OK.value(), "APIs retrieved", apiInventoryService.findAll());
    }

    @GetMapping("/{id}")
    public ApiResponse<ApiResponseDto> get(@PathVariable UUID id) {
        return ApiResponse.success(HttpStatus.OK.value(), "API retrieved", apiInventoryService.get(id));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ApiResponseDto>> register(@Valid @RequestBody RegisterApiRequest request,
                                                                HttpServletRequest servletRequest) {
        ApiResponseDto response = apiInventoryService.create(request, RequestMetadata.from(servletRequest));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(HttpStatus.CREATED.value(), "API registered", response));
    }

    @PutMapping("/{id}")
    public ApiResponse<ApiResponseDto> update(@PathVariable UUID id, @Valid @RequestBody UpdateApiRequest request,
                                              HttpServletRequest servletRequest) {
        return ApiResponse.success(HttpStatus.OK.value(), "API updated",
                apiInventoryService.update(id, request, RequestMetadata.from(servletRequest)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<MessageResponse>> delete(@PathVariable UUID id, HttpServletRequest servletRequest) {
        apiInventoryService.delete(id, RequestMetadata.from(servletRequest));
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "API unregistered",
                new MessageResponse("API unregistered from inventory")));
    }
}
