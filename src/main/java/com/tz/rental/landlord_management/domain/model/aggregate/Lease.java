package com.tz.rental.landlord_management.domain.model.aggregate;

import com.tz.rental.landlord_management.domain.model.valueobject.LeaseStatus;
import com.tz.rental.landlord_management.domain.model.valueobject.PaymentPeriod;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class Lease {

    private final LeaseId id;
    private final Tenant.TenantId tenantId;
    private final Room.RoomId roomId;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal rentAmount;
    private PaymentPeriod paymentPeriod;
    private LeaseStatus status;
    private String contractDocumentUrl;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public record LeaseId(UUID value) {
        public LeaseId {
            if (value == null) {
                throw new IllegalArgumentException("Lease ID cannot be null");
            }
        }
    }

    // Private constructor for internal use
    private Lease(LeaseId id, Tenant.TenantId tenantId, Room.RoomId roomId,
                  LocalDate startDate, LocalDate endDate, BigDecimal rentAmount,
                  PaymentPeriod paymentPeriod, LeaseStatus status, String contractDocumentUrl,
                  LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.tenantId = tenantId;
        this.roomId = roomId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.rentAmount = rentAmount;
        this.paymentPeriod = paymentPeriod;
        this.status = status;
        this.contractDocumentUrl = contractDocumentUrl;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        validate();
    }

    // Factory method - main way to create new leases
    public static Lease create(Tenant.TenantId tenantId, Room.RoomId roomId,
                               LocalDate startDate, LocalDate endDate,
                               BigDecimal rentAmount, PaymentPeriod paymentPeriod) {
        LeaseId id = new LeaseId(UUID.randomUUID());
        LeaseStatus initialStatus = determineInitialStatus(startDate);
        LocalDateTime now = LocalDateTime.now();

        return new Lease(id, tenantId, roomId, startDate, endDate, rentAmount,
                paymentPeriod, initialStatus, null, now, now);
    }

    // Factory method for existing leases (from database)
    public static Lease fromExisting(UUID id, UUID tenantId, UUID roomId,
                                     LocalDate startDate, LocalDate endDate,
                                     BigDecimal rentAmount, PaymentPeriod paymentPeriod,
                                     LeaseStatus status, String contractDocumentUrl,
                                     LocalDateTime createdAt, LocalDateTime updatedAt) {
        LeaseId leaseId = new LeaseId(id);
        Tenant.TenantId tenantIdVO = new Tenant.TenantId(tenantId);
        Room.RoomId roomIdVO = new Room.RoomId(roomId);

        return new Lease(leaseId, tenantIdVO, roomIdVO, startDate, endDate, rentAmount,
                paymentPeriod, status, contractDocumentUrl, createdAt, updatedAt);
    }

    public void terminate(LocalDate terminationDate) {
        if (status != LeaseStatus.ACTIVE) {
            throw new IllegalStateException("Only active leases can be terminated.");
        }
        if (terminationDate.isAfter(this.endDate) || terminationDate.isBefore(this.startDate)) {
            throw new IllegalArgumentException("Termination date must be within the lease period.");
        }
        this.endDate = terminationDate;
        this.status = LeaseStatus.TERMINATED;
        this.updatedAt = LocalDateTime.now();
    }

    public void attachContract(String url) {
        this.contractDocumentUrl = url;
        this.updatedAt = LocalDateTime.now();
    }

    private static LeaseStatus determineInitialStatus(LocalDate startDate) {
        return startDate.isAfter(LocalDate.now()) ? LeaseStatus.UPCOMING : LeaseStatus.ACTIVE;
    }

    private void validate() {
        if (tenantId == null || roomId == null) {
            throw new IllegalArgumentException("Tenant and Room IDs are required.");
        }
        if (startDate == null || endDate == null || startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date must be before end date.");
        }
        if (rentAmount == null || rentAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Rent amount must be a positive value.");
        }
    }
}