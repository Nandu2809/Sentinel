package com.sentinel.auth.api.controller;

import com.sentinel.auth.api.dto.CreateUserRequest;
import com.sentinel.auth.api.dto.MessageResponse;
import com.sentinel.auth.api.dto.UpdateUserRequest;
import com.sentinel.auth.api.dto.UserResponse;
import com.sentinel.auth.service.RequestMetadata;
import com.sentinel.auth.service.UserAdministrationService;
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
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserAdministrationService userAdministrationService;

    public UserController(UserAdministrationService userAdministrationService) {
        this.userAdministrationService = userAdministrationService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('USER_READ')")
    public ApiResponse<List<UserResponse>> findAll() {
        return ApiResponse.success(HttpStatus.OK.value(), "Users retrieved", userAdministrationService.findAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('USER_READ')")
    public ApiResponse<UserResponse> get(@PathVariable UUID id) {
        return ApiResponse.success(HttpStatus.OK.value(), "User retrieved", userAdministrationService.get(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('USER_WRITE')")
    public ResponseEntity<ApiResponse<UserResponse>> create(@Valid @RequestBody CreateUserRequest request,
                                                            HttpServletRequest servletRequest) {
        UserResponse response = userAdministrationService.create(request, RequestMetadata.from(servletRequest));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(HttpStatus.CREATED.value(), "User created", response));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('USER_WRITE')")
    public ApiResponse<UserResponse> update(@PathVariable UUID id, @Valid @RequestBody UpdateUserRequest request,
                                            HttpServletRequest servletRequest) {
        return ApiResponse.success(HttpStatus.OK.value(), "User updated",
                userAdministrationService.update(id, request, RequestMetadata.from(servletRequest)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('USER_DELETE')")
    public ResponseEntity<ApiResponse<MessageResponse>> delete(@PathVariable UUID id, HttpServletRequest servletRequest) {
        userAdministrationService.delete(id, RequestMetadata.from(servletRequest));
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "User deleted",
                new MessageResponse("User deleted")));
    }
}

