package com.sentinel.alert.consumer;

import com.sentinel.alert.domain.model.AlertSeverity;
import com.sentinel.alert.dto.CreateAlertRequest;
import com.sentinel.alert.service.AlertService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class SecurityEventConsumer {
    private static final Logger log = LoggerFactory.getLogger(SecurityEventConsumer.class);
    private final AlertService alertService;

    public SecurityEventConsumer(AlertService alertService) {
        this.alertService = alertService;
    }

    @EventListener
    public void onThreatDetected(Object event) {
        log.info("consumer_received_security_event event={}", event.getClass().getSimpleName());
    }

    public void processThreatDetected(String title, String threatType, String severityStr, double riskScore, String affectedApi, String affectedIp) {
        AlertSeverity severity;
        try {
            severity = AlertSeverity.valueOf(severityStr.toUpperCase());
        } catch (Exception e) {
            severity = AlertSeverity.HIGH;
        }

        CreateAlertRequest request = new CreateAlertRequest(
                title, "Security Threat Detected by Threat Engine", threatType,
                severity, riskScore, "threat-service", affectedApi, null, affectedIp, null, null
        );

        alertService.createAlert(request);
    }
}
