package com.tz.rental.landlord_management.application.mapper;

import com.tz.rental.landlord_management.application.dto.LeaseResponse;
import com.tz.rental.landlord_management.infrastructure.persistence.entity.LeaseEntity;
import org.springframework.stereotype.Component;

/**
 * Mapper for converting between Lease entities and DTOs.
 */
@Component
public class LeaseMapper {

    /**
     * Convert LeaseEntity to LeaseResponse DTO.
     *
     * @param entity The lease entity
     * @return LeaseResponse DTO with complete lease details
     */
    public LeaseResponse toResponse(LeaseEntity entity) {
        if (entity == null) {
            return null;
        }

        return LeaseResponse.builder()
                .id(entity.getId())
                .tenantId(entity.getTenant().getId())
                .tenantName(entity.getTenant().getFirstName() + " " + entity.getTenant().getLastName())
                .roomId(entity.getRoom().getId())
                .roomNumber(entity.getRoom().getRoomNumber())
                .houseName(entity.getRoom().getHouse().getName())
                .startDate(entity.getStartDate())
                .endDate(entity.getEndDate())
                .rentAmount(entity.getRentAmount())
                .paymentPeriod(entity.getPaymentPeriod())
                .status(entity.getStatus())
                .contractDocumentUrl(entity.getContractDocumentUrl())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
