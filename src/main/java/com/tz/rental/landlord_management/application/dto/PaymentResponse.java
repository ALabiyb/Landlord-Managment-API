package com.tz.rental.landlord_management.application.dto;

import com.tz.rental.landlord_management.domain.model.valueobject.PaymentStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response containing payment details")
public class PaymentResponse {

    @Schema(description = "ID of the payment")
    private UUID id;

    @Schema(description = "ID of the lease this payment is for")
    private UUID leaseId;

    @Schema(description = "The amount of money paid")
    private BigDecimal amountPaid;

    @Schema(description = "The date the payment was made")
    private LocalDate paymentDate;

    @Schema(description = "The date the payment was/is due")
    private LocalDate dueDate;

    @Schema(description = "Tenant name")
    private String tenantName;

    @Schema(description = "Room number")
    private String roomNumber;

    @Schema(description = "Current status of the payment")
    private PaymentStatus status;

    @Schema(description = "Transaction reference or receipt number")
    private String transactionReference;

    @Schema(description = "Optional remarks or notes about the payment")
    private String remarks;

    @Schema(description = "Date and time the payment was recorded")
    private LocalDateTime createdAt;

    @Schema(description = "Date and time the payment record was last updated")
    private LocalDateTime updatedAt;
}