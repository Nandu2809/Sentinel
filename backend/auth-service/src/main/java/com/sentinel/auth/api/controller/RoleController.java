package com.sentinel.auth.api.controller;

import com.sentinel.auth.api.dto.CreateRoleRequest;
import com.sentinel.auth.api.dto.MessageResponse;
import com.sentinel.auth.api.dto.RoleResponse;
import com.sentinel.auth.api.dto.UpdateRoleRequest;
import com.sentinel.auth.service.RequestMetadata;
import com.sentinel.auth.service.RoleService;
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
@RequestMapping("/api/v1/roles")
public class RoleController {
    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_READ')")
    public ApiResponse<List<RoleResponse>> findAll() {
        return ApiResponse.success(HttpStatus.OK.value(), "Roles retrieved", roleService.findAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_READ')")
    public ApiResponse<RoleResponse> get(@PathVariable UUID id) {
        return ApiResponse.success(HttpStatus.OK.value(), "Role retrieved", roleService.get(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_WRITE')")
    public ResponseEntity<ApiResponse<RoleResponse>> create(@Valid @RequestBody CreateRoleRequest request,
                                                            HttpServletRequest servletRequest) {
        RoleResponse response = roleService.create(request, RequestMetadata.from(servletRequest));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(HttpStatus.CREATED.value(), "Role created", response));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_WRITE')")
    public ApiResponse<RoleResponse> update(@PathVariable UUID id, @Valid @RequestBody UpdateRoleRequest request,
                                            HttpServletRequest servletRequest) {
        return ApiResponse.success(HttpStatus.OK.value(), "Role updated",
                roleService.update(id, request, RequestMetadata.from(servletRequest)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_DELETE')")
    public ResponseEntity<ApiResponse<MessageResponse>> delete(@PathVariable UUID id, HttpServletRequest servletRequest) {
        roleService.delete(id, RequestMetadata.from(servletRequest));
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Role deleted",
                new MessageResponse("Role deleted")));
    }
}

