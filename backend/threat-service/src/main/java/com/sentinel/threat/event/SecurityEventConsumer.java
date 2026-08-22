package com.sentinel.threat.event;

import com.sentinel.common.events.SecurityEventEnvelope;
import com.sentinel.threat.service.ThreatDetectionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class SecurityEventConsumer {
    private static final Logger log = LoggerFactory.getLogger(SecurityEventConsumer.class);

    private final ThreatDetectionService threatDetectionService;

    public SecurityEventConsumer(ThreatDetectionService threatDetectionService) {
        this.threatDetectionService = threatDetectionService;
    }

    @KafkaListener(
            topics = "sentinel.security-events",
            groupId = "sentinel-threat-engine"
    )
    public void consume(SecurityEventEnvelope event) {
        log.debug("Threat engine consumed eventId={} type={} email={}",
                event.eventId(), event.eventType(), event.email());
        threatDetectionService.analyze(event);
    }
}
