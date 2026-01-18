package com.tz.rental.landlord_management.infrastructure.persistence.repository.jpa;

import com.tz.rental.landlord_management.domain.model.valueobject.LeaseStatus;
import com.tz.rental.landlord_management.infrastructure.persistence.entity.LeaseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    BigDecimal sumAmountByLeaseIdInAndPaymentDateBetween(@Param("leaseIds") List<UUID> leaseIds,
            @Param("start") LocalDate start, @Param("end") LocalDate end);

    // ===== New Methods for Full CRUD =====

    @Query("SELECT l FROM LeaseEntity l JOIN FETCH l.tenant t JOIN FETCH l.room r JOIN FETCH r.house h WHERE h.landlord.id = :landlordId")
    Page<LeaseEntity> findByLandlordIdPaginated(@Param("landlordId") UUID landlordId, Pageable pageable);

    @Query("SELECT l FROM LeaseEntity l JOIN FETCH l.tenant t JOIN FETCH l.room r JOIN FETCH r.house h WHERE l.id = :leaseId AND h.landlord.id = :landlordId")
    Optional<LeaseEntity> findByIdAndLandlordId(@Param("leaseId") UUID leaseId, @Param("landlordId") UUID landlordId);

    @Query("SELECT l FROM LeaseEntity l JOIN FETCH l.tenant t JOIN FETCH l.room r JOIN FETCH r.house h WHERE h.landlord.id = :landlordId AND l.status = :status")
    Page<LeaseEntity> findByLandlordIdAndStatusPaginated(@Param("landlordId") UUID landlordId,
            @Param("status") LeaseStatus status, Pageable pageable);

    @Query("SELECT l FROM LeaseEntity l JOIN FETCH l.tenant t JOIN FETCH l.room r WHERE r.id = :roomId ORDER BY l.startDate DESC")
    List<LeaseEntity> findByRoomIdWithDetails(@Param("roomId") UUID roomId);

    @Query("SELECT l FROM LeaseEntity l JOIN FETCH l.tenant t JOIN FETCH l.room r JOIN FETCH r.house h WHERE l.status = :status AND l.endDate BETWEEN :startDate AND :endDate")
    List<LeaseEntity> findByStatusAndEndDateBetween(@Param("status") LeaseStatus status,
            @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT l FROM LeaseEntity l WHERE l.room.id = :roomId AND l.status IN ('ACTIVE', 'PENDING') AND ((l.startDate <= :endDate AND l.endDate >= :startDate))")
    List<LeaseEntity> findOverlappingLeases(@Param("roomId") UUID roomId, @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("SELECT CASE WHEN COUNT(l) > 0 THEN true ELSE false END FROM LeaseEntity l WHERE l.room.id = :roomId AND l.status = :status AND l.endDate > :date")
    boolean existsActiveLeaseForRoomAfterDate(@Param("roomId") UUID roomId, @Param("status") LeaseStatus status,
            @Param("date") LocalDate date);
}