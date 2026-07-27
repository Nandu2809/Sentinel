package com.sentinel.risk.scoring;

import com.sentinel.risk.dto.CalculateRiskRequest;
import org.springframework.stereotype.Component;

@Component
public class MultiFactorRiskCalculator implements RiskCalculationStrategy {

    @Override
    public double calculateScore(CalculateRiskRequest request) {
        double baseScore = 20.0; // Default baseline

        if (request.severity() != null) {
            switch (request.severity().toUpperCase()) {
                case "CRITICAL" -> baseScore = 90.0;
                case "HIGH" -> baseScore = 75.0;
                case "MEDIUM" -> baseScore = 50.0;
                case "LOW" -> baseScore = 30.0;
                default -> baseScore = 15.0;
            }
        }

        double multiplier = 1.0;
        if (Boolean.TRUE.equals(request.isCriticalAsset())) {
            multiplier += 0.25;
        }
        if (Boolean.TRUE.equals(request.isAuthFailure())) {
            multiplier += 0.20;
        }
        if (Boolean.TRUE.equals(request.isKnownBot())) {
            multiplier += 0.15;
        }

        double total = baseScore * multiplier;
        return Math.min(Math.max(Math.round(total * 10.0) / 10.0, 0.0), 100.0);
    }
}
