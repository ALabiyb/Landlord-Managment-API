package com.tz.rental.landlord_management.domain.repository;

import com.tz.rental.landlord_management.domain.model.aggregate.Payment;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository {
    Payment save(Payment payment);
    Optional<Payment> findById(UUID id);
    List<Payment> findByLeaseId(UUID leaseId);
    BigDecimal sumAmountByLeaseIdInAndPaymentDateBetween(List<UUID> leaseIds, LocalDate start, LocalDate end); // New method
    void deleteById(UUID id);
}