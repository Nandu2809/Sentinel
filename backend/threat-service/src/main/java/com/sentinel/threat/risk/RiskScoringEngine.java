package com.sentinel.threat.risk;

import com.sentinel.threat.domain.entity.RiskScoreEntity;
import com.sentinel.threat.domain.model.ThreatSeverity;
import com.sentinel.threat.repository.RiskScoreRepository;
import com.sentinel.threat.repository.ThreatEventRepository;
import com.sentinel.threat.ruleengine.DetectorResult;
import com.sentinel.threat.ruleengine.RequestTelemetry;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class RiskScoringEngine {
    private final ThreatEventRepository threatEventRepository;
    private final RiskScoreRepository riskScoreRepository;

    public RiskScoringEngine(ThreatEventRepository threatEventRepository, RiskScoreRepository riskScoreRepository) {
        this.threatEventRepository = threatEventRepository;
        this.riskScoreRepository = riskScoreRepository;
    }

    public double calculateAndRecordRiskScore(RequestTelemetry telemetry, List<DetectorResult> matches, ThreatSeverity highestSeverity) {
        if (matches == null || matches.isEmpty()) {
            return 0.0;
        }

        double baseScore = highestSeverity.getBaseRiskWeight();

        Instant oneHourAgo = Instant.now().minus(1, ChronoUnit.HOURS);
        long pastOffenses = threatEventRepository.countByClientIpAndCreatedAtAfter(telemetry.clientIp(), oneHourAgo);
        double frequencyBonus = Math.min(pastOffenses * 5.0, 20.0);

        double sensitiveMultiplier = 1.0;
        String uri = telemetry.uri() != null ? telemetry.uri().toLowerCase() : "";
        if (uri.contains("/auth") || uri.contains("/admin") || uri.contains("/users") || uri.contains("/keys")) {
            sensitiveMultiplier = 1.25;
        }

        double multiRuleBonus = matches.size() > 1 ? 15.0 : 0.0;

        double totalScore = (baseScore + frequencyBonus + multiRuleBonus) * sensitiveMultiplier;
        double normalizedScore = Math.min(Math.max(Math.round(totalScore * 10.0) / 10.0, 0.0), 100.0);

        riskScoreRepository.save(new RiskScoreEntity(telemetry.clientIp(), telemetry.userId(), telemetry.uri(), normalizedScore));

        return normalizedScore;
    }

    public ThreatSeverity mapRiskToSeverity(double riskScore) {
        if (riskScore >= 85.0) return ThreatSeverity.CRITICAL;
        if (riskScore >= 65.0) return ThreatSeverity.HIGH;
        if (riskScore >= 40.0) return ThreatSeverity.MEDIUM;
        if (riskScore >= 20.0) return ThreatSeverity.LOW;
        return ThreatSeverity.INFORMATIONAL;
    }
}
