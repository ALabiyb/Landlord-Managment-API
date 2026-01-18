package com.tz.rental.landlord_management.application.mapper;

import com.tz.rental.landlord_management.application.dto.NotificationResponse;
import com.tz.rental.landlord_management.infrastructure.persistence.entity.NotificationEntity;
import org.springframework.stereotype.Component;

@Component
public class NotificationMapper {

    public NotificationResponse toResponse(NotificationEntity entity) {
        if (entity == null) {
            return null;
        }

        return NotificationResponse.builder()
                .id(entity.getId())
                .type(entity.getType())
                .priority(entity.getPriority())
                .title(entity.getTitle())
                .message(entity.getMessage())
                .isRead(entity.isRead())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
