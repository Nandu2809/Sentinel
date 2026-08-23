package com.sentinel.risk.event;

import com.sentinel.common.events.AlertEventEnvelope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaAlertEventPublisher {
    private static final Logger log = LoggerFactory.getLogger(KafkaAlertEventPublisher.class);

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public KafkaAlertEventPublisher(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(AlertEventEnvelope alert) {
        log.info("Publishing alert_event eventId={} alertType={} severity={} riskScore={}",
                alert.eventId(), alert.alertType(), alert.severity(), alert.riskScore());
        kafkaTemplate.send("sentinel.alert-events",
                alert.userId() != null ? alert.userId().toString() : alert.eventId().toString(),
                alert);
    }
}
