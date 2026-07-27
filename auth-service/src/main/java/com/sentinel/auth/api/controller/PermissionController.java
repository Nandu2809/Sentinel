package com.sentinel.auth.api.controller;

import com.sentinel.auth.api.dto.CreatePermissionRequest;
import com.sentinel.auth.api.dto.MessageResponse;
import com.sentinel.auth.api.dto.PermissionResponse;
import com.sentinel.auth.api.dto.UpdatePermissionRequest;
import com.sentinel.auth.service.PermissionService;
import com.sentinel.auth.service.RequestMetadata;
import com.sentinel.common.api.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/permissions")
public class PermissionController {
    private final PermissionService permissionService;

    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('PERMISSION_READ')")
    public ApiResponse<List<PermissionResponse>> findAll() {
        return ApiResponse.success(HttpStatus.OK.value(), "Permissions retrieved", permissionService.findAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('PERMISSION_READ')")
    public ApiResponse<PermissionResponse> get(@PathVariable UUID id) {
        return ApiResponse.success(HttpStatus.OK.value(), "Permission retrieved", permissionService.get(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('PERMISSION_WRITE')")
    public ResponseEntity<ApiResponse<PermissionResponse>> create(@Valid @RequestBody CreatePermissionRequest request,
                                                                  HttpServletRequest servletRequest) {
        PermissionResponse response = permissionService.create(request, RequestMetadata.from(servletRequest));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(HttpStatus.CREATED.value(), "Permission created", response));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('PERMISSION_WRITE')")
    public ApiResponse<PermissionResponse> update(@PathVariable UUID id,
                                                  @Valid @RequestBody UpdatePermissionRequest request,
                                                  HttpServletRequest servletRequest) {
        return ApiResponse.success(HttpStatus.OK.value(), "Permission updated",
                permissionService.update(id, request, RequestMetadata.from(servletRequest)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('PERMISSION_DELETE')")
    public ResponseEntity<ApiResponse<MessageResponse>> delete(@PathVariable UUID id,
                                                               HttpServletRequest servletRequest) {
        permissionService.delete(id, RequestMetadata.from(servletRequest));
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Permission deleted",
                new MessageResponse("Permission deleted")));
    }
}

