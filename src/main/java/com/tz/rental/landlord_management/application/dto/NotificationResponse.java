package com.tz.rental.landlord_management.application.dto;

import com.tz.rental.landlord_management.domain.model.valueobject.NotificationPriority;
import com.tz.rental.landlord_management.domain.model.valueobject.NotificationType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class NotificationResponse {
    private UUID id;
    private NotificationType type;
    private NotificationPriority priority;
    private String title;
    private String message;
    private boolean isRead;
    private LocalDateTime createdAt;
}
