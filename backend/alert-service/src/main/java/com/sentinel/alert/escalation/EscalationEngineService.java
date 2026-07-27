package com.sentinel.alert.escalation;

import com.sentinel.alert.domain.entity.AlertEntity;
import com.sentinel.alert.domain.model.AlertSeverity;
import com.sentinel.alert.domain.model.NotificationChannel;
import com.sentinel.alert.notification.service.NotificationChannelDispatcher;
import com.sentinel.alert.repository.AlertRepository;
import com.sentinel.alert.repository.EscalationRuleRepository;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EscalationEngineService {
    private static final Logger log = LoggerFactory.getLogger(EscalationEngineService.class);
    private final AlertRepository alertRepository;
    private final EscalationRuleRepository escalationRuleRepository;
    private final NotificationChannelDispatcher dispatcher;

    public EscalationEngineService(AlertRepository alertRepository,
                                   EscalationRuleRepository escalationRuleRepository,
                                   NotificationChannelDispatcher dispatcher) {
        this.alertRepository = alertRepository;
        this.escalationRuleRepository = escalationRuleRepository;
        this.dispatcher = dispatcher;
    }

    @Scheduled(fixedDelay = 60000) // Check every 60 seconds
    @Transactional
    public void evaluateEscalations() {
        escalationRuleRepository.findByEnabledTrue().forEach(rule -> {
            Instant threshold = Instant.now().minus(rule.getUnacknowledgedTimeoutMinutes(), ChronoUnit.MINUTES);
            List<AlertEntity> breachedAlerts = alertRepository.findUnacknowledgedAlertsPastSla(rule.getSeverity(), threshold);

            for (AlertEntity alert : breachedAlerts) {
                log.warn("escalating_unacknowledged_alert code={} severity={} rule={}",
                        alert.getAlertCode(), alert.getSeverity(), rule.getRuleName());

                alert.assignAnalyst(rule.getEscalateToRole());
                alertRepository.save(alert);

                dispatcher.dispatch(alert, NotificationChannel.WEBSOCKET, rule.getEscalateToRole());
                dispatcher.dispatch(alert, NotificationChannel.EMAIL, "soc-lead@sentinel.security");
            }
        });
    }
}
