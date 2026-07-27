package com.sentinel.threat.ruleengine;

import com.sentinel.threat.domain.entity.RuleExecutionLogEntity;
import com.sentinel.threat.domain.entity.ThreatRuleEntity;
import com.sentinel.threat.repository.RuleExecutionLogRepository;
import com.sentinel.threat.repository.ThreatRuleRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ThreatRuleEvaluatorService {
    private static final Logger log = LoggerFactory.getLogger(ThreatRuleEvaluatorService.class);
    private final ThreatRuleRepository ruleRepository;
    private final RuleExecutionLogRepository executionLogRepository;
    private final Map<String, ThreatDetector> detectorMap;

    public ThreatRuleEvaluatorService(ThreatRuleRepository ruleRepository,
                                       RuleExecutionLogRepository executionLogRepository,
                                       List<ThreatDetector> detectors) {
        this.ruleRepository = ruleRepository;
        this.executionLogRepository = executionLogRepository;
        this.detectorMap = detectors.stream()
                .collect(Collectors.toMap(ThreatDetector::getRuleCode, Function.identity(), (a, b) -> a));
    }

    @Transactional
    public List<DetectorResult> evaluate(RequestTelemetry telemetry) {
        List<ThreatRuleEntity> enabledRules = ruleRepository.findByEnabledTrue();
        List<DetectorResult> matches = new ArrayList<>();

        for (ThreatRuleEntity rule : enabledRules) {
            ThreatDetector detector = detectorMap.get(rule.getRuleCode());
            if (detector == null || !detector.supports(telemetry)) {
                continue;
            }

            long start = System.currentTimeMillis();
            DetectorResult result;
            try {
                result = detector.evaluate(telemetry, rule);
            } catch (Exception e) {
                log.error("rule_evaluation_error rule={} uri={} error={}", rule.getRuleCode(), telemetry.uri(), e.getMessage(), e);
                result = DetectorResult.noMatch();
            }
            long executionTimeMs = System.currentTimeMillis() - start;

            executionLogRepository.save(new RuleExecutionLogEntity(rule.getRuleCode(), executionTimeMs, result.matched()));

            if (result.matched()) {
                log.warn("threat_rule_matched code={} type={} uri={}", rule.getRuleCode(), result.threatType(), telemetry.uri());
                matches.add(result);
            }
        }

        return matches;
    }
}
