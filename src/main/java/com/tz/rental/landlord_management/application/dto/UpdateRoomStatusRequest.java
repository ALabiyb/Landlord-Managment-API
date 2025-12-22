package com.tz.rental.landlord_management.application.dto;

import com.tz.rental.landlord_management.domain.model.valueobject.RoomStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateRoomStatusRequest {
    @NotNull
    private RoomStatus status;
}