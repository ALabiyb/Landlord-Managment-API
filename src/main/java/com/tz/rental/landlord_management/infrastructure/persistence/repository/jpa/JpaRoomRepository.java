package com.tz.rental.landlord_management.infrastructure.persistence.repository.jpa;

import com.tz.rental.landlord_management.domain.model.valueobject.RoomStatus;
import com.tz.rental.landlord_management.infrastructure.persistence.entity.RoomEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface JpaRoomRepository extends JpaRepository<RoomEntity, UUID> {
    List<RoomEntity> findByHouseId(UUID houseId);
    List<RoomEntity> findByStatus(RoomStatus status);
}