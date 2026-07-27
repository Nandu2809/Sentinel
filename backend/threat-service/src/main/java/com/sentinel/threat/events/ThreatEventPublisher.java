package com.sentinel.threat.events;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class ThreatEventPublisher {
    private static final Logger log = LoggerFactory.getLogger(ThreatEventPublisher.class);
    private final ApplicationEventPublisher applicationEventPublisher;

    public ThreatEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public void publishThreatDetected(ThreatDetectedEvent event) {
        log.warn("publishing_threat_detected_event code={} type={} score={}", event.threatCode(), event.threatType(), event.riskScore());
        applicationEventPublisher.publishEvent(event);
    }

    public void publishThreatResolved(ThreatResolvedEvent event) {
        log.info("publishing_threat_resolved_event code={}", event.threatCode());
        applicationEventPublisher.publishEvent(event);
    }

    public void publishIncidentCreated(IncidentCreatedEvent event) {
        log.warn("publishing_incident_created_event code={} type={} severity={}", event.incidentCode(), event.threatType(), event.severity());
        applicationEventPublisher.publishEvent(event);
    }

    public void publishRiskScoreCalculated(RiskScoreCalculatedEvent event) {
        log.debug("publishing_risk_score_calculated_event ip={} score={}", event.clientIp(), event.riskScore());
        applicationEventPublisher.publishEvent(event);
    }
}
