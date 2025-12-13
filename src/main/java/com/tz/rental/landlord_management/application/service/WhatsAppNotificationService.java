package com.tz.rental.landlord_management.application.service;

import com.tz.rental.landlord_management.domain.service.NotificationService;
import com.tz.rental.landlord_management.domain.model.valueobject.NotificationType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class WhatsAppNotificationService implements NotificationService {

    @Override
    public void sendNotification(String recipient, String message, NotificationType type) {
        // --- Placeholder for actual WhatsApp API integration ---
        // In a real-world scenario, this method would:
        // 1. Call an external WhatsApp Business API client.
        // 2. Potentially use a pre-approved WhatsApp message template based on 'type'.
        //    For example, a 'RENT_DUE_REMINDER' might map to a template like:
        //    "Hello {{1}}, your rent of {{2}} TZS is due on {{3}}."
        //    The 'message' parameter would then contain the dynamic data for the template.
        // 3. Handle API responses, retries, and error logging.
        // -------------------------------------------------------

        log.info("Simulating WhatsApp notification:");
        log.info("  Recipient: {}", recipient);
        log.info("  Type: {}", type);
        log.info("  Message: {}", message);
        log.info("------------------------------------");

        // For now, we just log the message.
        // Future: Integrate with a WhatsApp API client here.
    }
}