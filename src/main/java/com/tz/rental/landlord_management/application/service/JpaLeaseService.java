package com.tz.rental.landlord_management.application.service;

import com.tz.rental.landlord_management.application.dto.CreateLeaseRequest;
import com.tz.rental.landlord_management.application.dto.LeaseResponse;
import com.tz.rental.landlord_management.application.dto.UpdateLeaseRequest;
import com.tz.rental.landlord_management.application.mapper.LeaseMapper;
import com.tz.rental.landlord_management.domain.exception.NotFoundException;
import com.tz.rental.landlord_management.domain.model.valueobject.LeaseStatus;
import com.tz.rental.landlord_management.domain.model.valueobject.RoomStatus;
import com.tz.rental.landlord_management.infrastructure.persistence.entity.LeaseEntity;
import com.tz.rental.landlord_management.infrastructure.persistence.entity.RoomEntity;
import com.tz.rental.landlord_management.infrastructure.persistence.entity.TenantEntity;
import com.tz.rental.landlord_management.infrastructure.persistence.repository.jpa.JpaLeaseRepository;
import com.tz.rental.landlord_management.infrastructure.persistence.repository.jpa.JpaRoomRepository;
import com.tz.rental.landlord_management.infrastructure.persistence.repository.jpa.JpaTenantRepository;
import com.tz.rental.landlord_management.infrastructure.persistence.entity.ContractTemplateEntity;
import com.tz.rental.landlord_management.infrastructure.persistence.repository.jpa.JpaContractTemplateRepository;
import com.tz.rental.landlord_management.infrastructure.persistence.repository.jpa.JpaPaymentRepository;
import com.tz.rental.landlord_management.domain.model.valueobject.PaymentStatus;
import com.tz.rental.landlord_management.domain.model.valueobject.NotificationType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Service for managing lease operations with comprehensive business validation.
 * Implements full CRUD operations with landlord-scoped access control.
 */
@Service("jpaLeaseService")
@RequiredArgsConstructor
@Slf4j
public class JpaLeaseService {

    private final JpaLeaseRepository leaseRepository;
    private final JpaRoomRepository roomRepository;
    private final JpaTenantRepository tenantRepository;
    private final LeaseMapper leaseMapper;
    private final JpaContractTemplateRepository contractTemplateRepository;
    private final PdfGenerationService pdfGenerationService;
    private final WhatsAppNotificationService whatsappService;
    private final JpaPaymentRepository paymentRepository;

    /**
     * Create a new lease with comprehensive validation.
     *
     * @param landlordId The ID of the landlord creating the lease
     * @param request    The lease creation request
     * @return LeaseResponse with created lease details
     * @throws IllegalArgumentException if validation fails
     * @throws NotFoundException        if tenant or room not found
     */
    @Transactional
    public LeaseResponse createLease(UUID landlordId, CreateLeaseRequest request) {
        log.info("Creating lease for landlord {} - Tenant: {}, Room: {}",
                landlordId, request.getTenantId(), request.getRoomId());

        // 1. Validate tenant exists and belongs to landlord
        TenantEntity tenant = validateTenant(landlordId, request.getTenantId());

        // 2. Validate room exists and belongs to landlord
        RoomEntity room = validateRoom(landlordId, request.getRoomId());

        // 3. Validate room status (must be VACANT, not MAINTENANCE or OCCUPIED)
        validateRoomStatus(room);

        // 4. Validate no overlapping leases
        validateNoOverlappingLeases(request.getRoomId(), request.getStartDate(), request.getEndDate(), null);

        // 5. Create lease entity
        LeaseEntity lease = new LeaseEntity();
        lease.setTenant(tenant);
        lease.setRoom(room);
        lease.setStartDate(request.getStartDate());
        lease.setEndDate(request.getEndDate());
        lease.setRentAmount(request.getRentAmount());
        lease.setPaymentPeriod(request.getPaymentPeriod());
        lease.setStatus(LeaseStatus.PENDING); // New leases start as PENDING
        lease.setContractDocumentUrl(request.getContractDocumentUrl());

        // 6. Save lease
        LeaseEntity savedLease = leaseRepository.save(lease);

        // 7. Update room status to RESERVED (until first payment)
        room.setStatus(RoomStatus.RESERVED);
        roomRepository.save(room);

        log.info("Successfully created lease {} for room {}", savedLease.getId(), room.getId());
        return leaseMapper.toResponse(savedLease);
    }

    /**
     * Update an existing lease.
     *
     * @param landlordId The ID of the landlord
     * @param leaseId    The ID of the lease to update
     * @param request    The update request
     * @return Updated lease response
     */
    @Transactional
    public LeaseResponse updateLease(UUID landlordId, UUID leaseId, UpdateLeaseRequest request) {
        log.info("Updating lease {} for landlord {}", leaseId, landlordId);

        // Find and validate lease belongs to landlord
        LeaseEntity lease = leaseRepository.findByIdAndLandlordId(leaseId, landlordId)
                .orElseThrow(() -> new NotFoundException("Lease not found or you don't have permission to access it"));

        // Update fields if provided
        if (request.getEndDate() != null) {
            // Validate new end date
            if (request.getEndDate().isBefore(lease.getStartDate())) {
                throw new IllegalArgumentException("End date must be after start date");
            }
            // Check for overlaps with new end date
            validateNoOverlappingLeases(lease.getRoom().getId(), lease.getStartDate(),
                    request.getEndDate(), leaseId);
            lease.setEndDate(request.getEndDate());
        }

        if (request.getRentAmount() != null) {
            lease.setRentAmount(request.getRentAmount());
        }

        if (request.getPaymentPeriod() != null) {
            lease.setPaymentPeriod(request.getPaymentPeriod());
        }

        if (request.getStatus() != null) {
            // Handle status transitions
            handleStatusTransition(lease, request.getStatus());
        }

        if (request.getContractDocumentUrl() != null) {
            lease.setContractDocumentUrl(request.getContractDocumentUrl());
        }

        LeaseEntity updatedLease = leaseRepository.save(lease);
        log.info("Successfully updated lease {}", leaseId);
        return leaseMapper.toResponse(updatedLease);
    }

    /**
     * Get a single lease by ID.
     */
    @Transactional(readOnly = true)
    public LeaseResponse getLease(UUID landlordId, UUID leaseId) {
        LeaseEntity lease = leaseRepository.findByIdAndLandlordId(leaseId, landlordId)
                .orElseThrow(() -> new NotFoundException("Lease not found or you don't have permission to access it"));
        return leaseMapper.toResponse(lease);
    }

    /**
     * Get all leases for a landlord with pagination.
     */
    @Transactional(readOnly = true)
    public Page<LeaseResponse> getAllLeases(UUID landlordId, Pageable pageable) {
        Page<LeaseEntity> leases = leaseRepository.findByLandlordIdPaginated(landlordId, pageable);
        return leases.map(leaseMapper::toResponse);
    }

    /**
     * Get leases by status for a landlord.
     */
    @Transactional(readOnly = true)
    public Page<LeaseResponse> getLeasesByStatus(UUID landlordId, LeaseStatus status, Pageable pageable) {
        Page<LeaseEntity> leases = leaseRepository.findByLandlordIdAndStatusPaginated(landlordId, status, pageable);
        return leases.map(leaseMapper::toResponse);
    }

    /**
     * Get all leases for a specific room.
     */
    @Transactional(readOnly = true)
    public List<LeaseResponse> getLeasesByRoom(UUID landlordId, UUID roomId) {
        // Validate room belongs to landlord
        validateRoom(landlordId, roomId);

        List<LeaseEntity> leases = leaseRepository.findByRoomIdWithDetails(roomId);
        return leases.stream()
                .map(leaseMapper::toResponse)
                .toList();
    }

    /**
     * Terminate a lease early.
     */
    @Transactional
    public LeaseResponse terminateLease(UUID landlordId, UUID leaseId, LocalDate terminationDate) {
        log.info("Terminating lease {} on {}", leaseId, terminationDate);

        LeaseEntity lease = leaseRepository.findByIdAndLandlordId(leaseId, landlordId)
                .orElseThrow(() -> new NotFoundException("Lease not found or you don't have permission to access it"));

        if (terminationDate.isBefore(lease.getStartDate())) {
            throw new IllegalArgumentException("Termination date cannot be before lease start date");
        }

        if (terminationDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Termination date cannot be in the past");
        }

        lease.setEndDate(terminationDate);
        lease.setStatus(LeaseStatus.TERMINATED);

        // Update room status to VACANT
        RoomEntity room = lease.getRoom();
        room.setStatus(RoomStatus.VACANT);
        roomRepository.save(room);

        LeaseEntity updatedLease = leaseRepository.save(lease);
        log.info("Successfully terminated lease {}", leaseId);
        return leaseMapper.toResponse(updatedLease);
    }

    /**
     * Delete a lease (soft delete by changing status).
     */
    @Transactional
    public void deleteLease(UUID landlordId, UUID leaseId) {
        log.info("Deleting lease {} for landlord {}", leaseId, landlordId);

        LeaseEntity lease = leaseRepository.findByIdAndLandlordId(leaseId, landlordId)
                .orElseThrow(() -> new NotFoundException("Lease not found or you don't have permission to access it"));

        // Only allow deletion of PENDING leases
        if (lease.getStatus() == LeaseStatus.ACTIVE) {
            throw new IllegalStateException("Cannot delete an active lease. Terminate it first.");
        }

        // If lease was occupying a room, free it up
        if (lease.getStatus() == LeaseStatus.PENDING) {
            RoomEntity room = lease.getRoom();
            room.setStatus(RoomStatus.VACANT);
            roomRepository.save(room);
        }

        leaseRepository.delete(lease);
        log.info("Successfully deleted lease {}", leaseId);
    }

    /**
     * Generate contract PDF for a lease.
     */
    @Transactional(readOnly = true)
    public byte[] downloadContract(UUID landlordId, UUID leaseId) {
        LeaseEntity lease = leaseRepository.findByIdAndLandlordId(leaseId, landlordId)
                .orElseThrow(() -> new NotFoundException("Lease not found or you don't have permission to access it"));

        // Business Rule: Check if at least one payment is PAID before allowing download
        boolean hasPaidPayment = paymentRepository.findByLeaseId(leaseId).stream()
                .anyMatch(p -> p.getStatus() == PaymentStatus.PAID);

        if (!hasPaidPayment) {
            throw new IllegalStateException(
                    "Lease contract cannot be downloaded until at least one payment is confirmed (PAID).");
        }

        TenantEntity tenant = lease.getTenant();

        // Retrieve the default template or the one associated with the lease (if we had
        // that field).
        // For now, get the first active template or throw error.
        ContractTemplateEntity template = contractTemplateRepository.findAll().stream()
                .filter(ContractTemplateEntity::isActive)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        "No active contract templates found. Please create one first."));

        try {
            return pdfGenerationService.generateContractPdf(lease, tenant, template);
        } catch (java.io.IOException e) {
            log.error("Failed to generate PDF contract for lease {}", leaseId, e);
            throw new RuntimeException("Failed to generate contract PDF", e);
        }
    }

    /**
     * Share lease contract via WhatsApp.
     */
    @Transactional(readOnly = true)
    public void shareContractViaWhatsApp(UUID landlordId, UUID leaseId) {
        LeaseEntity lease = leaseRepository.findByIdAndLandlordId(leaseId, landlordId)
                .orElseThrow(() -> new NotFoundException("Lease not found or access denied"));

        TenantEntity tenant = lease.getTenant();
        String message = String.format(
                "Hello %s, your lease contract for room %s is ready. You can view it here: %s",
                tenant.getFirstName(),
                lease.getRoom().getRoomNumber(),
                lease.getContractDocumentUrl() != null ? lease.getContractDocumentUrl() : "Pending upload");

        whatsappService.sendNotification(tenant.getPhoneNumber(), message, NotificationType.CONTRACT_SHARED);
        log.info("Shared contract for lease {} with tenant via WhatsApp", leaseId);
    }

    // ===== Private Validation Methods =====

    /**
     * Validate tenant exists, is active, and belongs to the landlord.
     */
    private TenantEntity validateTenant(UUID landlordId, UUID tenantId) {
        TenantEntity tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new NotFoundException("Tenant not found with ID: " + tenantId));

        if (!tenant.getLandlord().getId().equals(landlordId)) {
            throw new IllegalArgumentException("Tenant does not belong to this landlord");
        }

        if (!tenant.isActive()) {
            throw new IllegalArgumentException("Tenant is not active and cannot be assigned to a lease");
        }

        return tenant;
    }

    /**
     * Validate room exists and belongs to the landlord.
     */
    private RoomEntity validateRoom(UUID landlordId, UUID roomId) {
        RoomEntity room = roomRepository.findById(roomId)
                .orElseThrow(() -> new NotFoundException("Room not found with ID: " + roomId));

        if (!room.getHouse().getLandlord().getId().equals(landlordId)) {
            throw new IllegalArgumentException("Room does not belong to this landlord");
        }

        return room;
    }

    /**
     * Validate room status is VACANT.
     */
    private void validateRoomStatus(RoomEntity room) {
        if (room.getStatus() == RoomStatus.OCCUPIED) {
            throw new IllegalStateException(
                    String.format("Room %s is already occupied and cannot be leased", room.getRoomNumber()));
        }

        if (room.getStatus() == RoomStatus.MAINTENANCE) {
            throw new IllegalStateException(
                    String.format("Room %s is under maintenance and cannot be leased", room.getRoomNumber()));
        }
    }

    /**
     * Validate no overlapping leases exist for the room.
     */
    private void validateNoOverlappingLeases(UUID roomId, LocalDate startDate, LocalDate endDate, UUID excludeLeaseId) {
        List<LeaseEntity> overlappingLeases = leaseRepository.findOverlappingLeases(roomId, startDate, endDate);

        // Filter out the current lease if updating
        if (excludeLeaseId != null) {
            overlappingLeases = overlappingLeases.stream()
                    .filter(lease -> !lease.getId().equals(excludeLeaseId))
                    .toList();
        }

        if (!overlappingLeases.isEmpty()) {
            LeaseEntity conflictingLease = overlappingLeases.get(0);
            throw new IllegalStateException(
                    String.format("Room already has a lease from %s to %s that overlaps with the requested dates",
                            conflictingLease.getStartDate(), conflictingLease.getEndDate()));
        }
    }

    /**
     * Handle lease status transitions with business rules.
     */
    private void handleStatusTransition(LeaseEntity lease, LeaseStatus newStatus) {
        LeaseStatus currentStatus = lease.getStatus();

        // Validate allowed transitions
        if (currentStatus == LeaseStatus.EXPIRED && newStatus != LeaseStatus.EXPIRED) {
            throw new IllegalStateException("Cannot change status of an expired lease");
        }

        if (currentStatus == LeaseStatus.TERMINATED && newStatus != LeaseStatus.TERMINATED) {
            throw new IllegalStateException("Cannot change status of a terminated lease");
        }

        // Update room status based on lease status
        RoomEntity room = lease.getRoom();
        if (newStatus == LeaseStatus.ACTIVE && currentStatus != LeaseStatus.ACTIVE) {
            room.setStatus(RoomStatus.OCCUPIED);
            roomRepository.save(room);
        } else if ((newStatus == LeaseStatus.EXPIRED || newStatus == LeaseStatus.TERMINATED)
                && currentStatus == LeaseStatus.ACTIVE) {
            room.setStatus(RoomStatus.VACANT);
            roomRepository.save(room);
        }

        lease.setStatus(newStatus);
    }
}
