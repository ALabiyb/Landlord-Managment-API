package com.tz.rental.landlord_management.application.service;

import com.tz.rental.landlord_management.application.dto.CreatePaymentRequest;
import com.tz.rental.landlord_management.application.dto.PaymentResponse;
import com.tz.rental.landlord_management.application.dto.UpdatePaymentRequest;
import com.tz.rental.landlord_management.application.mapper.PaymentMapper;
import com.tz.rental.landlord_management.domain.exception.NotFoundException;
import com.tz.rental.landlord_management.domain.model.valueobject.PaymentStatus;
import com.tz.rental.landlord_management.domain.model.valueobject.LeaseStatus;
import com.tz.rental.landlord_management.infrastructure.persistence.entity.LeaseEntity;
import com.tz.rental.landlord_management.infrastructure.persistence.entity.PaymentEntity;
import com.tz.rental.landlord_management.infrastructure.persistence.repository.jpa.JpaLeaseRepository;
import com.tz.rental.landlord_management.infrastructure.persistence.repository.jpa.JpaPaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Service for managing payments.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class JpaPaymentService {

    private final JpaPaymentRepository paymentRepository;
    private final JpaLeaseRepository leaseRepository;
    private final PaymentMapper paymentMapper;

    /**
     * Record a new payment.
     */
    @Transactional
    public PaymentResponse createPayment(UUID landlordId, CreatePaymentRequest request) {
        log.info("Recording payment for lease {}", request.getLeaseId());

        // Validate lease exists and belongs to landlord
        LeaseEntity lease = leaseRepository.findByIdAndLandlordId(request.getLeaseId(), landlordId)
                .orElseThrow(() -> new NotFoundException("Lease not found or access denied"));

        // Create payment
        PaymentEntity payment = new PaymentEntity();
        payment.setLease(lease);
        payment.setAmountPaid(request.getAmountPaid());
        payment.setPaymentDate(request.getPaymentDate());
        payment.setDueDate(request.getDueDate());
        payment.setStatus(request.getStatus());
        payment.setTransactionReference(request.getTransactionReference());
        payment.setRemarks(request.getRemarks());

        PaymentEntity savedPayment = paymentRepository.save(payment);
        log.info("Successfully recorded payment {}", savedPayment.getId());

        // Business Rule: Activate lease on first PAID payment
        if (request.getStatus() == PaymentStatus.PAID && lease.getStatus() == LeaseStatus.PENDING) {
            lease.setStatus(LeaseStatus.ACTIVE);
            leaseRepository.save(lease);

            // Update room status to OCCUPIED
            RoomEntity room = lease.getRoom();
            room.setStatus(RoomStatus.OCCUPIED);
            roomRepository.save(room);

            log.info("Lease {} automatically activated and Room {} set to OCCUPIED", lease.getId(), room.getId());
        }

        return paymentMapper.toResponse(savedPayment);
    }

    /**
     * Update an existing payment.
     */
    @Transactional
    public PaymentResponse updatePayment(UUID landlordId, UUID paymentId, UpdatePaymentRequest request) {
        log.info("Updating payment {}", paymentId);

        PaymentEntity payment = paymentRepository.findByIdAndLandlordId(paymentId, landlordId)
                .orElseThrow(() -> new NotFoundException("Payment not found or access denied"));

        if (request.getAmountPaid() != null) {
            payment.setAmountPaid(request.getAmountPaid());
        }
        if (request.getPaymentDate() != null) {
            payment.setPaymentDate(request.getPaymentDate());
        }
        if (request.getStatus() != null) {
            payment.setStatus(request.getStatus());

            // Business Rule: Activate lease on status changing to PAID
            LeaseEntity lease = payment.getLease();
            if (request.getStatus() == PaymentStatus.PAID && lease.getStatus() == LeaseStatus.PENDING) {
                lease.setStatus(LeaseStatus.ACTIVE);
                leaseRepository.save(lease);

                // Update room status to OCCUPIED
                RoomEntity room = lease.getRoom();
                room.setStatus(RoomStatus.OCCUPIED);
                roomRepository.save(room);

                log.info("Lease {} automatically activated and Room {} set to OCCUPIED via payment update",
                        lease.getId(), room.getId());
            }
        }
        if (request.getDueDate() != null) {
            payment.setDueDate(request.getDueDate());
        }
        if (request.getTransactionReference() != null) {
            payment.setTransactionReference(request.getTransactionReference());
        }
        if (request.getRemarks() != null) {
            payment.setRemarks(request.getRemarks());
        }

        PaymentEntity updatedPayment = paymentRepository.save(payment);
        return paymentMapper.toResponse(updatedPayment);
    }

    /**
     * Get a payment by ID.
     */
    @Transactional(readOnly = true)
    public PaymentResponse getPayment(UUID landlordId, UUID paymentId) {
        PaymentEntity payment = paymentRepository.findByIdAndLandlordId(paymentId, landlordId)
                .orElseThrow(() -> new NotFoundException("Payment not found or access denied"));
        return paymentMapper.toResponse(payment);
    }

    /**
     * Get all payments for a landlord.
     */
    @Transactional(readOnly = true)
    public Page<PaymentResponse> getAllPayments(UUID landlordId, Pageable pageable) {
        return paymentRepository.findByLandlordIdPaginated(landlordId, pageable)
                .map(paymentMapper::toResponse);
    }

    /**
     * Get payments by status.
     */
    @Transactional(readOnly = true)
    public Page<PaymentResponse> getPaymentsByStatus(UUID landlordId, PaymentStatus status, Pageable pageable) {
        return paymentRepository.findByLandlordIdAndStatusPaginated(landlordId, status, pageable)
                .map(paymentMapper::toResponse);
    }

    /**
     * Get payments for a specific lease.
     */
    @Transactional(readOnly = true)
    public List<PaymentResponse> getPaymentsByLease(UUID landlordId, UUID leaseId) {
        // Validation: Ensure lease belongs to landlord
        leaseRepository.findByIdAndLandlordId(leaseId, landlordId)
                .orElseThrow(() -> new NotFoundException("Lease not found or access denied"));

        return paymentRepository.findByLeaseId(leaseId).stream()
                .map(paymentMapper::toResponse)
                .toList();
    }

    /**
     * Calculate monthly revenue.
     */
    @Transactional(readOnly = true)
    public BigDecimal calculateMonthlyRevenue(UUID landlordId, int year, int month) {
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.plusMonths(1).minusDays(1);
        return paymentRepository.calculateTotalRevenue(landlordId, start, end);
    }
}
