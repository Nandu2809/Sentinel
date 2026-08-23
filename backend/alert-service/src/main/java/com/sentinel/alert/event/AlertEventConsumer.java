package com.sentinel.alert.event;

import com.sentinel.alert.service.AlertProcessingService;
import com.sentinel.common.events.AlertEventEnvelope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class AlertEventConsumer {
    private static final Logger log = LoggerFactory.getLogger(AlertEventConsumer.class);

    private final AlertProcessingService alertProcessingService;

    public AlertEventConsumer(AlertProcessingService alertProcessingService) {
        this.alertProcessingService = alertProcessingService;
    }

    @KafkaListener(
            topics = "sentinel.alert-events",
            groupId = "sentinel-alert-engine"
    )
    public void consume(AlertEventEnvelope alertEvent) {
        log.info("Alert engine consumed alert_event eventId={} alertType={} severity={} riskScore={}",
                alertEvent.eventId(), alertEvent.alertType(), alertEvent.severity(), alertEvent.riskScore());
        alertProcessingService.processAlert(alertEvent);
    }
}
