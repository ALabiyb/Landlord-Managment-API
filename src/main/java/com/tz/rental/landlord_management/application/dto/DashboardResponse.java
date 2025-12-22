package com.tz.rental.landlord_management.application.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class DashboardResponse {
    private long totalProperties;
    private long totalRooms;
    private long occupiedRooms;
    private long vacantRooms;
    private long totalTenants;
    private BigDecimal expectedMonthlyIncome;
    private BigDecimal actualMonthlyIncome;
}