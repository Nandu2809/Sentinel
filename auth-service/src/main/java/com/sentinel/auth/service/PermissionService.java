package com.sentinel.auth.service;

import com.sentinel.auth.api.dto.CreatePermissionRequest;
import com.sentinel.auth.api.dto.PermissionResponse;
import com.sentinel.auth.api.dto.UpdatePermissionRequest;
import com.sentinel.auth.domain.model.AuditOutcome;
import com.sentinel.auth.domain.model.PermissionEntity;
import com.sentinel.auth.mapper.AuthMapper;
import com.sentinel.auth.repository.PermissionRepository;
import com.sentinel.common.exception.BusinessException;
import com.sentinel.common.exception.ErrorCode;
import com.sentinel.common.security.SecurityEventType;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PermissionService {
    private final PermissionRepository permissionRepository;
    private final AuthMapper mapper;
    private final AuditEventService auditEventService;

    public PermissionService(PermissionRepository permissionRepository, AuthMapper mapper,
                             AuditEventService auditEventService) {
        this.permissionRepository = permissionRepository;
        this.mapper = mapper;
        this.auditEventService = auditEventService;
    }

    @Transactional(readOnly = true)
    public List<PermissionResponse> findAll() {
        return permissionRepository.findAll().stream()
                .map(mapper::toPermissionResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public PermissionResponse get(UUID id) {
        return mapper.toPermissionResponse(findEntity(id));
    }

    @Transactional
    public PermissionResponse create(CreatePermissionRequest request, RequestMetadata metadata) {
        if (permissionRepository.existsByName(request.name())) {
            throw new BusinessException(ErrorCode.DUPLICATE_RESOURCE, "Permission already exists");
        }
        PermissionEntity permission = permissionRepository.save(new PermissionEntity(
                request.name(),
                request.resource(),
                request.action(),
                request.description()));
        auditEventService.record(null, SecurityEventType.PERMISSION_CREATED, AuditOutcome.SUCCESS, metadata,
                "Permission", permission.getId().toString(), "Permission created");
        return mapper.toPermissionResponse(permission);
    }

    @Transactional
    public PermissionResponse update(UUID id, UpdatePermissionRequest request, RequestMetadata metadata) {
        PermissionEntity permission = findEntity(id);
        permission.update(request.resource(), request.action(), request.description());
        auditEventService.record(null, SecurityEventType.PERMISSION_UPDATED, AuditOutcome.SUCCESS, metadata,
                "Permission", permission.getId().toString(), "Permission updated");
        return mapper.toPermissionResponse(permission);
    }

    @Transactional
    public void delete(UUID id, RequestMetadata metadata) {
        PermissionEntity permission = findEntity(id);
        permissionRepository.delete(permission);
        auditEventService.record(null, SecurityEventType.PERMISSION_DELETED, AuditOutcome.SUCCESS, metadata,
                "Permission", id.toString(), "Permission deleted");
    }

    private PermissionEntity findEntity(UUID id) {
        return permissionRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "Permission not found"));
    }
}

