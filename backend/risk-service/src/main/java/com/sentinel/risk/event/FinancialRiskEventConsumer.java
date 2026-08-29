package com.sentinel.risk.event;

import com.sentinel.common.constants.KafkaConstants;
import com.sentinel.common.events.FinancialRiskEvent;
import com.sentinel.risk.service.RiskCalculationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

/**
 * Sentinel Phase 6F — Real-Time Kafka Listener for Financial Transaction Events.
 * Listens to sentinel.financial.events and passes telemetry to RiskCalculationService.
 */
@Service
public class FinancialRiskEventConsumer {
    private static final Logger log = LoggerFactory.getLogger(FinancialRiskEventConsumer.class);

    private final RiskCalculationService riskCalculationService;

    public FinancialRiskEventConsumer(RiskCalculationService riskCalculationService) {
        this.riskCalculationService = riskCalculationService;
    }

    @KafkaListener(
            topics = KafkaConstants.FINANCIAL_EVENTS_TOPIC,
            groupId = "sentinel-risk-financial-engine"
    )
    public void consume(FinancialRiskEvent event) {
        if (event == null || event.transactionId() == null) {
            log.warn("Received null or invalid FinancialRiskEvent");
            return;
        }

        log.info("FinancialRiskEventConsumer received transaction txId={} userId={} merchantId={} amount={} currency={}",
                event.transactionId(), event.userId(), event.merchantId(), event.amount(), event.currency());

        try {
            riskCalculationService.evaluateFinancialRisk(event);
        } catch (Exception e) {
            log.error("Error processing FinancialRiskEvent txId={}: {}", event.transactionId(), e.getMessage(), e);
        }
    }
}
