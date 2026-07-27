package com.sentinel.alert.notification.provider;

import com.sentinel.alert.domain.model.NotificationChannel;
import com.sentinel.alert.notification.NotificationPayload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class WebSocketNotificationProvider implements NotificationProvider {
    private static final Logger log = LoggerFactory.getLogger(WebSocketNotificationProvider.class);
    private final SimpMessagingTemplate messagingTemplate;

    public WebSocketNotificationProvider(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @Override
    public boolean supports(NotificationChannel channel) {
        return channel == NotificationChannel.WEBSOCKET;
    }

    @Override
    public void sendNotification(NotificationPayload payload) {
        try {
            messagingTemplate.convertAndSend("/topic/security-alerts", payload);
            log.info("websocket_alert_broadcast alertCode={}", payload.alertCode());
        } catch (Exception e) {
            log.warn("websocket_alert_delivery_failed alertCode={} error={}", payload.alertCode(), e.getMessage());
        }
    }
}
