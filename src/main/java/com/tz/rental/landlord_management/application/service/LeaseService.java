package com.tz.rental.landlord_management.application.service;

import com.tz.rental.landlord_management.application.dto.CreateLeaseRequest;
import com.tz.rental.landlord_management.application.dto.LeaseResponse;
import com.tz.rental.landlord_management.domain.exception.NotFoundException;
import com.tz.rental.landlord_management.domain.exception.UnauthorizedException;
import com.tz.rental.landlord_management.domain.model.aggregate.*;
import com.tz.rental.landlord_management.domain.model.valueobject.NotificationType;
import com.tz.rental.landlord_management.domain.model.valueobject.RoomStatus;
import com.tz.rental.landlord_management.domain.repository.HouseRepository;
import com.tz.rental.landlord_management.domain.repository.LeaseRepository;
import com.tz.rental.landlord_management.domain.repository.RoomRepository;
import com.tz.rental.landlord_management.domain.repository.TenantRepository;
import com.tz.rental.landlord_management.domain.service.NotificationService;
import com.tz.rental.landlord_management.infrastructure.persistence.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LeaseService {

    private final LeaseRepository leaseRepository;
    private final RoomRepository roomRepository;
    private final TenantRepository tenantRepository;
    private final HouseRepository houseRepository; // Added for authorization
    private final PdfGenerationService pdfGenerationService;
    private final LeaseMapper applicationLeaseMapper;
    private final NotificationService notificationService;

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
    public LeaseResponse createLease(CreateLeaseRequest request) {
        Tenant tenant = tenantRepository.findById(request.getTenantId())
                .orElseThrow(() -> new NotFoundException("Tenant", request.getTenantId()));
        Room room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new NotFoundException("Room", request.getRoomId()));

        // Security Check: Ensure the landlord owns the house this room belongs to
        authorizeLandlordForLease(Lease.create(tenant.getId(), room.getId(), request.getStartDate(), request.getEndDate(), request.getRentAmount(), request.getPaymentPeriod()));

        if (room.getStatus() != RoomStatus.VACANT) {
            throw new IllegalStateException("Room is not vacant and cannot be leased.");
        }

        Lease lease = Lease.create(
                tenant.getId(),
                room.getId(),
                request.getStartDate(),
                request.getEndDate(),
                request.getRentAmount(),
                request.getPaymentPeriod()
        );
        Lease savedLease = leaseRepository.save(lease);

        room.changeStatus(RoomStatus.OCCUPIED);
        roomRepository.save(room);

        return applicationLeaseMapper.toResponse(savedLease);
    }

    @Transactional(readOnly = true)
    public LeaseResponse getLease(UUID id) {
        Lease lease = leaseRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Lease", id));
        authorizeLandlordForLease(lease); // Security check
        return applicationLeaseMapper.toResponse(lease);
    }

    @Transactional
    public LeaseResponse generateContract(UUID leaseId, UUID templateId) {
        Lease lease = leaseRepository.findById(leaseId)
                .orElseThrow(() -> new NotFoundException("Lease", leaseId));
        authorizeLandlordForLease(lease); // Security check

        Tenant tenant = tenantRepository.findById(lease.getTenantId().value())
                .orElseThrow(() -> new NotFoundException("Tenant", lease.getTenantId().value()));

        try {
            String filePath = pdfGenerationService.generateContract(lease, tenant, templateId);
            lease.attachContract(filePath);
            Lease updatedLease = leaseRepository.save(lease);
            return applicationLeaseMapper.toResponse(updatedLease);
        } catch (IOException e) {
            throw new RuntimeException("Could not generate or save PDF contract", e);
        }
    }

    @Transactional
    public LeaseResponse shareContractViaWhatsApp(UUID leaseId) {
        Lease lease = leaseRepository.findById(leaseId)
                .orElseThrow(() -> new NotFoundException("Lease", leaseId));
        authorizeLandlordForLease(lease); // Security check

        Tenant tenant = tenantRepository.findById(lease.getTenantId().value())
                .orElseThrow(() -> new NotFoundException("Tenant", lease.getTenantId().value()));

        if (lease.getContractDocumentUrl() == null || lease.getContractDocumentUrl().isEmpty()) {
            throw new IllegalStateException("No contract document URL found for this lease. Generate the contract first.");
        }

        String message = String.format("Dear %s, your rental contract is available here: %s",
                tenant.getFirstName(), lease.getContractDocumentUrl());

        notificationService.sendNotification(tenant.getPhoneNumber().getValue(), message, NotificationType.CONTRACT_SHARED);

        return applicationLeaseMapper.toResponse(lease);
    }
}