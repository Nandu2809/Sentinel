package com.sentinel.auth.mapper;

import com.sentinel.auth.api.dto.PermissionResponse;
import com.sentinel.auth.api.dto.RoleResponse;
import com.sentinel.auth.api.dto.RoleSummary;
import com.sentinel.auth.api.dto.UserResponse;
import com.sentinel.auth.domain.model.PermissionEntity;
import com.sentinel.auth.domain.model.RoleEntity;
import com.sentinel.auth.domain.model.UserEntity;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class AuthMapper {
    public UserResponse toUserResponse(UserEntity user) {
        RoleEntity role = user.getRole();
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getUsername(),
                user.getFirstName(),
                user.getLastName(),
                user.getStatus(),
                user.isEmailVerified(),
                new RoleSummary(role.getId(), role.getName()),
                user.getCreatedAt(),
                user.getUpdatedAt());
    }

    public RoleResponse toRoleResponse(RoleEntity role) {
        return new RoleResponse(
                role.getId(),
                role.getName(),
                role.getDescription(),
                role.isSystemRole(),
                role.getPermissions().stream()
                        .sorted(Comparator.comparing(PermissionEntity::getName))
                        .map(this::toPermissionResponse)
                        .collect(Collectors.toCollection(LinkedHashSet::new)),
                role.getCreatedAt(),
                role.getUpdatedAt());
    }

    public PermissionResponse toPermissionResponse(PermissionEntity permission) {
        return new PermissionResponse(
                permission.getId(),
                permission.getName(),
                permission.getResource(),
                permission.getAction(),
                permission.getDescription(),
                permission.getCreatedAt(),
                permission.getUpdatedAt());
    }
}

