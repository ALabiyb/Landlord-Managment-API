package com.tz.rental.landlord_management.infrastructure.persistence.mapper;

import com.tz.rental.landlord_management.domain.model.aggregate.Tenant;
import com.tz.rental.landlord_management.infrastructure.persistence.entity.TenantEntity;
import org.springframework.stereotype.Component;

@Component("persistenceTenantMapper")
public class TenantMapper {

    public TenantEntity toEntity(Tenant tenant) {
        TenantEntity entity = new TenantEntity();
        entity.setId(tenant.getId().value());
        entity.setFirstName(tenant.getFirstName());
        entity.setLastName(tenant.getLastName());
        entity.setEmail(tenant.getEmail().getValue());
        entity.setPhoneNumber(tenant.getPhoneNumber().getValue());
        entity.setNationalId(tenant.getNationalId());
        if (tenant.getEmergencyContactName() != null) {
            entity.setEmergencyContactName(tenant.getEmergencyContactName());
            entity.setEmergencyContactPhone(tenant.getEmergencyContactPhone().getValue());
        }
        entity.setActive(tenant.isActive());
        entity.setCreatedAt(tenant.getCreatedAt()); // Set createdAt from domain
        entity.setUpdatedAt(tenant.getUpdatedAt()); // Set updatedAt from domain
        return entity;
    }

    public Tenant toDomain(TenantEntity entity) {
        // Use the fromExisting factory method to reconstruct the Tenant aggregate
        Tenant tenant = Tenant.fromExisting(
                entity.getId(),
                entity.getFirstName(),
                entity.getLastName(),
                entity.getEmail(),
                entity.getPhoneNumber(),
                entity.getNationalId(),
                entity.getEmergencyContactName(),
                entity.getEmergencyContactPhone(),
                entity.isActive(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
        return tenant;
    }
}