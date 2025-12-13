package com.tz.rental.landlord_management.infrastructure.persistence.repository.jpa;

import com.tz.rental.landlord_management.domain.model.valueobject.LeaseStatus;
import com.tz.rental.landlord_management.infrastructure.persistence.entity.LeaseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaLeaseRepository extends JpaRepository<LeaseEntity, UUID> {
    List<LeaseEntity> findByTenantId(UUID tenantId);

    @Query("SELECT l FROM LeaseEntity l WHERE l.room.id = :roomId AND l.status = 'ACTIVE'")
    Optional<LeaseEntity> findActiveLeaseByRoomId(UUID roomId);

    @Query("SELECT CASE WHEN COUNT(l) > 0 THEN TRUE ELSE FALSE END FROM LeaseEntity l WHERE l.room.id = :roomId AND l.status = 'ACTIVE'")
    boolean existsActiveLeaseForRoom(UUID roomId);

    List<LeaseEntity> findByStatus(LeaseStatus status);

    @Query("SELECT l FROM LeaseEntity l JOIN l.room r JOIN r.house h WHERE h.landlord.id = :landlordId")
    List<LeaseEntity> findByLandlordId(UUID landlordId);
}