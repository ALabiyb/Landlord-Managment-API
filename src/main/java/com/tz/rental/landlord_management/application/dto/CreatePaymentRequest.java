package com.tz.rental.landlord_management.application.dto;

import com.tz.rental.landlord_management.domain.model.valueobject.PaymentStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to record a new payment")
public class CreatePaymentRequest {

    @NotNull(message = "Lease ID is required")
    @Schema(description = "ID of the lease this payment is for", example = "a1b2c3d4-e5f6-7890-1234-567890abcdef")
    private UUID leaseId;

    @NotNull(message = "Amount paid is required")
    @Positive(message = "Amount paid must be positive")
    @DecimalMin(value = "0.01", message = "Amount paid must be greater than zero")
    @Schema(description = "The amount of money paid", example = "500000.00")
    private BigDecimal amountPaid;

    @NotNull(message = "Payment date is required")
    @PastOrPresent(message = "Payment date cannot be in the future")
    @Schema(description = "The date the payment was made", example = "2025-01-10")
    private LocalDate paymentDate;

    @NotNull(message = "Due date is required")
    @Schema(description = "Date when payment was/is due", example = "2025-01-01")
    private LocalDate dueDate;

    @NotNull(message = "Payment status is required")
    @Schema(description = "Status of the payment", example = "PAID")
    private PaymentStatus status;

    @Schema(description = "Transaction reference or receipt number")
    private String transactionReference;

    @Schema(description = "Optional remarks or notes about the payment", example = "Payment for 6 months (Jan-June)")
    private String remarks;
}