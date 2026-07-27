package com.sentinel.monitoring.mapper;

import com.sentinel.monitoring.api.dto.ApiHealthResponse;
import com.sentinel.monitoring.api.dto.ApiResponseDto;
import com.sentinel.monitoring.api.dto.RequestLogResponse;
import com.sentinel.monitoring.domain.model.ApiEntity;
import com.sentinel.monitoring.domain.model.ApiHealthEntity;
import com.sentinel.monitoring.domain.model.ApiRequestEntity;
import org.springframework.stereotype.Component;

@Component
public class MonitoringMapper {

    public ApiResponseDto toApiResponseDto(ApiEntity entity) {
        if (entity == null) {
            return null;
        }
        return new ApiResponseDto(
                entity.getId(),
                entity.getName(),
                entity.getPathPattern(),
                entity.getMethod(),
                entity.isEnabled(),
                entity.getRateLimitPerMin(),
                entity.getDescription(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    public RequestLogResponse toRequestLogResponse(ApiRequestEntity entity) {
        if (entity == null) {
            return null;
        }
        return new RequestLogResponse(
                entity.getId(),
                entity.getApi() != null ? entity.getApi().getId() : null,
                entity.getApi() != null ? entity.getApi().getName() : "UNREGISTERED",
                entity.getRequestId(),
                entity.getCorrelationId(),
                entity.getClientIp(),
                entity.getUserAgent(),
                entity.getHttpMethod(),
                entity.getUri(),
                entity.getStatusCode(),
                entity.getLatencyMs(),
                entity.getRequestSize(),
                entity.getResponseSize(),
                entity.getUserId(),
                entity.getTimestamp()
        );
    }

    public ApiHealthResponse toApiHealthResponse(ApiHealthEntity entity) {
        if (entity == null) {
            return null;
        }
        return new ApiHealthResponse(
                entity.getId(),
                entity.getServiceName(),
                entity.getStatus(),
                entity.getResponseTimeMs(),
                entity.getLastCheckedAt()
        );
    }
}
