package com.sentinel.monitoring.service;

import com.sentinel.common.exception.BusinessException;
import com.sentinel.common.exception.ErrorCode;
import com.sentinel.common.security.SecurityEventType;
import com.sentinel.monitoring.api.dto.ApiResponseDto;
import com.sentinel.monitoring.api.dto.RegisterApiRequest;
import com.sentinel.monitoring.api.dto.UpdateApiRequest;
import com.sentinel.monitoring.domain.model.ApiEntity;
import com.sentinel.monitoring.domain.model.AuditOutcome;
import com.sentinel.monitoring.mapper.MonitoringMapper;
import com.sentinel.monitoring.repository.ApiRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ApiInventoryService {
    private final ApiRepository apiRepository;
    private final MonitoringMapper mapper;
    private final AuditEventService auditEventService;

    public ApiInventoryService(ApiRepository apiRepository, MonitoringMapper mapper, AuditEventService auditEventService) {
        this.apiRepository = apiRepository;
        this.mapper = mapper;
        this.auditEventService = auditEventService;
    }

    @Transactional(readOnly = true)
    public List<ApiResponseDto> findAll() {
        return apiRepository.findAll().stream()
                .map(mapper::toApiResponseDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public ApiResponseDto get(UUID id) {
        return mapper.toApiResponseDto(findApi(id));
    }

    @Transactional
    public ApiResponseDto create(RegisterApiRequest request, RequestMetadata metadata) {
        if (apiRepository.existsByNameIgnoreCase(request.name())) {
            throw new BusinessException(ErrorCode.DUPLICATE_RESOURCE, "API with this name already exists");
        }
        ApiEntity entity = new ApiEntity(
                request.name(),
                request.pathPattern(),
                request.method(),
                request.enabled(),
                request.rateLimitPerMin(),
                request.description()
        );
        ApiEntity saved = apiRepository.save(entity);
        auditEventService.record(null, SecurityEventType.USER_CREATED, AuditOutcome.SUCCESS, metadata,
                "ApiInventory", saved.getId().toString(), "Registered API in inventory: " + saved.getName());
        return mapper.toApiResponseDto(saved);
    }

    @Transactional
    public ApiResponseDto update(UUID id, UpdateApiRequest request, RequestMetadata metadata) {
        ApiEntity entity = findApi(id);
        entity.update(
                request.name(),
                request.pathPattern(),
                request.method(),
                request.enabled(),
                request.rateLimitPerMin(),
                request.description()
        );
        auditEventService.record(null, SecurityEventType.USER_UPDATED, AuditOutcome.SUCCESS, metadata,
                "ApiInventory", entity.getId().toString(), "Updated API inventory record: " + entity.getName());
        return mapper.toApiResponseDto(entity);
    }

    @Transactional
    public void delete(UUID id, RequestMetadata metadata) {
        ApiEntity entity = findApi(id);
        apiRepository.delete(entity);
        auditEventService.record(null, SecurityEventType.USER_DELETED, AuditOutcome.SUCCESS, metadata,
                "ApiInventory", id.toString(), "Unregistered API from inventory: " + entity.getName());
    }

    public ApiEntity findApi(UUID id) {
        return apiRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "API not found in inventory"));
    }
}
