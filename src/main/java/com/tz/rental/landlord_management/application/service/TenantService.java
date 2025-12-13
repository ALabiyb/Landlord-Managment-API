package com.tz.rental.landlord_management.application.service;

import com.tz.rental.landlord_management.application.dto.CreateTenantRequest;
import com.tz.rental.landlord_management.application.dto.TenantResponse;
import com.tz.rental.landlord_management.domain.exception.AlreadyExistsException; // Added import
import com.tz.rental.landlord_management.domain.exception.NotFoundException;
import com.tz.rental.landlord_management.domain.exception.UnauthorizedException;
import com.tz.rental.landlord_management.domain.model.aggregate.House;
import com.tz.rental.landlord_management.domain.model.aggregate.Landlord;
import com.tz.rental.landlord_management.domain.model.aggregate.Lease;
import com.tz.rental.landlord_management.domain.model.aggregate.Room;
import com.tz.rental.landlord_management.domain.model.aggregate.Tenant;
import com.tz.rental.landlord_management.domain.model.valueobject.LeaseStatus;
import com.tz.rental.landlord_management.domain.repository.HouseRepository;
import com.tz.rental.landlord_management.domain.repository.LeaseRepository;
import com.tz.rental.landlord_management.domain.repository.RoomRepository;
import com.tz.rental.landlord_management.domain.repository.TenantRepository;
import com.tz.rental.landlord_management.infrastructure.persistence.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TenantService {

    private final TenantRepository tenantRepository;
    private final LeaseRepository leaseRepository; // Added for authorization
    private final RoomRepository roomRepository; // Added for authorization
    private final HouseRepository houseRepository; // Added for authorization
    private final TenantMapper applicationTenantMapper;

    private Landlord.LandlordId getCurrentLandlordId() {
        UserEntity currentUser = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (currentUser.getLandlord() == null) {
            throw new IllegalStateException("The current user is not a landlord.");
        }
        return new Landlord.LandlordId(currentUser.getLandlord().getId());
    }

    // Helper method to check if a tenant belongs to the current landlord
    private void authorizeLandlordForTenant(UUID tenantId) {
        Landlord.LandlordId currentLandlordId = getCurrentLandlordId();
        // Find any active lease for this tenant that belongs to the current landlord
        boolean authorized = leaseRepository.findByTenantId(tenantId).stream()
                .anyMatch(lease -> {
                    Room room = roomRepository.findById(lease.getRoomId().value())
                            .orElseThrow(() -> new NotFoundException("Room", lease.getRoomId().value()));
                    House house = houseRepository.findById(room.getHouseId())
                            .orElseThrow(() -> new NotFoundException("House", room.getHouseId().value()));
                    return house.getLandlordId().equals(currentLandlordId);
                });

        if (!authorized) {
            throw new UnauthorizedException("You are not authorized to view or manage this tenant.");
        }
    }

    @Transactional
    public TenantResponse createTenant(CreateTenantRequest request) {
        // No landlord authorization needed here, as any landlord can create a tenant.
        // The link to the landlord is established via a Lease.
        if (tenantRepository.existsByEmail(request.getEmail())) {
            throw new AlreadyExistsException("Tenant", "email=" + request.getEmail());
        }
        if (tenantRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new AlreadyExistsException("Tenant", "phone=" + request.getPhoneNumber());
        }
        if (tenantRepository.existsByNationalId(request.getNationalId())) {
            throw new AlreadyExistsException("Tenant", "nationalId=" + request.getNationalId());
        }

        Tenant tenant = Tenant.create(
                request.getFirstName(),
                request.getLastName(),
                request.getEmail(),
                request.getPhoneNumber(),
                request.getNationalId()
        );

        if (request.getEmergencyContactName() != null && request.getEmergencyContactPhone() != null) {
            tenant.addEmergencyContact(request.getEmergencyContactName(), request.getEmergencyContactPhone());
        }

        Tenant savedTenant = tenantRepository.save(tenant);
        return applicationTenantMapper.toResponse(savedTenant);
    }

    @Transactional(readOnly = true)
    public TenantResponse getTenant(UUID id) {
        Tenant tenant = tenantRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Tenant", id));
        authorizeLandlordForTenant(id); // Security check
        return applicationTenantMapper.toResponse(tenant);
    }

    @Transactional(readOnly = true)
    public List<TenantResponse> getAllTenants() {
        Landlord.LandlordId currentLandlordId = getCurrentLandlordId();
        // Find all leases belonging to the current landlord
        List<Lease> landlordsLeases = leaseRepository.findByLandlordId(currentLandlordId);

        // Extract unique tenant IDs from these leases
        List<UUID> tenantIds = landlordsLeases.stream()
                .map(lease -> lease.getTenantId().value())
                .distinct()
                .collect(Collectors.toList());

        // Fetch tenants by these IDs
        return tenantRepository.findAllById(tenantIds).stream()
                .map(applicationTenantMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public TenantResponse updateTenant(UUID id, CreateTenantRequest request) {
        Tenant tenant = tenantRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Tenant", id));
        authorizeLandlordForTenant(id); // Security check

        tenant.updateDetails(
                request.getFirstName(),
                request.getLastName(),
                request.getEmail(),
                request.getPhoneNumber()
        );

        if (request.getEmergencyContactName() != null && request.getEmergencyContactPhone() != null) {
            tenant.addEmergencyContact(request.getEmergencyContactName(), request.getEmergencyContactPhone());
        }

        Tenant updatedTenant = tenantRepository.save(tenant);
        return applicationTenantMapper.toResponse(updatedTenant);
    }

    @Transactional
    public void deleteTenant(UUID id) {
        Tenant tenant = tenantRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Tenant", id));
        authorizeLandlordForTenant(id); // Security check
        tenant.deactivate(); // Soft delete
        tenantRepository.save(tenant);
    }
}