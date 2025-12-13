package com.tz.rental.landlord_management.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@Schema(description = "Represents a single entry in the monthly income report, detailing income from a lease.")
public class MonthlyIncomeReportEntry {
    @Schema(description = "ID of the lease", example = "a1b2c3d4-e5f6-7890-1234-567890abcdef")
    private UUID leaseId;

    @Schema(description = "Name of the tenant", example = "Jane Smith")
    private String tenantName;

    @Schema(description = "Room number", example = "R101")
    private String roomNumber;

    @Schema(description = "House name", example = "Kigamboni Beach House")
    private String houseName;

    @Schema(description = "Expected monthly rent from the lease", example = "250000.00")
    private BigDecimal expectedRent;

    @Schema(description = "Total amount paid for this lease in the reporting month", example = "250000.00")
    private BigDecimal amountPaid;

    @Schema(description = "Balance remaining for this lease in the reporting month", example = "0.00")
    private BigDecimal balance;
}