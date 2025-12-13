package com.tz.rental.landlord_management.application.service;

import com.tz.rental.landlord_management.application.dto.CreatePaymentRequest;
import com.tz.rental.landlord_management.application.dto.PaymentResponse;
import com.tz.rental.landlord_management.domain.exception.NotFoundException;
import com.tz.rental.landlord_management.domain.exception.UnauthorizedException;
import com.tz.rental.landlord_management.domain.model.aggregate.*;
import com.tz.rental.landlord_management.domain.model.valueobject.PaymentStatus;
import com.tz.rental.landlord_management.domain.repository.HouseRepository;
import com.tz.rental.landlord_management.domain.repository.LeaseRepository;
import com.tz.rental.landlord_management.domain.repository.PaymentRepository;
import com.tz.rental.landlord_management.domain.repository.RoomRepository;
import com.tz.rental.landlord_management.infrastructure.persistence.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final LeaseRepository leaseRepository;
    private final RoomRepository roomRepository; // Added for authorization
    private final HouseRepository houseRepository; // Added for authorization
    @Qualifier("applicationPaymentMapper")
    private final PaymentMapper applicationPaymentMapper;

    private Landlord.LandlordId getCurrentLandlordId() {
        UserEntity currentUser = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (currentUser.getLandlord() == null) {
            throw new IllegalStateException("The current user is not a landlord.");
        }
        return new Landlord.LandlordId(currentUser.getLandlord().getId());
    }

    private void authorizeLandlordForLease(Lease lease) {
        Room room = roomRepository.findById(lease.getRoomId().value())
                .orElseThrow(() -> new NotFoundException("Room", lease.getRoomId().value()));
        House house = houseRepository.findById(room.getHouseId())
                .orElseThrow(() -> new NotFoundException("House", room.getHouseId().value()));

        if (!house.getLandlordId().equals(getCurrentLandlordId())) {
            throw new UnauthorizedException("You are not authorized to manage this lease.");
        }
    }

    @Transactional
    public PaymentResponse createPayment(CreatePaymentRequest request) {
        Lease lease = leaseRepository.findById(request.getLeaseId())
                .orElseThrow(() -> new NotFoundException("Lease", request.getLeaseId()));
        authorizeLandlordForLease(lease); // Security check

        Payment payment = Payment.create(
                lease.getId(),
                request.getAmountPaid(),
                request.getPaymentDate(),
                request.getTransactionReference()
        );
        Payment savedPayment = paymentRepository.save(payment);
        return applicationPaymentMapper.toResponse(savedPayment);
    }

    @Transactional(readOnly = true)
    public PaymentResponse getPayment(UUID id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Payment", id));
        Lease lease = leaseRepository.findById(payment.getLeaseId().value())
                .orElseThrow(() -> new NotFoundException("Lease", payment.getLeaseId().value()));
        authorizeLandlordForLease(lease); // Security check
        return applicationPaymentMapper.toResponse(payment);
    }

    @Transactional(readOnly = true)
    public List<PaymentResponse> getPaymentsByLeaseId(UUID leaseId) {
        Lease lease = leaseRepository.findById(leaseId)
                .orElseThrow(() -> new NotFoundException("Lease", leaseId));
        authorizeLandlordForLease(lease); // Security check

        return paymentRepository.findByLeaseId(leaseId).stream()
                .map(applicationPaymentMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public PaymentResponse updatePayment(UUID id, CreatePaymentRequest request) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Payment", id));
        Lease lease = leaseRepository.findById(payment.getLeaseId().value())
                .orElseThrow(() -> new NotFoundException("Lease", payment.getLeaseId().value()));
        authorizeLandlordForLease(lease); // Security check

        payment.updatePayment(
                request.getAmountPaid(),
                request.getPaymentDate(),
                request.getTransactionReference(),
                PaymentStatus.PAID
        );
        Payment updatedPayment = paymentRepository.save(payment);
        return applicationPaymentMapper.toResponse(updatedPayment);
    }

    @Transactional
    public void deletePayment(UUID id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Payment", id));
        Lease lease = leaseRepository.findById(payment.getLeaseId().value())
                .orElseThrow(() -> new NotFoundException("Lease", payment.getLeaseId().value()));
        authorizeLandlordForLease(lease); // Security check
        paymentRepository.deleteById(id);
    }
}