package com.sentinel.alert.notification.provider;

import com.sentinel.alert.domain.model.NotificationChannel;
import com.sentinel.alert.notification.NotificationPayload;

public interface NotificationProvider {
    boolean supports(NotificationChannel channel);
    void sendNotification(NotificationPayload payload);
}
