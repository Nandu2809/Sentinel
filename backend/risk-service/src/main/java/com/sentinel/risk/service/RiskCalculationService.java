package com.sentinel.risk.service;

import com.sentinel.common.events.RiskEventEnvelope;
import com.sentinel.common.events.ThreatEventEnvelope;
import com.sentinel.risk.domain.entity.RiskAssessmentEntity;
import com.sentinel.risk.event.KafkaRiskEventPublisher;
import com.sentinel.risk.repository.RiskAssessmentRepository;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RiskCalculationService {
    private static final Logger log = LoggerFactory.getLogger(RiskCalculationService.class);

    private final RiskAssessmentRepository riskAssessmentRepository;
    private final KafkaRiskEventPublisher riskEventPublisher;

    public RiskCalculationService(RiskAssessmentRepository riskAssessmentRepository, KafkaRiskEventPublisher riskEventPublisher) {
        this.riskAssessmentRepository = riskAssessmentRepository;
        this.riskEventPublisher = riskEventPublisher;
    }

    @Transactional
    public void evaluate(ThreatEventEnvelope threatEvent) {
        if (threatEvent == null) {
            return;
        }

        int baseBonus = getThreatBonus(threatEvent.threatType());
        int rawScore = (int) Math.round(threatEvent.riskScore()) + baseBonus;
        int finalRiskScore = Math.min(100, Math.max(0, rawScore));

        String riskLevel = calculateRiskLevel(finalRiskScore);
        String decision = determineDecision(riskLevel);

        RiskAssessmentEntity entity = new RiskAssessmentEntity(
                threatEvent.userId(),
                threatEvent.threatId(),
                finalRiskScore,
                riskLevel,
                decision,
                Instant.now()
        );

        RiskAssessmentEntity saved = riskAssessmentRepository.save(entity);

        log.info("RISK_EVALUATED user={} threatType={} riskScore={} riskLevel={} decision={}",
                threatEvent.email(), threatEvent.threatType(), finalRiskScore, riskLevel, decision);

        riskEventPublisher.publish(new RiskEventEnvelope(
                saved.getId(),
                threatEvent.userId(),
                threatEvent.email(),
                finalRiskScore,
                riskLevel,
                decision,
                Instant.now()
        ));
    }

    private int getThreatBonus(String threatType) {
        if (threatType == null) {
            return 0;
        }
        return switch (threatType.toUpperCase()) {
            case "BRUTE_FORCE_ATTACK", "BRUTE_FORCE" -> 50;
            case "SUSPICIOUS_LOGIN" -> 30;
            case "ACCOUNT_ABUSE" -> 40;
            default -> 20;
        };
    }

    private String calculateRiskLevel(int score) {
        if (score >= 81) {
            return "CRITICAL";
        } else if (score >= 61) {
            return "HIGH";
        } else if (score >= 31) {
            return "MEDIUM";
        } else {
            return "LOW";
        }
    }

    private String determineDecision(String riskLevel) {
        return switch (riskLevel) {
            case "CRITICAL" -> "BLOCK_AND_ALERT";
            case "HIGH" -> "INVESTIGATE";
            case "MEDIUM" -> "REVIEW";
            default -> "MONITOR";
        };
    }
}
