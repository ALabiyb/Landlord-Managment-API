package com.tz.rental.landlord_management.application.service;

import com.tz.rental.landlord_management.application.dto.CreateRoomRequest;
import com.tz.rental.landlord_management.application.dto.RoomResponse;
import com.tz.rental.landlord_management.application.dto.UpdateRoomStatusRequest;
import com.tz.rental.landlord_management.domain.model.valueobject.RoomStatus;
import com.tz.rental.landlord_management.infrastructure.persistence.entity.HouseEntity;
import com.tz.rental.landlord_management.infrastructure.persistence.entity.RoomEntity;
import com.tz.rental.landlord_management.infrastructure.persistence.repository.jpa.JpaHouseRepository;
import com.tz.rental.landlord_management.infrastructure.persistence.repository.jpa.JpaRoomRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoomServiceTest {

    @Mock
    private JpaRoomRepository roomRepository;

    @Mock
    private JpaHouseRepository houseRepository;

    @InjectMocks
    private RoomService roomService;

    private HouseEntity testHouse;
    private RoomEntity testRoom;
    private CreateRoomRequest createRoomRequest;

    @BeforeEach
    void setUp() {
        testHouse = new HouseEntity();
        testHouse.setId(UUID.randomUUID());
        testHouse.setName("Test House");

        testRoom = new RoomEntity();
        testRoom.setId(UUID.randomUUID());
        testRoom.setHouse(testHouse);
        testRoom.setRoomNumber("R101");
        testRoom.setMonthlyRent(new BigDecimal("100000"));
        testRoom.setStatus(RoomStatus.VACANT);

        createRoomRequest = new CreateRoomRequest();
        createRoomRequest.setHouseId(testHouse.getId());
        createRoomRequest.setRoomNumber("R101");
        createRoomRequest.setMonthlyRent(new BigDecimal("100000"));
    }

    @Test
    void createRoom_shouldSaveAndReturnRoomResponse() {
        // Arrange
        when(houseRepository.findById(testHouse.getId())).thenReturn(Optional.of(testHouse));
        when(roomRepository.save(any(RoomEntity.class))).thenReturn(testRoom);

        // Act
        RoomResponse response = roomService.createRoom(createRoomRequest);

        // Assert
        assertNotNull(response);
        assertEquals(testRoom.getRoomNumber(), response.getRoomNumber());
        assertEquals(RoomStatus.VACANT, response.getStatus());
        verify(roomRepository).save(any(RoomEntity.class));
    }

    @Test
    void getRoomById_shouldReturnRoomResponse() {
        // Arrange
        when(roomRepository.findById(testRoom.getId())).thenReturn(Optional.of(testRoom));

        // Act
        RoomResponse response = roomService.getRoomById(testRoom.getId());

        // Assert
        assertNotNull(response);
        assertEquals(testRoom.getId(), response.getId());
    }

    @Test
    void getAllRoomsForHouse_shouldReturnListOfRooms() {
        // Arrange
        when(houseRepository.findById(testHouse.getId())).thenReturn(Optional.of(testHouse));
        when(roomRepository.findByHouse(testHouse)).thenReturn(Collections.singletonList(testRoom));

        // Act
        List<RoomResponse> response = roomService.getAllRoomsForHouse(testHouse.getId(), null);

        // Assert
        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals(testRoom.getId(), response.get(0).getId());
    }

    @Test
    void updateRoom_shouldUpdateAndReturnRoomResponse() {
        // Arrange
        when(roomRepository.findById(testRoom.getId())).thenReturn(Optional.of(testRoom));
        when(roomRepository.save(any(RoomEntity.class))).thenReturn(testRoom);
        CreateRoomRequest updateRequest = new CreateRoomRequest();
        updateRequest.setRoomNumber("R101-UPDATED");
        updateRequest.setMonthlyRent(new BigDecimal("120000"));

        // Act
        RoomResponse response = roomService.updateRoom(testRoom.getId(), updateRequest);

        // Assert
        assertNotNull(response);
        assertEquals("R101-UPDATED", response.getRoomNumber());
        assertEquals(new BigDecimal("120000"), response.getMonthlyRent());
    }

    @Test
    void updateRoomStatus_shouldChangeStatusAndReturnRoomResponse() {
        // Arrange
        when(roomRepository.findById(testRoom.getId())).thenReturn(Optional.of(testRoom));
        when(roomRepository.save(any(RoomEntity.class))).thenReturn(testRoom);
        UpdateRoomStatusRequest statusRequest = new UpdateRoomStatusRequest();
        statusRequest.setStatus(RoomStatus.MAINTENANCE);

        // Act
        RoomResponse response = roomService.updateRoomStatus(testRoom.getId(), statusRequest);

        // Assert
        assertNotNull(response);
        assertEquals(RoomStatus.MAINTENANCE, response.getStatus());
    }

    @Test
    void deleteRoom_shouldCallDeleteRepositoryMethod() {
        // Arrange
        when(roomRepository.findById(testRoom.getId())).thenReturn(Optional.of(testRoom));

        // Act
        roomService.deleteRoom(testRoom.getId());

        // Assert
        verify(roomRepository).delete(testRoom);
    }
}