package com.tz.rental.landlord_management.infrastructure.persistence.repository.jpa;

import com.tz.rental.landlord_management.infrastructure.persistence.entity.NotificationEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface JpaNotificationRepository extends JpaRepository<NotificationEntity, UUID> {

    Page<NotificationEntity> findByLandlordId(UUID landlordId, Pageable pageable);

    Page<NotificationEntity> findByTenantId(UUID tenantId, Pageable pageable);

    long countByLandlordIdAndIsReadFalse(UUID landlordId);

    long countByTenantIdAndIsReadFalse(UUID tenantId);
}
