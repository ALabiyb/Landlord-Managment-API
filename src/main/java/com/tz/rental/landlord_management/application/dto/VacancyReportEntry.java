package com.tz.rental.landlord_management.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
@Schema(description = "Represents a single vacant room in the vacancy report.")
public class VacancyReportEntry {
    @Schema(description = "ID of the vacant room", example = "f0e9d8c7-b6a5-4321-fedc-ba9876543210")
    private UUID roomId;

    @Schema(description = "Room number", example = "R203")
    private String roomNumber;

    @Schema(description = "House name where the room is located", example = "Kigamboni Beach House")
    private String houseName;

    @Schema(description = "House ID where the room is located", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID houseId;

    @Schema(description = "Description of the room", example = "Spacious room on the second floor.")
    private String roomDescription;
}