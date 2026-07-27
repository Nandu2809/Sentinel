package com.sentinel.auth.service;

import com.sentinel.auth.api.dto.CreateRoleRequest;
import com.sentinel.auth.api.dto.RoleResponse;
import com.sentinel.auth.api.dto.UpdateRoleRequest;
import com.sentinel.auth.domain.model.AuditOutcome;
import com.sentinel.auth.domain.model.PermissionEntity;
import com.sentinel.auth.domain.model.RoleEntity;
import com.sentinel.auth.mapper.AuthMapper;
import com.sentinel.auth.repository.PermissionRepository;
import com.sentinel.auth.repository.RoleRepository;
import com.sentinel.common.exception.BusinessException;
import com.sentinel.common.exception.ErrorCode;
import com.sentinel.common.security.SecurityEventType;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RoleService {
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final AuthMapper mapper;
    private final AuditEventService auditEventService;

    public RoleService(RoleRepository roleRepository, PermissionRepository permissionRepository, AuthMapper mapper,
                       AuditEventService auditEventService) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
        this.mapper = mapper;
        this.auditEventService = auditEventService;
    }

    @Transactional(readOnly = true)
    public List<RoleResponse> findAll() {
        return roleRepository.findAll().stream()
                .map(mapper::toRoleResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public RoleResponse get(UUID id) {
        return mapper.toRoleResponse(findEntity(id));
    }

    @Transactional
    public RoleResponse create(CreateRoleRequest request, RequestMetadata metadata) {
        if (roleRepository.existsByName(request.name())) {
            throw new BusinessException(ErrorCode.DUPLICATE_RESOURCE, "Role already exists");
        }
        RoleEntity role = new RoleEntity(request.name(), request.description(), false);
        role.replacePermissions(resolvePermissions(request.permissionIds()));
        RoleEntity saved = roleRepository.save(role);
        auditEventService.record(null, SecurityEventType.ROLE_CREATED, AuditOutcome.SUCCESS, metadata,
                "Role", saved.getId().toString(), "Role created");
        return mapper.toRoleResponse(saved);
    }

    @Transactional
    public RoleResponse update(UUID id, UpdateRoleRequest request, RequestMetadata metadata) {
        RoleEntity role = findEntity(id);
        role.update(request.description(), resolvePermissions(request.permissionIds()));
        auditEventService.record(null, SecurityEventType.ROLE_UPDATED, AuditOutcome.SUCCESS, metadata,
                "Role", role.getId().toString(), "Role updated");
        return mapper.toRoleResponse(role);
    }

    @Transactional
    public void delete(UUID id, RequestMetadata metadata) {
        RoleEntity role = findEntity(id);
        if (role.isSystemRole()) {
            throw new BusinessException(ErrorCode.SECURITY_POLICY_VIOLATION, "System roles cannot be deleted");
        }
        roleRepository.delete(role);
        auditEventService.record(null, SecurityEventType.ROLE_DELETED, AuditOutcome.SUCCESS, metadata,
                "Role", id.toString(), "Role deleted");
    }

    private RoleEntity findEntity(UUID id) {
        return roleRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "Role not found"));
    }

    private Set<PermissionEntity> resolvePermissions(Set<UUID> permissionIds) {
        if (permissionIds == null || permissionIds.isEmpty()) {
            return Set.of();
        }
        List<PermissionEntity> permissions = permissionRepository.findByIdIn(permissionIds);
        if (permissions.size() != permissionIds.size()) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "One or more permissions were not found");
        }
        return new HashSet<>(permissions);
    }
}

