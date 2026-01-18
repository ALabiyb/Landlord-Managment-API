package com.tz.rental.landlord_management.application.dto;

import com.tz.rental.landlord_management.domain.model.valueobject.LeaseStatus;
import com.tz.rental.landlord_management.domain.model.valueobject.PaymentPeriod;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Request DTO for updating an existing lease.
 * All fields are optional - only provided fields will be updated.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to update an existing lease agreement")
public class UpdateLeaseRequest {

    @Schema(description = "New lease end date", example = "2026-12-31")
    private LocalDate endDate;

    @Positive(message = "Rent amount must be positive")
    @DecimalMin(value = "0.01", message = "Rent amount must be greater than zero")
    @Schema(description = "Updated rent amount", example = "550000.00")
    private BigDecimal rentAmount;

    @Schema(description = "Updated payment frequency", example = "MONTHLY")
    private PaymentPeriod paymentPeriod;

    @Schema(description = "Updated lease status", example = "ACTIVE")
    private LeaseStatus status;

    @Schema(description = "URL to the updated contract document", example = "https://storage.example.com/contracts/lease-123-updated.pdf")
    private String contractDocumentUrl;
}
