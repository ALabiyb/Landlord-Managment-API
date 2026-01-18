package com.tz.rental.landlord_management.application.mapper;

import com.tz.rental.landlord_management.application.dto.ActivityLogResponse;
import com.tz.rental.landlord_management.infrastructure.persistence.entity.ActivityLogEntity;
import org.springframework.stereotype.Component;

@Component
public class ActivityLogMapper {

    public ActivityLogResponse toResponse(ActivityLogEntity entity) {
        if (entity == null) {
            return null;
        }

        return ActivityLogResponse.builder()
                .id(entity.getId())
                .activityType(entity.getActivityType())
                .description(entity.getDescription())
                .entityId(entity.getEntityId())
                .entityType(entity.getEntityType())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
