package com.sentinel.risk.scoring;

import com.sentinel.risk.dto.CalculateRiskRequest;

public interface RiskCalculationStrategy {
    double calculateScore(CalculateRiskRequest request);
}
