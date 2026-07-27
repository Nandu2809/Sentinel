package com.sentinel.alert.producer;

import com.sentinel.alert.events.AlertAcknowledgedEvent;
import com.sentinel.alert.events.AlertCreatedEvent;
import com.sentinel.alert.events.AlertResolvedEvent;
import com.sentinel.alert.events.NotificationSentEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class AlertEventProducer {
    private static final Logger log = LoggerFactory.getLogger(AlertEventProducer.class);
    private final ApplicationEventPublisher applicationEventPublisher;

    public AlertEventProducer(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public void publishAlertCreated(AlertCreatedEvent event) {
        log.info("publishing_alert_created code={} severity={}", event.alertCode(), event.severity());
        applicationEventPublisher.publishEvent(event);
    }

    public void publishAlertAcknowledged(AlertAcknowledgedEvent event) {
        log.info("publishing_alert_acknowledged code={} analyst={}", event.alertCode(), event.acknowledgedBy());
        applicationEventPublisher.publishEvent(event);
    }

    public void publishAlertResolved(AlertResolvedEvent event) {
        log.info("publishing_alert_resolved code={} analyst={}", event.alertCode(), event.resolvedBy());
        applicationEventPublisher.publishEvent(event);
    }

    public void publishNotificationSent(NotificationSentEvent event) {
        log.info("publishing_notification_sent channel={} status={}", event.channel(), event.status());
        applicationEventPublisher.publishEvent(event);
    }
}
