package com.tz.rental.landlord_management.application.dto;

import com.tz.rental.landlord_management.domain.model.valueobject.PaymentStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Request DTO for updating an existing payment.
 * All fields are optional - only provided fields will be updated.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to update an existing payment")
public class UpdatePaymentRequest {

    @Positive(message = "Amount paid must be positive")
    @DecimalMin(value = "0.01", message = "Amount paid must be greater than zero")
    @Schema(description = "Updated amount paid", example = "550000.00")
    private BigDecimal amountPaid;

    @PastOrPresent(message = "Payment date cannot be in the future")
    @Schema(description = "Updated payment date", example = "2025-12-26")
    private LocalDate paymentDate;

    @Schema(description = "Updated payment status", example = "PAID")
    private PaymentStatus status;

    @Schema(description = "Updated due date", example = "2025-01-01")
    private LocalDate dueDate;

    @Schema(description = "Updated transaction reference", example = "TXN-2025-12-26-002")
    private String transactionReference;
}
