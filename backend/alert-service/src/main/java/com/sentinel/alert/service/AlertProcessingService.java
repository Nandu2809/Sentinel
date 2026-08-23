package com.sentinel.alert.service;

import com.sentinel.alert.domain.entity.AlertEntity;
import com.sentinel.alert.domain.model.AlertSeverity;
import com.sentinel.alert.domain.model.AlertStatus;
import com.sentinel.alert.incident.service.IncidentService;
import com.sentinel.alert.repository.AlertRepository;
import com.sentinel.common.events.AlertEventEnvelope;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AlertProcessingService {
    private static final Logger log = LoggerFactory.getLogger(AlertProcessingService.class);

    private final AlertRepository alertRepository;
    private final EmailNotificationService emailNotificationService;
    private final IncidentService incidentService;

    public AlertProcessingService(AlertRepository alertRepository,
                                  EmailNotificationService emailNotificationService,
                                  IncidentService incidentService) {
        this.alertRepository = alertRepository;
        this.emailNotificationService = emailNotificationService;
        this.incidentService = incidentService;
    }

    @Transactional
    public void processAlert(AlertEventEnvelope alertEvent) {
        if (alertEvent == null) {
            return;
        }

        AlertSeverity severity = parseSeverity(alertEvent.severity());

        AlertEntity entity = new AlertEntity(
                alertEvent.eventId(),
                alertEvent.userId(),
                alertEvent.alertType(),
                severity,
                alertEvent.riskScore() != null ? alertEvent.riskScore().doubleValue() : 0.0,
                alertEvent.message(),
                AlertStatus.OPEN,
                alertEvent.timestamp() != null ? alertEvent.timestamp() : Instant.now()
        );

        AlertEntity saved = alertRepository.save(entity);

        log.info("ALERT_PROCESSED alertId={} alertType={} severity={} riskScore={}",
                saved.getId(), saved.getAlertType(), saved.getSeverity(), saved.getRiskScore());

        emailNotificationService.sendAlertEmail(alertEvent);

        // Automatically trigger Incident Creation if CRITICAL or HIGH severity
        if (severity == AlertSeverity.CRITICAL || severity == AlertSeverity.HIGH) {
            incidentService.createIncidentFromAlert(saved);
        }
    }

    private AlertSeverity parseSeverity(String severityStr) {
        if (severityStr == null) {
            return AlertSeverity.HIGH;
        }
        try {
            return AlertSeverity.valueOf(severityStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            return AlertSeverity.HIGH;
        }
    }
}

