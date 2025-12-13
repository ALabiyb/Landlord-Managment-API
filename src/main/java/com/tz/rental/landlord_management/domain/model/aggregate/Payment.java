package com.tz.rental.landlord_management.domain.model.aggregate;

import com.tz.rental.landlord_management.domain.model.valueobject.PaymentStatus;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class Payment {

    private final PaymentId id;
    private final Lease.LeaseId leaseId;
    private BigDecimal amountPaid;
    private LocalDate paymentDate;
    private PaymentStatus status;
    private String transactionReference;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public record PaymentId(UUID value) {
        public PaymentId {
            if (value == null) {
                throw new IllegalArgumentException("Payment ID cannot be null");
            }
        }
    }

    private Payment(PaymentId id, Lease.LeaseId leaseId, BigDecimal amountPaid, LocalDate paymentDate, String transactionReference) {
        this.id = id;
        this.leaseId = leaseId;
        this.amountPaid = amountPaid;
        this.paymentDate = paymentDate;
        this.transactionReference = transactionReference;
        this.status = PaymentStatus.PAID; // Default to PAID for new payments
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        validate();
    }

    public static Payment create(Lease.LeaseId leaseId, BigDecimal amountPaid, LocalDate paymentDate, String transactionReference) {
        return new Payment(new PaymentId(UUID.randomUUID()), leaseId, amountPaid, paymentDate, transactionReference);
    }

    public void updatePayment(BigDecimal newAmountPaid, LocalDate newPaymentDate, String newTransactionReference, PaymentStatus newStatus) {
        this.amountPaid = newAmountPaid;
        this.paymentDate = newPaymentDate;
        this.transactionReference = newTransactionReference;
        this.status = newStatus;
        this.updatedAt = LocalDateTime.now();
        validate();
    }

    private void validate() {
        if (leaseId == null) {
            throw new IllegalArgumentException("Lease ID is required for a payment.");
        }
        if (amountPaid == null || amountPaid.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount paid must be a positive value.");
        }
        if (paymentDate == null) {
            throw new IllegalArgumentException("Payment date is required.");
        }
    }
}