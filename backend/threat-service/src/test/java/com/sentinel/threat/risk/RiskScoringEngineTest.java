package com.sentinel.threat.risk;

import com.sentinel.threat.domain.model.ThreatSeverity;
import com.sentinel.threat.domain.model.ThreatType;
import com.sentinel.threat.repository.RiskScoreRepository;
import com.sentinel.threat.repository.ThreatEventRepository;
import com.sentinel.threat.ruleengine.DetectorResult;
import com.sentinel.threat.ruleengine.RequestTelemetry;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RiskScoringEngineTest {

    @Mock
    private ThreatEventRepository threatEventRepository;

    @Mock
    private RiskScoreRepository riskScoreRepository;

    private RiskScoringEngine riskScoringEngine;

    @BeforeEach
    void setUp() {
        riskScoringEngine = new RiskScoringEngine(threatEventRepository, riskScoreRepository);
    }

    @Test
    void calculateRiskScore_CriticalRuleOnAuthEndpoint() {
        RequestTelemetry telemetry = new RequestTelemetry(
                "req-1", "corr-1", "192.168.1.10", "curl", "POST", "/api/v1/auth/login",
                Map.of(), Map.of(), null, null, null, 200, 10L, Instant.now()
        );

        DetectorResult sqliMatch = DetectorResult.match(
                "RULE-SQLI-001", ThreatType.SQL_INJECTION, ThreatSeverity.CRITICAL, "1=1", "1=1", "Fix"
        );

        when(threatEventRepository.countByClientIpAndCreatedAtAfter(eq("192.168.1.10"), any())).thenReturn(2L);

        double score = riskScoringEngine.calculateAndRecordRiskScore(telemetry, List.of(sqliMatch), ThreatSeverity.CRITICAL);

        assertTrue(score >= 85.0);
        assertEquals(ThreatSeverity.CRITICAL, riskScoringEngine.mapRiskToSeverity(score));
    }
}
