package com.tz.rental.landlord_management.application.mapper;

import com.tz.rental.landlord_management.application.dto.PaymentResponse;
import com.tz.rental.landlord_management.infrastructure.persistence.entity.PaymentEntity;
import org.springframework.stereotype.Component;

/**
 * Mapper for converting between Payment entities and DTOs.
 */
@Component
public class PaymentMapper {

    /**
     * Convert PaymentEntity to PaymentResponse DTO.
     *
     * @param entity The payment entity
     * @return PaymentResponse DTO with complete payment details
     */
    public PaymentResponse toResponse(PaymentEntity entity) {
        if (entity == null) {
            return null;
        }

        String tenantName = "";
        String roomNumber = "";

        if (entity.getLease() != null) {
            if (entity.getLease().getTenant() != null) {
                tenantName = entity.getLease().getTenant().getFirstName() + " "
                        + entity.getLease().getTenant().getLastName();
            }
            if (entity.getLease().getRoom() != null) {
                roomNumber = entity.getLease().getRoom().getRoomNumber();
            }
        }

        return PaymentResponse.builder()
                .id(entity.getId())
                .leaseId(entity.getLease() != null ? entity.getLease().getId() : null)
                .amountPaid(entity.getAmountPaid())
                .paymentDate(entity.getPaymentDate())
                .dueDate(entity.getDueDate())
                .tenantName(tenantName)
                .roomNumber(roomNumber)
                .status(entity.getStatus())
                .transactionReference(entity.getTransactionReference())
                .remarks(entity.getRemarks())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
