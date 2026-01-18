package com.tz.rental.landlord_management.application.dto;

import com.tz.rental.landlord_management.domain.model.valueobject.ActivityType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Response DTO for activity logs.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response containing activity log details")
public class ActivityLogResponse {

    private UUID id;
    private ActivityType activityType;
    private String description;
    private UUID entityId;
    private String entityType;
    private LocalDateTime createdAt;
}
