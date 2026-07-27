package com.sentinel.threat.detectors;

import com.sentinel.threat.domain.entity.ThreatRuleEntity;
import com.sentinel.threat.domain.model.ThreatSeverity;
import com.sentinel.threat.domain.model.ThreatType;
import com.sentinel.threat.ruleengine.DetectorResult;
import com.sentinel.threat.ruleengine.RequestTelemetry;
import com.sentinel.threat.ruleengine.ThreatDetector;
import org.springframework.stereotype.Component;

@Component
public class JwtManipulationDetector implements ThreatDetector {
    public static final String RULE_CODE = "RULE-JWT-001";

    @Override
    public String getRuleCode() {
        return RULE_CODE;
    }

    @Override
    public boolean supports(RequestTelemetry telemetry) {
        return telemetry.authToken() != null || (telemetry.headers() != null && telemetry.getHeader("Authorization") != null);
    }

    @Override
    public DetectorResult evaluate(RequestTelemetry telemetry, ThreatRuleEntity rule) {
        String token = telemetry.authToken();
        if (token == null && telemetry.headers() != null) {
            String authHeader = telemetry.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                token = authHeader.substring(7).trim();
            }
        }

        if (token != null) {
            if (token.contains("alg\": \"none\"") || token.contains("alg\":\"none\"") || token.toLowerCase().startsWith("eyJhbGciOiJub25l")) {
                return DetectorResult.match(
                        RULE_CODE,
                        ThreatType.JWT_MANIPULATION,
                        rule != null ? rule.getSeverity() : ThreatSeverity.HIGH,
                        "alg:none",
                        token,
                        "Reject unsigned JWT tokens and enforce strong HMAC/RSA signature validation."
                );
            }
        }
        return DetectorResult.noMatch();
    }
}
