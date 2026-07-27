package com.sentinel.threat.domain.model;

public enum ThreatSeverity {
    INFORMATIONAL(10.0),
    LOW(25.0),
    MEDIUM(50.0),
    HIGH(75.0),
    CRITICAL(95.0);

    private final double baseRiskWeight;

    ThreatSeverity(double baseRiskWeight) {
        this.baseRiskWeight = baseRiskWeight;
    }

    public double getBaseRiskWeight() {
        return baseRiskWeight;
    }
}
