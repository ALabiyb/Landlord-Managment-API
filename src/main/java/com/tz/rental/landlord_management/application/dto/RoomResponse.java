package com.tz.rental.landlord_management.application.dto;

import com.tz.rental.landlord_management.domain.model.valueobject.RoomStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Schema(description = "Response containing room details")
public class RoomResponse {

    @Schema(description = "ID of the room")
    private UUID id;

    @Schema(description = "ID of the house this room belongs to")
    private UUID houseId;

    @Schema(description = "Room number or identifier")
    private String roomNumber;

    @Schema(description = "Description of the room")
    private String description;

    @Schema(description = "Monthly rent for the room")
    private BigDecimal monthlyRent;

    @Schema(description = "Current status of the room")
    private RoomStatus status;

    @Schema(description = "Date and time the room was created")
    private LocalDateTime createdAt;

    @Schema(description = "Date and time the room was last updated")
    private LocalDateTime updatedAt;
}