package com.tz.rental.landlord_management.application.mapper;

import com.tz.rental.landlord_management.application.dto.TenantResponse;
import com.tz.rental.landlord_management.infrastructure.persistence.entity.TenantEntity;
import org.springframework.stereotype.Component;

@Component
public class JpaTenantMapper {

    public TenantResponse toResponse(TenantEntity entity) {
        if (entity == null) {
            return null;
        }

        return TenantResponse.builder()
                .id(entity.getId())
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .email(entity.getEmail())
                .phoneNumber(entity.getPhoneNumber())
                .nationalId(entity.getNationalId())
                .emergencyContactName(entity.getEmergencyContactName())
                .emergencyContactPhone(entity.getEmergencyContactPhone())
                .isActive(entity.isActive())
                .build();
    }
}
