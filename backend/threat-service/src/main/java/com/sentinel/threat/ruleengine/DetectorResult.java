package com.sentinel.threat.ruleengine;

import com.sentinel.threat.domain.model.ThreatSeverity;
import com.sentinel.threat.domain.model.ThreatType;

public record DetectorResult(
        boolean matched,
        String ruleCode,
        ThreatType threatType,
        ThreatSeverity severity,
        String matchedPattern,
        String rawPayloadSample,
        String recommendation
) {
    public static DetectorResult noMatch() {
        return new DetectorResult(false, null, null, null, null, null, null);
    }

    public static DetectorResult match(String ruleCode, ThreatType threatType, ThreatSeverity severity,
                                        String matchedPattern, String rawPayloadSample, String recommendation) {
        return new DetectorResult(true, ruleCode, threatType, severity, matchedPattern, rawPayloadSample, recommendation);
    }
}
