package com.tz.rental.landlord_management.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Schema(description = "Request to record a new payment")
public class CreatePaymentRequest {

    @NotNull(message = "Lease ID is required")
    @Schema(description = "ID of the lease this payment is for", example = "a1b2c3d4-e5f6-7890-1234-567890abcdef")
    private UUID leaseId;

    @NotNull(message = "Amount paid is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Amount paid must be greater than zero")
    @Schema(description = "The amount of money paid", example = "500000.00")
    private BigDecimal amountPaid;

    @NotNull(message = "Payment date is required")
    @Schema(description = "The date the payment was made", example = "2025-01-10")
    private LocalDate paymentDate;

    @Schema(description = "Optional transaction reference or receipt number", example = "TRX123456789")
    private String transactionReference;
}