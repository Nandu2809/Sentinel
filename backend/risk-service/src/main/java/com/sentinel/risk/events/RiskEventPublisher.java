package com.sentinel.risk.events;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class RiskEventPublisher {
    private static final Logger log = LoggerFactory.getLogger(RiskEventPublisher.class);
    private final ApplicationEventPublisher applicationEventPublisher;

    public RiskEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public void publishRiskCalculated(RiskCalculatedEvent event) {
        log.debug("publishing_risk_calculated_event type={} id={} score={}", event.entityType(), event.entityId(), event.riskScore());
        applicationEventPublisher.publishEvent(event);
    }

    public void publishRiskUpdated(RiskUpdatedEvent event) {
        log.info("publishing_risk_updated_event type={} id={} newScore={}", event.entityType(), event.entityId(), event.newScore());
        applicationEventPublisher.publishEvent(event);
    }

    public void publishRiskThresholdExceeded(RiskThresholdExceededEvent event) {
        log.warn("publishing_risk_threshold_exceeded_event type={} id={} score={}", event.entityType(), event.entityId(), event.riskScore());
        applicationEventPublisher.publishEvent(event);
    }

    public void publishSecurityScoreUpdated(SecurityScoreUpdatedEvent event) {
        log.info("publishing_security_score_updated_event score={} heatIndex={}", event.securityScore(), event.threatHeatIndex());
        applicationEventPublisher.publishEvent(event);
    }
}
