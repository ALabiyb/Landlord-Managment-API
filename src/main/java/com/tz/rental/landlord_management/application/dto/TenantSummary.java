package com.tz.rental.landlord_management.application.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
public class TenantSummary {
    private UUID id;
    private String fullName;
    private LocalDate leaseEndDate;
}