package com.sentinel.monitoring.event;

import com.sentinel.common.events.SecurityEventEnvelope;
import com.sentinel.monitoring.domain.model.SecurityEventEntity;
import com.sentinel.monitoring.repository.SecurityEventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SecurityEventConsumer {
    private static final Logger log = LoggerFactory.getLogger(SecurityEventConsumer.class);

    private final SecurityEventRepository securityEventRepository;

    public SecurityEventConsumer(SecurityEventRepository securityEventRepository) {
        this.securityEventRepository = securityEventRepository;
    }

    @KafkaListener(
            topics = "sentinel.security-events",
            groupId = "sentinel-monitoring-group"
    )
    @Transactional
    public void consume(SecurityEventEnvelope event) {
        log.info("Consumed security_event eventType={} userId={} email={} outcome={}",
                event.eventType(), event.userId(), event.email(), event.outcome());

        SecurityEventEntity entity = new SecurityEventEntity(
                event.eventId(),
                event.eventType(),
                event.userId(),
                event.email(),
                event.ipAddress(),
                event.userAgent(),
                event.outcome(),
                event.message(),
                event.timestamp()
        );

        securityEventRepository.save(entity);
    }
}
