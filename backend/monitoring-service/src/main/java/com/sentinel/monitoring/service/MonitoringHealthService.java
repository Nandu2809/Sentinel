package com.sentinel.monitoring.service;

import com.sentinel.monitoring.api.dto.ApiHealthResponse;
import com.sentinel.monitoring.domain.model.ApiHealthEntity;
import com.sentinel.monitoring.mapper.MonitoringMapper;
import com.sentinel.monitoring.repository.ApiHealthRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MonitoringHealthService {
    private final ApiHealthRepository healthRepository;
    private final MonitoringMapper mapper;

    public MonitoringHealthService(ApiHealthRepository healthRepository, MonitoringMapper mapper) {
        this.healthRepository = healthRepository;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public List<ApiHealthResponse> getHealthMetrics() {
        return healthRepository.findAll().stream()
                .map(mapper::toApiHealthResponse)
                .toList();
    }

    @Transactional
    public ApiHealthResponse recordHealthCheck(String serviceName, String status, long responseTimeMs) {
        ApiHealthEntity health = healthRepository.findByServiceName(serviceName)
                .map(existing -> {
                    existing.updateHealth(status, responseTimeMs);
                    return existing;
                })
                .orElseGet(() -> new ApiHealthEntity(serviceName, status, responseTimeMs));

        return mapper.toApiHealthResponse(healthRepository.save(health));
    }
}
