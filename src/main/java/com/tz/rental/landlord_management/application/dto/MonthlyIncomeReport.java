package com.tz.rental.landlord_management.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;

@Data
@Builder
@Schema(description = "Comprehensive monthly income report for the landlord.")
public class MonthlyIncomeReport {
    @Schema(description = "The month and year this report covers", example = "2025-01")
    private YearMonth reportMonth;

    @Schema(description = "Total expected income from all active leases for the month", example = "1000000.00")
    private BigDecimal totalExpectedIncome;

    @Schema(description = "Total actual income received from all payments for the month", example = "950000.00")
    private BigDecimal totalActualIncome;

    @Schema(description = "Total outstanding balance across all leases for the month", example = "50000.00")
    private BigDecimal totalOutstandingBalance;

    @Schema(description = "List of individual lease income entries for the month")
    private List<MonthlyIncomeReportEntry> entries;
}