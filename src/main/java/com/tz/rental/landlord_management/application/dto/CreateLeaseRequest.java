package com.tz.rental.landlord_management.application.dto;

import com.tz.rental.landlord_management.domain.model.valueobject.PaymentPeriod;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Schema(description = "Request to create a new lease agreement")
public class CreateLeaseRequest {

    @NotNull(message = "Tenant ID is required")
    @Schema(description = "ID of the tenant", example = "a1b2c3d4-e5f6-7890-1234-567890abcdef")
    private UUID tenantId;

    @NotNull(message = "Room ID is required")
    @Schema(description = "ID of the room to be leased", example = "f0e9d8c7-b6a5-4321-fedc-ba9876543210")
    private UUID roomId;

    @NotNull(message = "Start date is required")
    @FutureOrPresent(message = "Start date must be in the present or future")
    @Schema(description = "Lease start date", example = "2025-01-01")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    @Future(message = "End date must be in the future")
    @Schema(description = "Lease end date", example = "2026-01-01")
    private LocalDate endDate;

    @NotNull(message = "Rent amount is required")
    @Schema(description = "Monthly rent amount", example = "500000.00")
    private BigDecimal rentAmount;

    @NotNull(message = "Payment period is required")
    @Schema(description = "Payment frequency", example = "MONTHLY")
    private PaymentPeriod paymentPeriod;
}