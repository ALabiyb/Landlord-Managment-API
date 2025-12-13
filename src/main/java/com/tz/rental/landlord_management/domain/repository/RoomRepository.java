package com.tz.rental.landlord_management.domain.repository;

import com.tz.rental.landlord_management.domain.model.aggregate.Room;
import com.tz.rental.landlord_management.domain.model.valueobject.RoomStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RoomRepository {
    Room save(Room room);
    Optional<Room> findById(UUID id);
    List<Room> findAll(); // Added this method
    List<Room> findByHouseId(UUID houseId);
    void deleteById(UUID id);
    List<Room> findAllByStatus(RoomStatus status);
}