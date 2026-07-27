package com.sentinel.alert.notification.provider;

import com.sentinel.alert.domain.model.NotificationChannel;
import com.sentinel.alert.notification.NotificationPayload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class WebhookNotificationProvider implements NotificationProvider {
    private static final Logger log = LoggerFactory.getLogger(WebhookNotificationProvider.class);

    @Override
    public boolean supports(NotificationChannel channel) {
        return channel == NotificationChannel.WEBHOOK;
    }

    @Override
    public void sendNotification(NotificationPayload payload) {
        log.info("webhook_alert_dispatch alertCode={} title={}", payload.alertCode(), payload.title());
    }
}
