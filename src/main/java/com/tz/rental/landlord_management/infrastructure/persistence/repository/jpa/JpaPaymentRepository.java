package com.tz.rental.landlord_management.infrastructure.persistence.repository.jpa;

import com.tz.rental.landlord_management.infrastructure.persistence.entity.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface JpaPaymentRepository extends JpaRepository<PaymentEntity, UUID> {
    List<PaymentEntity> findByLeaseId(UUID leaseId);

    @Query("SELECT SUM(p.amountPaid) FROM PaymentEntity p WHERE p.lease.id IN :leaseIds AND p.paymentDate BETWEEN :start AND :end")
    BigDecimal sumAmountByLeaseIdInAndPaymentDateBetween(@Param("leaseIds") List<UUID> leaseIds, @Param("start") LocalDate start, @Param("end") LocalDate end);
}