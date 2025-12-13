package com.tz.rental.landlord_management.application.service;

import com.tz.rental.landlord_management.application.dto.CreateRoomRequest;
import com.tz.rental.landlord_management.application.dto.RoomResponse;
import com.tz.rental.landlord_management.domain.exception.NotFoundException;
import com.tz.rental.landlord_management.domain.exception.UnauthorizedException;
import com.tz.rental.landlord_management.domain.model.aggregate.House;
import com.tz.rental.landlord_management.domain.model.aggregate.Landlord;
import com.tz.rental.landlord_management.domain.model.aggregate.Room;
import com.tz.rental.landlord_management.domain.model.valueobject.RoomStatus;
import com.tz.rental.landlord_management.domain.repository.HouseRepository;
import com.tz.rental.landlord_management.domain.repository.RoomRepository;
import com.tz.rental.landlord_management.infrastructure.persistence.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;
    private final HouseRepository houseRepository; // Added HouseRepository
    @Qualifier("applicationRoomMapper")
    private final RoomMapper applicationRoomMapper;

    private Landlord.LandlordId getCurrentLandlordId() {
        UserEntity currentUser = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (currentUser.getLandlord() == null) {
            throw new IllegalStateException("The current user is not a landlord.");
        }
        return new Landlord.LandlordId(currentUser.getLandlord().getId());
    }

    private void authorizeLandlordForHouse(UUID houseId) {
        Landlord.LandlordId currentLandlordId = getCurrentLandlordId();
        House house = houseRepository.findById(new House.HouseId(houseId))
                .orElseThrow(() -> new NotFoundException("House", houseId));
        if (!house.getLandlordId().equals(currentLandlordId)) {
            throw new UnauthorizedException("You are not authorized to manage rooms in this house.");
        }
    }

    @Transactional
    public RoomResponse createRoom(CreateRoomRequest request) {
        authorizeLandlordForHouse(request.getHouseId()); // Security check

        Room room = Room.create(
                new House.HouseId(request.getHouseId()),
                request.getRoomNumber(),
                request.getMonthlyRent(),
                request.getDescription()
        );
        Room savedRoom = roomRepository.save(room);
        return applicationRoomMapper.toResponse(savedRoom);
    }

    @Transactional(readOnly = true)
    public RoomResponse getRoom(UUID id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Room", id));
        authorizeLandlordForHouse(room.getHouseId().value()); // Security check
        return applicationRoomMapper.toResponse(room);
    }

    @Transactional(readOnly = true)
    public List<RoomResponse> getRoomsByHouse(UUID houseId) {
        authorizeLandlordForHouse(houseId); // Security check
        return roomRepository.findByHouseId(houseId).stream()
                .map(applicationRoomMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public RoomResponse updateRoom(UUID id, CreateRoomRequest request) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Room", id));
        authorizeLandlordForHouse(room.getHouseId().value()); // Security check

        room.updateDetails(
                request.getRoomNumber(),
                request.getMonthlyRent(),
                request.getDescription()
        );
        Room updatedRoom = roomRepository.save(room);
        return applicationRoomMapper.toResponse(updatedRoom);
    }

    @Transactional
    public void deleteRoom(UUID id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Room", id));
        authorizeLandlordForHouse(room.getHouseId().value()); // Security check
        roomRepository.deleteById(id);
    }

    @Transactional
    public RoomResponse changeRoomStatus(UUID id, RoomStatus status) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Room", id));
        authorizeLandlordForHouse(room.getHouseId().value()); // Security check
        room.changeStatus(status);
        Room updatedRoom = roomRepository.save(room);
        return applicationRoomMapper.toResponse(updatedRoom);
    }
}