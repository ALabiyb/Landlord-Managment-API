package com.tz.rental.landlord_management.infrastructure.persistence.repository.jpa;

import com.tz.rental.landlord_management.domain.model.valueobject.PaymentStatus;
import com.tz.rental.landlord_management.infrastructure.persistence.entity.PaymentEntity;
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
public interface JpaPaymentRepository extends JpaRepository<PaymentEntity, UUID> {
    List<PaymentEntity> findByLeaseId(UUID leaseId);

    @Query("SELECT SUM(p.amountPaid) FROM PaymentEntity p WHERE p.lease.id IN :leaseIds AND p.paymentDate BETWEEN :start AND :end")
    BigDecimal sumAmountByLeaseIdInAndPaymentDateBetween(@Param("leaseIds") List<UUID> leaseIds,
            @Param("start") LocalDate start, @Param("end") LocalDate end);

    // ===== New Methods for Full CRUD =====

    @Query("SELECT p FROM PaymentEntity p JOIN FETCH p.lease l JOIN FETCH l.tenant t JOIN FETCH l.room r JOIN FETCH r.house h WHERE h.landlord.id = :landlordId")
    Page<PaymentEntity> findByLandlordIdPaginated(@Param("landlordId") UUID landlordId, Pageable pageable);

    @Query("SELECT p FROM PaymentEntity p JOIN FETCH p.lease l JOIN FETCH l.tenant t JOIN FETCH l.room r JOIN FETCH r.house h WHERE p.id = :paymentId AND h.landlord.id = :landlordId")
    Optional<PaymentEntity> findByIdAndLandlordId(@Param("paymentId") UUID paymentId,
            @Param("landlordId") UUID landlordId);

    @Query("SELECT p FROM PaymentEntity p JOIN FETCH p.lease l JOIN FETCH l.tenant t JOIN FETCH l.room r JOIN FETCH r.house h WHERE h.landlord.id = :landlordId AND p.status = :status")
    Page<PaymentEntity> findByLandlordIdAndStatusPaginated(@Param("landlordId") UUID landlordId,
            @Param("status") PaymentStatus status, Pageable pageable);

    @Query("SELECT COALESCE(SUM(p.amountPaid), 0) FROM PaymentEntity p JOIN p.lease l JOIN l.room r JOIN r.house h WHERE h.landlord.id = :landlordId AND p.status = 'PAID' AND p.paymentDate BETWEEN :startDate AND :endDate")
    BigDecimal calculateTotalRevenue(@Param("landlordId") UUID landlordId, @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
}