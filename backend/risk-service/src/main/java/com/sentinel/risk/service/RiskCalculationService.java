package com.sentinel.risk.service;

import com.sentinel.common.events.AIThreatEventEnvelope;
import com.sentinel.common.events.AlertEventEnvelope;
import com.sentinel.common.events.RiskEventEnvelope;
import com.sentinel.common.events.ThreatEventEnvelope;
import com.sentinel.risk.domain.entity.RiskAssessmentEntity;
import com.sentinel.risk.event.KafkaAlertEventPublisher;
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
    private final KafkaAlertEventPublisher alertEventPublisher;

    public RiskCalculationService(RiskAssessmentRepository riskAssessmentRepository,
                                  KafkaRiskEventPublisher riskEventPublisher,
                                  KafkaAlertEventPublisher alertEventPublisher) {
        this.riskAssessmentRepository = riskAssessmentRepository;
        this.riskEventPublisher = riskEventPublisher;
        this.alertEventPublisher = alertEventPublisher;
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

        if ("HIGH".equalsIgnoreCase(riskLevel) || "CRITICAL".equalsIgnoreCase(riskLevel)) {
            if (alertEventPublisher != null) {
                alertEventPublisher.publish(new AlertEventEnvelope(
                        saved.getId(),
                        threatEvent.userId(),
                        threatEvent.threatType() != null ? threatEvent.threatType() : "SECURITY_ALERT",
                        riskLevel,
                        finalRiskScore,
                        "SENTINEL SECURITY ALERT: High risk threat detected (" + threatEvent.threatType() + ") for user " + threatEvent.email() + " with risk score " + finalRiskScore,
                        Instant.now()
                ));
            }
        }
    }

    @Transactional
    public void evaluateAIThreat(AIThreatEventEnvelope aiEvent) {
        if (aiEvent == null) {
            return;
        }

        int aiBonus = aiEvent.anomalyScore() != null ? (int) Math.round(aiEvent.anomalyScore() * 0.5) : 30;
        int finalRiskScore = Math.min(100, Math.max(0, 50 + aiBonus));

        String riskLevel = calculateRiskLevel(finalRiskScore);
        String decision = determineDecision(riskLevel);

        RiskAssessmentEntity entity = new RiskAssessmentEntity(
                aiEvent.userId(),
                aiEvent.eventId(),
                finalRiskScore,
                riskLevel,
                decision,
                Instant.now()
        );

        RiskAssessmentEntity saved = riskAssessmentRepository.save(entity);

        log.info("AI_RISK_EVALUATED user={} anomalyScore={} finalRiskScore={} riskLevel={} decision={}",
                aiEvent.email(), aiEvent.anomalyScore(), finalRiskScore, riskLevel, decision);

        riskEventPublisher.publish(new RiskEventEnvelope(
                saved.getId(),
                aiEvent.userId(),
                aiEvent.email(),
                finalRiskScore,
                riskLevel,
                decision,
                Instant.now()
        ));

        if ("HIGH".equalsIgnoreCase(riskLevel) || "CRITICAL".equalsIgnoreCase(riskLevel)) {
            if (alertEventPublisher != null) {
                alertEventPublisher.publish(new AlertEventEnvelope(
                        saved.getId(),
                        aiEvent.userId(),
                        "AI_ANOMALY_" + (aiEvent.prediction() != null ? aiEvent.prediction() : "BEHAVIORAL"),
                        riskLevel,
                        finalRiskScore,
                        "SENTINEL AI THREAT ALERT: " + (aiEvent.reason() != null ? aiEvent.reason() : "High AI anomaly score detected"),
                        Instant.now()
                ));
            }
        }
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
