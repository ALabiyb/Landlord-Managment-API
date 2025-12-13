package com.tz.rental.landlord_management.infrastructure.persistence.mapper;

import com.tz.rental.landlord_management.domain.model.aggregate.Lease;
import com.tz.rental.landlord_management.domain.model.aggregate.Payment;
import com.tz.rental.landlord_management.infrastructure.persistence.entity.LeaseEntity;
import com.tz.rental.landlord_management.infrastructure.persistence.entity.PaymentEntity;
import org.springframework.stereotype.Component;

@Component("persistencePaymentMapper")
public class PaymentMapper {

    public PaymentEntity toEntity(Payment payment) {
        PaymentEntity entity = new PaymentEntity();
        entity.setId(payment.getId().value());

        LeaseEntity leaseEntity = new LeaseEntity();
        leaseEntity.setId(payment.getLeaseId().value());
        entity.setLease(leaseEntity);

        entity.setAmountPaid(payment.getAmountPaid());
        entity.setPaymentDate(payment.getPaymentDate());
        entity.setStatus(payment.getStatus());
        entity.setTransactionReference(payment.getTransactionReference());
        return entity;
    }

    public Payment toDomain(PaymentEntity entity) {
        // Note: This simplified mapping assumes the Lease object is not fully loaded
        // A more complete solution might involve fetching the full Lease aggregate
        Payment payment = Payment.create(
                new Lease.LeaseId(entity.getLease().getId()),
                entity.getAmountPaid(),
                entity.getPaymentDate(),
                entity.getTransactionReference()
        );
        // If status can be updated, you might need to call a method on the domain object
        // payment.setStatus(entity.getStatus()); // Assuming a setter or internal method
        return payment;
    }
}