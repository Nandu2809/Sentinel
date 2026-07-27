package com.sentinel.threat.incident;

import com.sentinel.common.exception.BusinessException;
import com.sentinel.common.exception.ErrorCode;
import com.sentinel.threat.domain.entity.IncidentEntity;
import com.sentinel.threat.domain.entity.ThreatEventEntity;
import com.sentinel.threat.domain.model.IncidentStatus;
import com.sentinel.threat.domain.model.ThreatSeverity;
import com.sentinel.threat.dto.IncidentResponse;
import com.sentinel.threat.events.IncidentCreatedEvent;
import com.sentinel.threat.events.ThreatEventPublisher;
import com.sentinel.threat.mapper.ThreatMapper;
import com.sentinel.threat.repository.IncidentRepository;
import java.time.Instant;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class IncidentManagerService {
    private final IncidentRepository incidentRepository;
    private final ThreatMapper mapper;
    private final ThreatEventPublisher eventPublisher;

    public IncidentManagerService(IncidentRepository incidentRepository, ThreatMapper mapper, ThreatEventPublisher eventPublisher) {
        this.incidentRepository = incidentRepository;
        this.mapper = mapper;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public void processThreatForIncident(ThreatEventEntity threatEvent) {
        if (threatEvent.getSeverity() == ThreatSeverity.HIGH || threatEvent.getSeverity() == ThreatSeverity.CRITICAL || threatEvent.getRiskScore() >= 65.0) {
            String incidentCode = "INC-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            String evidence = String.format("{\"threatCode\":\"%s\",\"requestId\":\"%s\",\"clientIp\":\"%s\",\"matchedRule\":\"%s\"}",
                    threatEvent.getThreatCode(), threatEvent.getRequestId(), threatEvent.getClientIp(),
                    threatEvent.getMatchedRule() != null ? threatEvent.getMatchedRule().getRuleCode() : "GENERIC");

            IncidentEntity incident = new IncidentEntity(
                    incidentCode,
                    threatEvent.getThreatType(),
                    threatEvent.getSeverity(),
                    threatEvent.getRiskScore(),
                    threatEvent.getEndpoint(),
                    threatEvent.getUserId(),
                    evidence,
                    IncidentStatus.OPEN,
                    threatEvent.getRecommendation()
            );

            IncidentEntity saved = incidentRepository.save(incident);

            eventPublisher.publishIncidentCreated(new IncidentCreatedEvent(
                    saved.getIncidentCode(),
                    saved.getThreatType(),
                    saved.getSeverity(),
                    saved.getRiskScore(),
                    saved.getAffectedEndpoint(),
                    Instant.now()
            ));
        }
    }

    @Transactional(readOnly = true)
    public Page<IncidentResponse> findAll(Pageable pageable) {
        return incidentRepository.findAll(pageable).map(mapper::toIncidentResponse);
    }

    @Transactional(readOnly = true)
    public IncidentResponse get(UUID id) {
        return mapper.toIncidentResponse(findIncident(id));
    }

    @Transactional
    public IncidentResponse updateStatus(UUID id, IncidentStatus status) {
        IncidentEntity entity = findIncident(id);
        entity.updateStatus(status);
        return mapper.toIncidentResponse(entity);
    }

    public IncidentEntity findIncident(UUID id) {
        return incidentRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "Incident record not found"));
    }
}
