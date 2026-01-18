package com.tz.rental.landlord_management.application.service;

import com.tz.rental.landlord_management.application.dto.CreateLeaseRequest;
import com.tz.rental.landlord_management.application.dto.CreateTenantRequest;
import com.tz.rental.landlord_management.application.dto.TenantResponse;
import com.tz.rental.landlord_management.application.service.JpaLeaseService;
import com.tz.rental.landlord_management.domain.exception.AlreadyExistsException;
import com.tz.rental.landlord_management.domain.exception.NotFoundException;
import com.tz.rental.landlord_management.infrastructure.persistence.entity.LandlordEntity;
import com.tz.rental.landlord_management.infrastructure.persistence.entity.TenantEntity;
import com.tz.rental.landlord_management.infrastructure.persistence.repository.jpa.JpaLandlordRepository;
import com.tz.rental.landlord_management.infrastructure.persistence.repository.jpa.JpaTenantRepository;
import com.tz.rental.landlord_management.application.mapper.JpaTenantMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service("jpaTenantService")
@RequiredArgsConstructor
@Slf4j
public class JpaTenantService {

    private final JpaTenantRepository tenantRepository;
    private final JpaLandlordRepository landlordRepository;
    private final JpaTenantMapper tenantMapper;
    private final JpaLeaseService leaseService;

    @Transactional
    public TenantResponse createTenant(UUID landlordId, CreateTenantRequest request) {
        // Validate unique fields (email, phone, national ID)
        if (tenantRepository.existsByEmail(request.getEmail())) {
            throw new AlreadyExistsException("Tenant with this email already exists.");
        }
        if (tenantRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new AlreadyExistsException("Tenant with this phone number already exists.");
        }
        if (request.getNationalId() != null && tenantRepository.existsByNationalId(request.getNationalId())) {
            throw new AlreadyExistsException("Tenant with this National ID already exists.");
        }

        LandlordEntity landlord = landlordRepository.findById(landlordId)
                .orElseThrow(() -> new NotFoundException("Landlord not found"));

        TenantEntity tenant = new TenantEntity();
        tenant.setId(UUID.randomUUID());
        tenant.setLandlord(landlord);
        tenant.setFirstName(request.getFirstName());
        tenant.setLastName(request.getLastName());
        tenant.setEmail(request.getEmail());
        tenant.setPhoneNumber(request.getPhoneNumber());
        tenant.setNationalId(request.getNationalId());
        tenant.setEmergencyContactName(request.getEmergencyContactName());
        tenant.setEmergencyContactPhone(request.getEmergencyContactPhone());
        tenant.setActive(true);

        TenantEntity savedTenant = tenantRepository.save(tenant);
        log.info("Created tenant {} for landlord {}", savedTenant.getId(), landlordId);

        // 2. Automate Lease Creation (Business Rule: Tenant must have a room)
        CreateLeaseRequest leaseRequest = CreateLeaseRequest.builder()
                .tenantId(savedTenant.getId())
                .roomId(request.getRoomId())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .rentAmount(request.getRentAmount())
                .paymentPeriod(request.getPaymentPeriod())
                .contractDocumentUrl(request.getContractDocumentUrl())
                .build();

        leaseService.createLease(landlordId, leaseRequest);
        log.info("Automatically created lease for tenant {} in room {}", savedTenant.getId(), request.getRoomId());

        return tenantMapper.toResponse(savedTenant);
    }

    @Transactional(readOnly = true)
    public TenantResponse getTenant(UUID landlordId, UUID tenantId) {
        TenantEntity tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new NotFoundException("Tenant not found"));

        if (!tenant.getLandlord().getId().equals(landlordId)) {
            throw new NotFoundException("Tenant not found or access denied");
        }

        return tenantMapper.toResponse(tenant);
    }

    @Transactional(readOnly = true)
    public List<TenantResponse> getAllTenants(UUID landlordId) {
        return tenantRepository.findByLandlordId(landlordId).stream()
                .map(tenantMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public TenantResponse updateTenant(UUID landlordId, UUID tenantId, CreateTenantRequest request) {
        TenantEntity tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new NotFoundException("Tenant not found"));

        if (!tenant.getLandlord().getId().equals(landlordId)) {
            throw new NotFoundException("Tenant not found or access denied");
        }

        tenant.setFirstName(request.getFirstName());
        tenant.setLastName(request.getLastName());
        tenant.setEmail(request.getEmail());
        tenant.setPhoneNumber(request.getPhoneNumber());
        if (request.getNationalId() != null) {
            tenant.setNationalId(request.getNationalId());
        }
        tenant.setEmergencyContactName(request.getEmergencyContactName());
        tenant.setEmergencyContactPhone(request.getEmergencyContactPhone());

        TenantEntity updatedTenant = tenantRepository.save(tenant);
        return tenantMapper.toResponse(updatedTenant);
    }

    @Transactional
    public void deleteTenant(UUID landlordId, UUID tenantId) {
        TenantEntity tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new NotFoundException("Tenant not found"));

        if (!tenant.getLandlord().getId().equals(landlordId)) {
            throw new NotFoundException("Tenant not found or access denied");
        }

        // Soft delete
        tenant.setActive(false);
        tenantRepository.save(tenant);
        log.info("Deactivated tenant {}", tenantId);
    }
}
