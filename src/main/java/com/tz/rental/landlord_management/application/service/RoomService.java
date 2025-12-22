package com.tz.rental.landlord_management.application.service;

import com.tz.rental.landlord_management.application.dto.CreateRoomRequest;
import com.tz.rental.landlord_management.application.dto.RoomResponse;
import com.tz.rental.landlord_management.application.dto.UpdateRoomStatusRequest;
import com.tz.rental.landlord_management.domain.exception.NotFoundException;
import com.tz.rental.landlord_management.domain.model.valueobject.RoomStatus;
import com.tz.rental.landlord_management.infrastructure.persistence.entity.HouseEntity;
import com.tz.rental.landlord_management.infrastructure.persistence.entity.RoomEntity;
import com.tz.rental.landlord_management.infrastructure.persistence.repository.jpa.JpaHouseRepository;
import com.tz.rental.landlord_management.infrastructure.persistence.repository.jpa.JpaRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoomService {

    private static final String ROOM_NOT_FOUND_MSG = "Room not found with ID: ";
    private final JpaRoomRepository roomRepository;
    private final JpaHouseRepository houseRepository;

    @Transactional
    public RoomResponse createRoom(CreateRoomRequest request) {
        HouseEntity houseEntity = houseRepository.findById(request.getHouseId())
                .orElseThrow(() -> new NotFoundException("House not found with ID: " + request.getHouseId()));

        // TODO: Check if room number is unique within the house

        RoomEntity roomEntity = new RoomEntity();
        roomEntity.setId(UUID.randomUUID());
        roomEntity.setHouse(houseEntity);
        roomEntity.setStatus(RoomStatus.VACANT);
        // Map request to entity
        mapRequestToEntity(request, roomEntity);

        RoomEntity savedRoom = roomRepository.save(roomEntity);
        return mapEntityToResponse(savedRoom);
    }

    @Transactional(readOnly = true)
    public RoomResponse getRoomById(UUID id) {
        RoomEntity roomEntity = roomRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ROOM_NOT_FOUND_MSG + id));
        return mapEntityToResponse(roomEntity);
    }

    @Transactional(readOnly = true)
    public List<RoomResponse> getAllRoomsForHouse(UUID houseId, RoomStatus status) {
        HouseEntity houseEntity = houseRepository.findById(houseId)
                .orElseThrow(() -> new NotFoundException("House not found with ID: " + houseId));

        List<RoomEntity> rooms;
        if (status != null) {
            rooms = roomRepository.findByHouseAndStatus(houseEntity, status);
        } else {
            rooms = roomRepository.findByHouse(houseEntity);
        }

        return rooms.stream()
                .map(this::mapEntityToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public RoomResponse updateRoom(UUID id, CreateRoomRequest request) {
        RoomEntity roomEntity = roomRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ROOM_NOT_FOUND_MSG + id));

        mapRequestToEntity(request, roomEntity);
        RoomEntity updatedRoom = roomRepository.save(roomEntity);
        return mapEntityToResponse(updatedRoom);
    }

    @Transactional
    public RoomResponse updateRoomStatus(UUID id, UpdateRoomStatusRequest request) {
        RoomEntity roomEntity = roomRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ROOM_NOT_FOUND_MSG + id));

        roomEntity.setStatus(request.getStatus());
        RoomEntity updatedRoom = roomRepository.save(roomEntity);
        return mapEntityToResponse(updatedRoom);
    }

    @Transactional
    public void deleteRoom(UUID id) {
        RoomEntity roomEntity = roomRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ROOM_NOT_FOUND_MSG + id));
        roomRepository.delete(roomEntity);
    }

    private void mapRequestToEntity(CreateRoomRequest request, RoomEntity entity) {
        entity.setRoomNumber(request.getRoomNumber());
        entity.setDescription(request.getDescription());
        entity.setMonthlyRent(request.getMonthlyRent());
        entity.setSize(request.getSize());
        entity.setImageUrls(request.getImageUrls());
    }

    private RoomResponse mapEntityToResponse(RoomEntity entity) {
        return RoomResponse.builder()
                .id(entity.getId())
                .houseId(entity.getHouse().getId())
                .roomNumber(entity.getRoomNumber())
                .description(entity.getDescription())
                .monthlyRent(entity.getMonthlyRent())
                .size(entity.getSize())
                .status(entity.getStatus())
                .imageUrls(entity.getImageUrls())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .house(RoomResponse.HouseSummary.builder()
                        .id(entity.getHouse().getId())
                        .name(entity.getHouse().getName())
                        .propertyCode(entity.getHouse().getPropertyCode())
                        .district(entity.getHouse().getDistrict())
                        .build())
                .build();
    }
}