package com.sentinel.alert.service;

import com.sentinel.alert.domain.entity.AlertAssignmentEntity;
import com.sentinel.alert.domain.entity.AlertEntity;
import com.sentinel.alert.domain.entity.AlertHistoryEntity;
import com.sentinel.alert.domain.model.AlertSeverity;
import com.sentinel.alert.domain.model.AlertStatus;
import com.sentinel.alert.domain.model.NotificationChannel;
import com.sentinel.alert.dto.AlertResponse;
import com.sentinel.alert.dto.AlertStatisticsResponse;
import com.sentinel.alert.dto.AlertTrendResponse;
import com.sentinel.alert.dto.AssignAlertRequest;
import com.sentinel.alert.dto.CreateAlertRequest;
import com.sentinel.alert.dto.ResolveAlertRequest;
import com.sentinel.alert.events.AlertAcknowledgedEvent;
import com.sentinel.alert.events.AlertCreatedEvent;
import com.sentinel.alert.events.AlertResolvedEvent;
import com.sentinel.alert.mapper.AlertMapper;
import com.sentinel.alert.notification.service.NotificationChannelDispatcher;
import com.sentinel.alert.producer.AlertEventProducer;
import com.sentinel.alert.repository.AlertAssignmentRepository;
import com.sentinel.alert.repository.AlertHistoryRepository;
import com.sentinel.alert.repository.AlertRepository;
import com.sentinel.common.exception.BusinessException;
import com.sentinel.common.exception.ErrorCode;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AlertService {
    private final AlertRepository alertRepository;
    private final AlertHistoryRepository historyRepository;
    private final AlertAssignmentRepository assignmentRepository;
    private final NotificationChannelDispatcher dispatcher;
    private final AlertEventProducer producer;
    private final AlertMapper mapper;

    public AlertService(AlertRepository alertRepository,
                        AlertHistoryRepository historyRepository,
                        AlertAssignmentRepository assignmentRepository,
                        NotificationChannelDispatcher dispatcher,
                        AlertEventProducer producer,
                        AlertMapper mapper) {
        this.alertRepository = alertRepository;
        this.historyRepository = historyRepository;
        this.assignmentRepository = assignmentRepository;
        this.dispatcher = dispatcher;
        this.producer = producer;
        this.mapper = mapper;
    }

    @Transactional
    public AlertResponse createAlert(CreateAlertRequest request) {
        String code = "ALT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        double riskScore = request.riskScore() != null ? request.riskScore() : 50.0;

        AlertEntity entity = new AlertEntity(
                code, request.title(), request.description(), request.threatType(),
                request.severity(), riskScore, request.sourceService(),
                request.affectedApi(), request.affectedUser(), request.affectedIp(),
                request.correlationId(), request.evidenceJson()
        );

        AlertEntity saved = alertRepository.save(entity);

        historyRepository.save(new AlertHistoryEntity(
                saved, AlertStatus.NEW, AlertStatus.NEW, "SYSTEM", "Alert Automatically Ingested"
        ));

        dispatcher.dispatch(saved, NotificationChannel.WEBSOCKET, "ALL");
        if (saved.getSeverity() == AlertSeverity.CRITICAL || saved.getSeverity() == AlertSeverity.HIGH) {
            dispatcher.dispatch(saved, NotificationChannel.EMAIL, "soc-alerts@sentinel.security");
        }

        producer.publishAlertCreated(new AlertCreatedEvent(
                saved.getId(), saved.getAlertCode(), saved.getTitle(),
                saved.getSeverity(), saved.getRiskScore(), saved.getSourceService()
        ));

        return mapper.toAlertResponse(saved);
    }

    @Transactional(readOnly = true)
    public Page<AlertResponse> getAlerts(AlertStatus status, AlertSeverity severity, Pageable pageable) {
        if (status != null) {
            return alertRepository.findByStatus(status, pageable).map(mapper::toAlertResponse);
        }
        if (severity != null) {
            return alertRepository.findBySeverity(severity, pageable).map(mapper::toAlertResponse);
        }
        return alertRepository.findAll(pageable).map(mapper::toAlertResponse);
    }

    @Transactional(readOnly = true)
    public AlertResponse getAlertById(UUID id) {
        AlertEntity entity = alertRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "Alert not found: " + id));
        return mapper.toAlertResponse(entity);
    }

    @Transactional
    public AlertResponse acknowledgeAlert(UUID id, String analyst) {
        AlertEntity entity = alertRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "Alert not found: " + id));

        AlertStatus prev = entity.getStatus();
        entity.updateStatus(AlertStatus.ACKNOWLEDGED, null);
        if (entity.getAssignedAnalyst() == null) {
            entity.assignAnalyst(analyst);
        }

        AlertEntity saved = alertRepository.save(entity);

        historyRepository.save(new AlertHistoryEntity(
                saved, prev, AlertStatus.ACKNOWLEDGED, analyst, "Alert Acknowledged by SOC Analyst"
        ));

        producer.publishAlertAcknowledged(new AlertAcknowledgedEvent(saved.getId(), saved.getAlertCode(), analyst));
        return mapper.toAlertResponse(saved);
    }

    @Transactional
    public AlertResponse assignAlert(UUID id, AssignAlertRequest request) {
        AlertEntity entity = alertRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "Alert not found: " + id));

        String prevAnalyst = entity.getAssignedAnalyst();
        entity.assignAnalyst(request.analyst());
        if (entity.getStatus() == AlertStatus.NEW) {
            entity.updateStatus(AlertStatus.INVESTIGATING, null);
        }

        AlertEntity saved = alertRepository.save(entity);

        assignmentRepository.save(new AlertAssignmentEntity(
                saved, prevAnalyst, request.analyst(), request.assignedBy()
        ));

        return mapper.toAlertResponse(saved);
    }

    @Transactional
    public AlertResponse resolveAlert(UUID id, ResolveAlertRequest request) {
        AlertEntity entity = alertRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "Alert not found: " + id));

        AlertStatus prev = entity.getStatus();
        entity.updateStatus(request.status(), request.resolutionNotes());
        AlertEntity saved = alertRepository.save(entity);

        historyRepository.save(new AlertHistoryEntity(
                saved, prev, request.status(), request.resolvedBy(), request.resolutionNotes()
        ));

        producer.publishAlertResolved(new AlertResolvedEvent(saved.getId(), saved.getAlertCode(), request.resolvedBy(), request.resolutionNotes()));
        return mapper.toAlertResponse(saved);
    }

    @Transactional
    public AlertResponse closeAlert(UUID id, String closedBy) {
        AlertEntity entity = alertRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "Alert not found: " + id));

        AlertStatus prev = entity.getStatus();
        entity.updateStatus(AlertStatus.CLOSED, "Alert closed");
        AlertEntity saved = alertRepository.save(entity);

        historyRepository.save(new AlertHistoryEntity(
                saved, prev, AlertStatus.CLOSED, closedBy, "Alert Closed"
        ));

        return mapper.toAlertResponse(saved);
    }

    @Transactional(readOnly = true)
    public AlertStatisticsResponse getStatistics() {
        long total = alertRepository.count();
        long newAlerts = alertRepository.countBySeverityAndStatus(AlertSeverity.HIGH, AlertStatus.NEW) +
                alertRepository.countBySeverityAndStatus(AlertSeverity.CRITICAL, AlertStatus.NEW);
        long openAlerts = alertRepository.findByStatus(AlertStatus.NEW, Pageable.unpaged()).getTotalElements() +
                alertRepository.findByStatus(AlertStatus.ACKNOWLEDGED, Pageable.unpaged()).getTotalElements() +
                alertRepository.findByStatus(AlertStatus.INVESTIGATING, Pageable.unpaged()).getTotalElements();
        long resolvedAlerts = alertRepository.findByStatus(AlertStatus.RESOLVED, Pageable.unpaged()).getTotalElements();
        long criticalAlerts = alertRepository.findBySeverity(AlertSeverity.CRITICAL, Pageable.unpaged()).getTotalElements();

        Map<String, Long> severityDist = alertRepository.countAlertsBySeverity().stream()
                .collect(Collectors.toMap(r -> r[0].toString(), r -> (Long) r[1]));

        Map<String, Long> statusDist = alertRepository.countAlertsByStatus().stream()
                .collect(Collectors.toMap(r -> r[0].toString(), r -> (Long) r[1]));

        return new AlertStatisticsResponse(total, newAlerts, openAlerts, resolvedAlerts, criticalAlerts, severityDist, statusDist);
    }

    @Transactional(readOnly = true)
    public AlertTrendResponse getTrends() {
        Map<String, Long> statusDist = alertRepository.countAlertsByStatus().stream()
                .collect(Collectors.toMap(r -> r[0].toString(), r -> (Long) r[1]));

        List<Map<String, Object>> trendData = statusDist.entrySet().stream()
                .map(e -> Map.<String, Object>of("status", e.getKey(), "count", e.getValue()))
                .toList();

        return new AlertTrendResponse("24h", trendData);
    }
}
