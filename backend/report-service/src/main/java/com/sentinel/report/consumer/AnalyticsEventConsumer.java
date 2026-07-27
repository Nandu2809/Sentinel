package com.sentinel.report.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class AnalyticsEventConsumer {
    private static final Logger log = LoggerFactory.getLogger(AnalyticsEventConsumer.class);

    @EventListener
    public void onSecurityEvent(Object event) {
        log.debug("analytics_event_consumed type={}", event.getClass().getSimpleName());
    }
}
