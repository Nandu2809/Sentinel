package com.sentinel.threat.event;

import com.sentinel.common.events.ThreatEventEnvelope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaThreatEventPublisher {
    private static final Logger log = LoggerFactory.getLogger(KafkaThreatEventPublisher.class);

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public KafkaThreatEventPublisher(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(ThreatEventEnvelope event) {
        log.info("Publishing threat_event threatId={} type={} email={}",
                event.threatId(), event.threatType(), event.email());
        kafkaTemplate.send("sentinel.threat-events",
                event.threatId() != null ? event.threatId().toString() : event.eventId().toString(),
                event);
    }
}
