package com.tz.rental.landlord_management.domain.service;

import com.tz.rental.landlord_management.domain.model.valueobject.NotificationType;

public interface NotificationService {
    void sendNotification(String recipient, String message, NotificationType type);
}