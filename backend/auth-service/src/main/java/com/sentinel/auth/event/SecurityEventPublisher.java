package com.sentinel.auth.event;

import com.sentinel.common.events.SecurityEventEnvelope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class SecurityEventPublisher {
    private static final Logger log = LoggerFactory.getLogger(SecurityEventPublisher.class);
    private static final String TOPIC = "sentinel.security-events";

    private final KafkaTemplate<String, SecurityEventEnvelope> kafkaTemplate;

    public SecurityEventPublisher(KafkaTemplate<String, SecurityEventEnvelope> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(SecurityEventEnvelope event) {
        log.debug("Publishing security event to Kafka topic={} eventId={} type={}",
                TOPIC, event.eventId(), event.eventType());
        kafkaTemplate.send(
                TOPIC,
                event.eventId().toString(),
                event
        );
    }
}
