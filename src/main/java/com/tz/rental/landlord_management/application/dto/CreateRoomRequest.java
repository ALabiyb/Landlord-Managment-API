package com.tz.rental.landlord_management.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Schema(description = "Request to create a new room")
public class CreateRoomRequest {

    @NotNull(message = "House ID is required")
    @Schema(description = "ID of the house this room belongs to", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID houseId;

    @NotBlank(message = "Room number is required")
    @Schema(description = "Room number or identifier", example = "A101")
    private String roomNumber;

    @Schema(description = "Description of the room", example = "A spacious room with a balcony")
    private String description;

    @NotNull(message = "Monthly rent is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Monthly rent must be greater than zero")
    @Schema(description = "Monthly rent for the room", example = "250000.00")
    private BigDecimal monthlyRent;
}