package com.sentinel.monitoring.service;

import com.sentinel.monitoring.api.dto.LogRequestDto;
import com.sentinel.monitoring.api.dto.RequestLogResponse;
import com.sentinel.monitoring.domain.model.ApiEntity;
import com.sentinel.monitoring.domain.model.ApiErrorLogEntity;
import com.sentinel.monitoring.domain.model.ApiRequestEntity;
import com.sentinel.monitoring.event.ApiErrorOccurredEvent;
import com.sentinel.monitoring.event.ApiRequestReceivedEvent;
import com.sentinel.monitoring.event.ApiResponseCompletedEvent;
import com.sentinel.monitoring.event.MonitoringEventPublisher;
import com.sentinel.monitoring.mapper.MonitoringMapper;
import com.sentinel.monitoring.repository.ApiErrorLogRepository;
import com.sentinel.monitoring.repository.ApiRepository;
import com.sentinel.monitoring.repository.ApiRequestRepository;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TelemetryIngestionService {
    private final ApiRequestRepository requestRepository;
    private final ApiRepository apiRepository;
    private final ApiErrorLogRepository errorLogRepository;
    private final MonitoringMapper mapper;
    private final MonitoringEventPublisher eventPublisher;

    public TelemetryIngestionService(ApiRequestRepository requestRepository, ApiRepository apiRepository,
                                     ApiErrorLogRepository errorLogRepository, MonitoringMapper mapper,
                                     MonitoringEventPublisher eventPublisher) {
        this.requestRepository = requestRepository;
        this.apiRepository = apiRepository;
        this.errorLogRepository = errorLogRepository;
        this.mapper = mapper;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public RequestLogResponse logRequest(LogRequestDto dto) {
        Optional<ApiEntity> matchedApi = apiRepository.findByPathPatternAndMethod(dto.uri(), dto.httpMethod());
        ApiEntity api = matchedApi.orElse(null);

        long reqSize = dto.requestSize() != null ? dto.requestSize() : 0L;
        long respSize = dto.responseSize() != null ? dto.responseSize() : 0L;
        Instant eventTime = dto.timestamp() != null ? dto.timestamp() : Instant.now();

        ApiRequestEntity requestEntity = new ApiRequestEntity(
                api,
                dto.requestId(),
                dto.correlationId(),
                dto.clientIp(),
                dto.userAgent(),
                dto.httpMethod(),
                dto.uri(),
                dto.statusCode(),
                dto.latencyMs(),
                reqSize,
                respSize,
                dto.userId(),
                eventTime
        );

        ApiRequestEntity saved = requestRepository.save(requestEntity);

        if (dto.statusCode() >= 400) {
            String errorMsg = "HTTP " + dto.statusCode() + " response recorded for " + dto.uri();
            ApiErrorLogEntity errorLog = new ApiErrorLogEntity(
                    dto.requestId(), api, dto.statusCode(), errorMsg, null, eventTime
            );
            errorLogRepository.save(errorLog);

            eventPublisher.publishErrorOccurred(new ApiErrorOccurredEvent(
                    dto.requestId(), dto.correlationId(), dto.statusCode(), errorMsg, dto.uri(), eventTime
            ));
        }

        eventPublisher.publishRequestReceived(new ApiRequestReceivedEvent(
                dto.requestId(), dto.correlationId(), dto.clientIp(), dto.httpMethod(), dto.uri(), dto.userId(), eventTime
        ));

        eventPublisher.publishResponseCompleted(new ApiResponseCompletedEvent(
                dto.requestId(), dto.correlationId(), dto.statusCode(), dto.latencyMs(), respSize, eventTime
        ));

        return mapper.toRequestLogResponse(saved);
    }

    @Transactional(readOnly = true)
    public Page<RequestLogResponse> searchRequests(UUID apiId, String userId, String clientIp,
                                                  Integer statusCode, Instant startTime, Instant endTime,
                                                  Pageable pageable) {
        return requestRepository.searchRequests(apiId, userId, clientIp, statusCode, startTime, endTime, pageable)
                .map(mapper::toRequestLogResponse);
    }
}
