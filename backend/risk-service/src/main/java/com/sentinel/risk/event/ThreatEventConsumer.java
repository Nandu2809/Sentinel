package com.sentinel.risk.event;

import com.sentinel.common.events.ThreatEventEnvelope;
import com.sentinel.risk.service.RiskCalculationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class ThreatEventConsumer {
    private static final Logger log = LoggerFactory.getLogger(ThreatEventConsumer.class);

    private final RiskCalculationService riskCalculationService;

    public ThreatEventConsumer(RiskCalculationService riskCalculationService) {
        this.riskCalculationService = riskCalculationService;
    }

    @KafkaListener(
            topics = "sentinel.threat-events",
            groupId = "sentinel-risk-engine"
    )
    public void consume(ThreatEventEnvelope threatEvent) {
        log.info("Risk engine consumed threat_event threatId={} threatType={} user={}",
                threatEvent.threatId(), threatEvent.threatType(), threatEvent.email());
        riskCalculationService.evaluate(threatEvent);
    }
}
