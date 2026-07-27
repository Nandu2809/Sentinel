package com.sentinel.monitoring.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class MonitoringEventPublisher {
    private static final Logger log = LoggerFactory.getLogger(MonitoringEventPublisher.class);
    private final ApplicationEventPublisher applicationEventPublisher;

    public MonitoringEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public void publishRequestReceived(ApiRequestReceivedEvent event) {
        log.debug("publishing_request_received_event id={} uri={}", event.requestId(), event.uri());
        applicationEventPublisher.publishEvent(event);
    }

    public void publishResponseCompleted(ApiResponseCompletedEvent event) {
        log.debug("publishing_response_completed_event id={} status={}", event.requestId(), event.statusCode());
        applicationEventPublisher.publishEvent(event);
    }

    public void publishErrorOccurred(ApiErrorOccurredEvent event) {
        log.warn("publishing_error_occurred_event id={} status={} msg={}", event.requestId(), event.statusCode(), event.errorMessage());
        applicationEventPublisher.publishEvent(event);
    }
}
