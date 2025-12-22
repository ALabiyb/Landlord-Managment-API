package com.tz.rental.landlord_management.infrastructure.persistence.mapper;

import com.tz.rental.landlord_management.domain.model.aggregate.Room;
import com.tz.rental.landlord_management.infrastructure.persistence.entity.RoomEntity;
import org.springframework.stereotype.Component;

@Component
public class RoomMapper {

    public Room toDomain(RoomEntity entity) {
        return new Room.RoomBuilder(entity.getId(), entity.getHouse().getId(), entity.getCreatedAt())
                .roomNumber(entity.getRoomNumber())
                .monthlyRent(entity.getMonthlyRent())
                .description(entity.getDescription())
                .status(entity.getStatus())
                .size(entity.getSize())
                .imageUrls(entity.getImageUrls())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public RoomEntity toEntity(Room domain) {
        RoomEntity entity = new RoomEntity();
        entity.setId(domain.getId().value());
        // Assuming houseId is set elsewhere or handled via relationship
        entity.setRoomNumber(domain.getRoomNumber());
        entity.setDescription(domain.getDescription());
        entity.setMonthlyRent(domain.getMonthlyRent());
        entity.setStatus(domain.getStatus());
        entity.setSize(domain.getSize());
        entity.setImageUrls(domain.getImageUrls());
        return entity;
    }
}