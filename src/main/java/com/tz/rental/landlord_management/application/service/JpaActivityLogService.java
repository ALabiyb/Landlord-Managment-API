package com.tz.rental.landlord_management.application.service;

import com.tz.rental.landlord_management.application.dto.ActivityLogResponse;
import com.tz.rental.landlord_management.application.mapper.ActivityLogMapper;
import com.tz.rental.landlord_management.domain.model.valueobject.ActivityType;
import com.tz.rental.landlord_management.infrastructure.persistence.entity.ActivityLogEntity;
import com.tz.rental.landlord_management.infrastructure.persistence.entity.LandlordEntity;
import com.tz.rental.landlord_management.infrastructure.persistence.repository.jpa.JpaActivityLogRepository;
import com.tz.rental.landlord_management.infrastructure.persistence.repository.jpa.JpaLandlordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Service for managing activity logs.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class JpaActivityLogService {

    private final JpaActivityLogRepository activityLogRepository;
    private final JpaLandlordRepository landlordRepository;
    private final ActivityLogMapper activityLogMapper;

    /**
     * Record an activity log asynchronously.
     * Uses REQUIRES_NEW propagation to ensure logs are saved even if the main
     * transaction fails (optional, depending on requirements).
     * For now we'll use standard propagation but mark as Async if enabled in
     * config.
     */
    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void recordActivity(UUID landlordId, ActivityType type, String description, UUID entityId, String entityType,
            String ipAddress) {
        try {
            ActivityLogEntity logEntity = new ActivityLogEntity();

            if (landlordId != null) {
                LandlordEntity landlord = landlordRepository.findById(landlordId).orElse(null);
                logEntity.setLandlord(landlord);
            }

            logEntity.setActivityType(type);
            logEntity.setDescription(description);
            logEntity.setEntityId(entityId);
            logEntity.setEntityType(entityType);
            logEntity.setIpAddress(ipAddress);

            activityLogRepository.save(logEntity);
            log.debug("Recorded activity: {}", description);
        } catch (Exception e) {
            log.error("Failed to record activity log", e);
        }
    }

    @Transactional(readOnly = true)
    public Page<ActivityLogResponse> getLogsForLandlord(UUID landlordId, Pageable pageable) {
        return activityLogRepository.findByLandlordId(landlordId, pageable)
                .map(activityLogMapper::toResponse);
    }
}
