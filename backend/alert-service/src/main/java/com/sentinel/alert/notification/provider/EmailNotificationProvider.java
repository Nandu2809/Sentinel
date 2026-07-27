package com.sentinel.alert.notification.provider;

import com.sentinel.alert.domain.model.NotificationChannel;
import com.sentinel.alert.notification.NotificationPayload;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Component
public class EmailNotificationProvider implements NotificationProvider {
    private static final Logger log = LoggerFactory.getLogger(EmailNotificationProvider.class);
    private final JavaMailSender mailSender;

    public EmailNotificationProvider(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public boolean supports(NotificationChannel channel) {
        return channel == NotificationChannel.EMAIL;
    }

    @Override
    public void sendNotification(NotificationPayload payload) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(payload.recipient() != null ? payload.recipient() : "soc-alerts@sentinel.security");
            helper.setSubject(String.format("[%s] Sentinel Security Alert: %s", payload.severity(), payload.title()));

            String htmlBody = String.format("""
                <html>
                <body style="font-family: Arial, sans-serif; color: #333;">
                    <div style="background: #1e293b; color: #fff; padding: 15px; font-size: 18px; font-weight: bold;">
                        🛡️ Sentinel Security Alert - %s
                    </div>
                    <div style="padding: 20px; border: 1px solid #cbd5e1;">
                        <p><strong>Alert Code:</strong> %s</p>
                        <p><strong>Severity:</strong> <span style="color: red; font-weight: bold;">%s</span></p>
                        <p><strong>Risk Score:</strong> %.1f / 100.0</p>
                        <p><strong>Affected Endpoint:</strong> <code>%s</code></p>
                        <p><strong>Description:</strong> %s</p>
                        <hr/>
                        <p style="font-size: 12px; color: #64748b;">This is an automated security alert from Sentinel SOC Engine.</p>
                    </div>
                </body>
                </html>
                """, payload.severity(), payload.alertCode(), payload.severity(), payload.riskScore(),
                    payload.affectedApi() != null ? payload.affectedApi() : "N/A", payload.description());

            helper.setText(htmlBody, true);
            mailSender.send(message);
            log.info("email_alert_sent recipient={} alertCode={}", payload.recipient(), payload.alertCode());
        } catch (Exception e) {
            log.warn("email_alert_delivery_failed alertCode={} error={}", payload.alertCode(), e.getMessage());
        }
    }
}
