package com.sentinel.risk.event;

import com.sentinel.common.events.AIThreatEventEnvelope;
import com.sentinel.risk.service.RiskCalculationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class AIThreatEventConsumer {
    private static final Logger log = LoggerFactory.getLogger(AIThreatEventConsumer.class);

    private final RiskCalculationService riskCalculationService;

    public AIThreatEventConsumer(RiskCalculationService riskCalculationService) {
        this.riskCalculationService = riskCalculationService;
    }

    @KafkaListener(
            topics = "sentinel.ai-events",
            groupId = "sentinel-risk-ai-engine"
    )
    public void consume(AIThreatEventEnvelope aiEvent) {
        log.info("Risk engine consumed ai_threat_event userId={} anomalyScore={} prediction={} reason={}",
                aiEvent.userId(), aiEvent.anomalyScore(), aiEvent.prediction(), aiEvent.reason());
        riskCalculationService.evaluateAIThreat(aiEvent);
    }
}
