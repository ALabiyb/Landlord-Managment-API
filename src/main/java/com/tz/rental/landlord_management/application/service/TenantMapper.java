package com.tz.rental.landlord_management.application.service;

import com.tz.rental.landlord_management.application.dto.TenantResponse;
import com.tz.rental.landlord_management.domain.model.aggregate.Tenant;
import org.springframework.stereotype.Component;

@Component("applicationTenantMapper")
public class TenantMapper {

    public TenantResponse toResponse(Tenant tenant) {
        TenantResponse response = new TenantResponse();
        response.setId(tenant.getId().value());
        response.setFirstName(tenant.getFirstName());
        response.setLastName(tenant.getLastName());
        response.setEmail(tenant.getEmail().getValue());
        response.setPhoneNumber(tenant.getPhoneNumber().getValue());
        response.setNationalId(tenant.getNationalId());
        if (tenant.getEmergencyContactName() != null) {
            response.setEmergencyContactName(tenant.getEmergencyContactName());
            response.setEmergencyContactPhone(tenant.getEmergencyContactPhone().getValue());
        }
        response.setActive(tenant.isActive());
        response.setCreatedAt(tenant.getCreatedAt());
        response.setUpdatedAt(tenant.getUpdatedAt());
        return response;
    }
}