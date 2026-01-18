package com.tz.rental.landlord_management.infrastructure.persistence.repository.jpa;

import com.tz.rental.landlord_management.domain.model.valueobject.ActivityType;
import com.tz.rental.landlord_management.infrastructure.persistence.entity.ActivityLogEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Repository for Activity Logs.
 */
@Repository
public interface JpaActivityLogRepository extends JpaRepository<ActivityLogEntity, UUID> {

    /**
     * Find logs for a specific landlord.
     */
    Page<ActivityLogEntity> findByLandlordId(UUID landlordId, Pageable pageable);

    /**
     * Find logs by type for a landlord.
     */
    Page<ActivityLogEntity> findByLandlordIdAndActivityType(UUID landlordId, ActivityType activityType,
            Pageable pageable);

    /**
     * Find recent logs for a landlord.
     */
    @Query("SELECT a FROM ActivityLogEntity a WHERE a.landlord.id = :landlordId AND a.createdAt >= :sinceDate ORDER BY a.createdAt DESC")
    Page<ActivityLogEntity> findRecentLogs(@Param("landlordId") UUID landlordId,
            @Param("sinceDate") LocalDateTime sinceDate, Pageable pageable);
}
