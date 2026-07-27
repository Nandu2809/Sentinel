package com.sentinel.risk.scoring;

import com.sentinel.risk.domain.model.EntityType;
import com.sentinel.risk.dto.CalculateRiskRequest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MultiFactorRiskCalculatorTest {

    private final MultiFactorRiskCalculator calculator = new MultiFactorRiskCalculator();

    @Test
    void calculateScore_CriticalSeverityOnCriticalAsset() {
        CalculateRiskRequest req = new CalculateRiskRequest(
                EntityType.ENDPOINT, "/api/v1/auth/login", "SQL_INJECTION", "CRITICAL", true, true, false
        );

        double score = calculator.calculateScore(req);

        assertTrue(score >= 80.0);
    }

    @Test
    void calculateScore_BaselineRequest() {
        CalculateRiskRequest req = new CalculateRiskRequest(
                EntityType.USER, "user-123", null, null, false, false, false
        );

        double score = calculator.calculateScore(req);

        assertEquals(20.0, score);
    }
}
