package com.tz.rental.landlord_management.application.service;

import com.tz.rental.landlord_management.application.dto.RoomResponse;
import com.tz.rental.landlord_management.domain.model.aggregate.Room;
import org.springframework.stereotype.Component;

@Component("applicationRoomMapper")
public class RoomMapper {

    public RoomResponse toResponse(Room room) {
        RoomResponse response = new RoomResponse();
        response.setId(room.getId().value());
        response.setHouseId(room.getHouseId().value());
        response.setRoomNumber(room.getRoomNumber());
        response.setDescription(room.getDescription());
        response.setMonthlyRent(room.getMonthlyRent());
        response.setStatus(room.getStatus());
        response.setCreatedAt(room.getCreatedAt());
        response.setUpdatedAt(room.getUpdatedAt());
        return response;
    }
}