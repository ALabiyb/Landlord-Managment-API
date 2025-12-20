package com.tz.rental.landlord_management.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
@Schema(description = "Statistics for the landlord's dashboard")
public class DashboardStatsDTO {
    @Schema(description = "Total number of properties (houses) owned by the landlord")
    private long totalProperties;

    @Schema(description = "Number of properties that have at least one occupied room")
    private long occupiedProperties;

    @Schema(description = "Number of properties that are completely vacant")
    private long vacantProperties;

    @Schema(description = "Total number of unique tenants with active leases")
    private long totalTenants;

    @Schema(description = "Total expected rental income for the current month from all active leases")
    private BigDecimal expectedMonthlyIncome;

    @Schema(description = "Total payments received in the current month")
    private BigDecimal receivedMonthlyIncome;
}