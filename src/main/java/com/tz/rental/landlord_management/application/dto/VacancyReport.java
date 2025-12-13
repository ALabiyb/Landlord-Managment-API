package com.tz.rental.landlord_management.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@Schema(description = "Report detailing all currently vacant rooms.")
public class VacancyReport {
    @Schema(description = "The date the report was generated", example = "2025-01-10")
    private LocalDate reportDate;

    @Schema(description = "Total number of vacant rooms", example = "5")
    private int totalVacantRooms;

    @Schema(description = "List of individual vacant room entries")
    private List<VacancyReportEntry> entries;
}