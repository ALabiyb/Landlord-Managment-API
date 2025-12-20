package com.tz.rental.landlord_management.infrastructure.persistence.repository.jpa;

import com.tz.rental.landlord_management.domain.model.valueobject.LeaseStatus;
import com.tz.rental.landlord_management.infrastructure.persistence.entity.LeaseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
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
    List<LeaseEntity> findByLandlordId(@Param("landlordId") UUID landlordId);

    @Query("SELECT count(l) FROM LeaseEntity l JOIN l.room r JOIN r.house h WHERE h.landlord.id = :landlordId AND l.status = :status")
    long countByLandlordIdAndStatus(@Param("landlordId") UUID landlordId, @Param("status") LeaseStatus status);

    @Query("SELECT SUM(p.amountPaid) FROM PaymentEntity p WHERE p.lease.id IN :leaseIds AND p.paymentDate BETWEEN :start AND :end")
    BigDecimal sumAmountByLeaseIdInAndPaymentDateBetween(@Param("leaseIds") List<UUID> leaseIds, @Param("start") LocalDate start, @Param("end") LocalDate end);
}