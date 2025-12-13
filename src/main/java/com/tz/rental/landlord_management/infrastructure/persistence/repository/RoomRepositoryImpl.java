package com.tz.rental.landlord_management.infrastructure.persistence.repository;

import com.tz.rental.landlord_management.domain.model.aggregate.Room;
import com.tz.rental.landlord_management.domain.model.valueobject.RoomStatus;
import com.tz.rental.landlord_management.domain.repository.RoomRepository;
import com.tz.rental.landlord_management.infrastructure.persistence.entity.RoomEntity;
import com.tz.rental.landlord_management.infrastructure.persistence.mapper.RoomMapper;
import com.tz.rental.landlord_management.infrastructure.persistence.repository.jpa.JpaRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RoomRepositoryImpl implements RoomRepository {

    private final JpaRoomRepository jpaRoomRepository;
    private final RoomMapper roomMapper;

    @Override
    public Room save(Room room) {
        RoomEntity entity = roomMapper.toEntity(room);
        RoomEntity savedEntity = jpaRoomRepository.save(entity);
        return roomMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Room> findById(UUID id) {
        return jpaRoomRepository.findById(id).map(roomMapper::toDomain);
    }

    @Override
    public List<Room> findAll() {
        return jpaRoomRepository.findAll().stream()
                .map(roomMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Room> findByHouseId(UUID houseId) {
        return jpaRoomRepository.findByHouseId(houseId).stream()
                .map(roomMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(UUID id) {
        jpaRoomRepository.deleteById(id);
    }

    @Override
    public List<Room> findAllByStatus(RoomStatus status) {
        return jpaRoomRepository.findByStatus(status).stream()
                .map(roomMapper::toDomain)
                .collect(Collectors.toList());
    }
}