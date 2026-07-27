package com.sentinel.risk.domain.model;

public enum RiskClassification {
    VERY_LOW(0.0, 20.0),
    LOW(21.0, 40.0),
    MEDIUM(41.0, 60.0),
    HIGH(61.0, 80.0),
    CRITICAL(81.0, 100.0);

    private final double minScore;
    private final double maxScore;

    RiskClassification(double minScore, double maxScore) {
        this.minScore = minScore;
        this.maxScore = maxScore;
    }

    public static RiskClassification fromScore(double score) {
        if (score >= 81.0) return CRITICAL;
        if (score >= 61.0) return HIGH;
        if (score >= 41.0) return MEDIUM;
        if (score >= 21.0) return LOW;
        return VERY_LOW;
    }

    public double getMinScore() {
        return minScore;
    }

    public double getMaxScore() {
        return maxScore;
    }
}
