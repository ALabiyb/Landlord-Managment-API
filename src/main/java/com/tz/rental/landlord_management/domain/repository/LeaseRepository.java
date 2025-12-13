package com.tz.rental.landlord_management.domain.repository;

import com.tz.rental.landlord_management.domain.model.aggregate.Landlord;
import com.tz.rental.landlord_management.domain.model.aggregate.Lease;
import com.tz.rental.landlord_management.domain.model.valueobject.LeaseStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LeaseRepository {
    Lease save(Lease lease);
    Optional<Lease> findById(UUID id);
    List<Lease> findAll();
    List<Lease> findByTenantId(UUID tenantId);
    List<Lease> findByLandlordId(Landlord.LandlordId landlordId); // Added this method
    Optional<Lease> findByRoomIdAndStatus(UUID roomId, String status);
    boolean isRoomOccupied(UUID roomId);
    List<Lease> findAllByStatus(LeaseStatus status);
}