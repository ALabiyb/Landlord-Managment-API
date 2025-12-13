package com.tz.rental.landlord_management.infrastructure.persistence.mapper;

import com.tz.rental.landlord_management.domain.model.aggregate.House;
import com.tz.rental.landlord_management.domain.model.aggregate.Room;
import com.tz.rental.landlord_management.infrastructure.persistence.entity.HouseEntity;
import com.tz.rental.landlord_management.infrastructure.persistence.entity.RoomEntity;
import org.springframework.stereotype.Component;

@Component("persistenceRoomMapper")
public class RoomMapper {

    public RoomEntity toEntity(Room room) {
        RoomEntity entity = new RoomEntity();
        entity.setId(room.getId().value());
        HouseEntity houseEntity = new HouseEntity();
        houseEntity.setId(room.getHouseId().value());
        entity.setHouse(houseEntity);
        entity.setRoomNumber(room.getRoomNumber());
        entity.setDescription(room.getDescription());
        entity.setMonthlyRent(room.getMonthlyRent());
        entity.setStatus(room.getStatus());
        entity.setCreatedAt(room.getCreatedAt()); // Set createdAt from domain
        entity.setUpdatedAt(room.getUpdatedAt()); // Set updatedAt from domain
        return entity;
    }

    public Room toDomain(RoomEntity entity) {
        // Use the fromExisting factory method to reconstruct the Room aggregate
        Room room = Room.fromExisting(
                entity.getId(),
                entity.getHouse().getId(),
                entity.getRoomNumber(),
                entity.getMonthlyRent(),
                entity.getDescription(),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
        return room;
    }
}