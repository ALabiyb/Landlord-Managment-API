package com.tz.rental.landlord_management.application.service;

import com.tz.rental.landlord_management.application.dto.CreateHouseRequest;
import com.tz.rental.landlord_management.application.dto.HouseResponse;
import com.tz.rental.landlord_management.application.dto.PaginatedResponse;
import com.tz.rental.landlord_management.domain.model.valueobject.HouseType;
import com.tz.rental.landlord_management.infrastructure.persistence.entity.HouseEntity;
import com.tz.rental.landlord_management.infrastructure.persistence.entity.LandlordEntity;
import com.tz.rental.landlord_management.infrastructure.persistence.repository.jpa.JpaHouseRepository;
import com.tz.rental.landlord_management.infrastructure.persistence.repository.jpa.JpaLandlordRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HouseServiceTest {

    @Mock
    private JpaHouseRepository houseRepository;

    @Mock
    private JpaLandlordRepository landlordRepository;

    @InjectMocks
    private HouseService houseService;

    private LandlordEntity testLandlord;
    private HouseEntity testHouse;
    private CreateHouseRequest createHouseRequest;

    @BeforeEach
    void setUp() {
        testLandlord = new LandlordEntity();
        testLandlord.setId(UUID.randomUUID());

        // Mock the security context to simulate a logged-in user
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("testuser", "password"));
        when(landlordRepository.findByUserUsername("testuser")).thenReturn(Optional.of(testLandlord));

        testHouse = new HouseEntity();
        testHouse.setId(UUID.randomUUID());
        testHouse.setLandlord(testLandlord);
        testHouse.setPropertyCode("PROP001");
        testHouse.setName("Test House");
        testHouse.setHouseType(HouseType.STANDALONE);

        createHouseRequest = new CreateHouseRequest();
        createHouseRequest.setPropertyCode("PROP001");
        createHouseRequest.setName("Test House");
        createHouseRequest.setHouseType(HouseType.STANDALONE);
    }

    @Test
    void createHouse_shouldSaveAndReturnHouseResponse() {
        // Arrange
        when(houseRepository.findByPropertyCode(any(String.class))).thenReturn(Optional.empty());
        when(houseRepository.save(any(HouseEntity.class))).thenReturn(testHouse);

        // Act
        HouseResponse response = houseService.createHouse(createHouseRequest);

        // Assert
        assertNotNull(response);
        assertEquals(testHouse.getPropertyCode(), response.getPropertyCode());
        verify(houseRepository).save(any(HouseEntity.class));
    }

    @Test
    void getHouseById_shouldReturnHouseResponse() {
        // Arrange
        when(houseRepository.findById(testHouse.getId())).thenReturn(Optional.of(testHouse));

        // Act
        HouseResponse response = houseService.getHouseById(testHouse.getId(), false);

        // Assert
        assertNotNull(response);
        assertEquals(testHouse.getId(), response.getId());
    }

    @Test
    void getAllHouses_shouldReturnPaginatedResponse() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<HouseEntity> housePage = new PageImpl<>(Collections.singletonList(testHouse), pageable, 1);
        when(houseRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(housePage);

        // Act
        PaginatedResponse<List<HouseResponse>> response = houseService.getAllHouses(pageable, null);

        // Assert
        assertNotNull(response);
        assertEquals(1, response.getData().size());
        assertEquals(0, response.getPagination().getCurrentPage());
    }

    @Test
    void updateHouse_shouldUpdateAndReturnHouseResponse() {
        // Arrange
        when(houseRepository.findById(testHouse.getId())).thenReturn(Optional.of(testHouse));
        when(houseRepository.save(any(HouseEntity.class))).thenReturn(testHouse);
        CreateHouseRequest updateRequest = new CreateHouseRequest();
        updateRequest.setPropertyCode("PROP001");
        updateRequest.setName("Updated Test House");
        updateRequest.setHouseType(HouseType.APARTMENT);

        // Act
        HouseResponse response = houseService.updateHouse(testHouse.getId(), updateRequest);

        // Assert
        assertNotNull(response);
        assertEquals("Updated Test House", response.getName());
        assertEquals(HouseType.APARTMENT, response.getHouseType());
    }

    @Test
    void deleteHouse_shouldCallDeleteRepositoryMethod() {
        // Arrange
        when(houseRepository.findById(testHouse.getId())).thenReturn(Optional.of(testHouse));

        // Act
        houseService.deleteHouse(testHouse.getId());

        // Assert
        verify(houseRepository).delete(testHouse);
    }
}