package com.sentinel.alert.service;

import com.sentinel.common.events.AlertEventEnvelope;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailNotificationService {
    private static final Logger log = LoggerFactory.getLogger(EmailNotificationService.class);

    private final JavaMailSender mailSender;

    @Value("${sentinel.alert.email.to:sec-ops@sentinel.com}")
    private String recipientEmail;

    @Value("${sentinel.alert.email.from:alerts@sentinel.com}")
    private String fromEmail;

    public EmailNotificationService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendAlertEmail(AlertEventEnvelope alert) {
        if (alert == null) {
            return;
        }

        String severity = alert.severity() != null ? alert.severity().toUpperCase() : "HIGH";
        if (!"HIGH".equals(severity) && !"CRITICAL".equals(severity)) {
            log.debug("Skipping email for alert with severity {}", severity);
            return;
        }

        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(recipientEmail);
            helper.setSubject("SENTINEL SECURITY ALERT - " + alert.alertType() + " [" + severity + "]");

            String body = """
                <html>
                <body style="font-family: Arial, sans-serif; color: #333;">
                    <div style="background-color: #721c24; color: white; padding: 15px; border-radius: 5px;">
                        <h2 style="margin: 0;">SENTINEL SECURITY ALERT</h2>
                    </div>
                    <div style="padding: 20px; border: 1px solid #ddd; border-top: none;">
                        <p><strong>Alert Type:</strong> %s</p>
                        <p><strong>Severity:</strong> <span style="color: red; font-weight: bold;">%s</span></p>
                        <p><strong>Risk Score:</strong> %d</p>
                        <p><strong>User ID:</strong> %s</p>
                        <p><strong>Message:</strong> %s</p>
                        <p><strong>Timestamp:</strong> %s</p>
                        <hr style="border: none; border-top: 1px solid #eee;" />
                        <p style="font-size: 12px; color: #777;">Sentinel Security Analytics & Response Engine</p>
                    </div>
                </body>
                </html>
                """.formatted(
                    alert.alertType(),
                    severity,
                    alert.riskScore() != null ? alert.riskScore() : 0,
                    alert.userId() != null ? alert.userId().toString() : "N/A",
                    alert.message() != null ? alert.message() : "High risk security incident detected",
                    alert.timestamp() != null ? alert.timestamp().toString() : "N/A"
            );

            helper.setText(body, true);
            mailSender.send(mimeMessage);

            log.info("SENTINEL_EMAIL_NOTIFICATION_SENT to={} alertType={} riskScore={} severity={}",
                    recipientEmail, alert.alertType(), alert.riskScore(), severity);
        } catch (MessagingException | RuntimeException e) {
            log.error("Failed to send alert email for eventId={}: {}", alert.eventId(), e.getMessage(), e);
        }
    }
}
