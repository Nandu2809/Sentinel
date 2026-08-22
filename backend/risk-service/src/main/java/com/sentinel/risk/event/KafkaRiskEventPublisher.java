package com.sentinel.risk.event;

import com.sentinel.common.events.RiskEventEnvelope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaRiskEventPublisher {
    private static final Logger log = LoggerFactory.getLogger(KafkaRiskEventPublisher.class);

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public KafkaRiskEventPublisher(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(RiskEventEnvelope event) {
        log.info("Publishing risk_event eventId={} user={} score={} level={} decision={}",
                event.eventId(), event.email(), event.riskScore(), event.riskLevel(), event.decision());
        kafkaTemplate.send("sentinel.risk-events",
                event.userId() != null ? event.userId().toString() : event.eventId().toString(),
                event);
    }
}
