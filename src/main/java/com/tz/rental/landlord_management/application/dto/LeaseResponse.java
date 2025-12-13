package com.tz.rental.landlord_management.application.dto;

import com.tz.rental.landlord_management.domain.model.valueobject.LeaseStatus;
import com.tz.rental.landlord_management.domain.model.valueobject.PaymentPeriod;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Schema(description = "Response containing lease details")
public class LeaseResponse {

    @Schema(description = "ID of the lease")
    private UUID id;

    @Schema(description = "ID of the tenant")
    private UUID tenantId;

    @Schema(description = "ID of the room")
    private UUID roomId;

    @Schema(description = "Lease start date")
    private LocalDate startDate;

    @Schema(description = "Lease end date")
    private LocalDate endDate;

    @Schema(description = "Monthly rent amount")
    private BigDecimal rentAmount;

    @Schema(description = "Payment frequency")
    private PaymentPeriod paymentPeriod;

    @Schema(description = "Current status of the lease")
    private LeaseStatus status;

    @Schema(description = "URL of the generated contract document")
    private String contractDocumentUrl;

    @Schema(description = "Date and time the lease was created")
    private LocalDateTime createdAt;
}