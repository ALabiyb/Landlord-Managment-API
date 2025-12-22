package com.tz.rental.landlord_management.application.service;

import com.tz.rental.landlord_management.application.dto.DashboardResponse;
import com.tz.rental.landlord_management.domain.model.valueobject.RoomStatus;
import com.tz.rental.landlord_management.infrastructure.persistence.entity.LandlordEntity;
import com.tz.rental.landlord_management.infrastructure.persistence.repository.jpa.JpaHouseRepository;
import com.tz.rental.landlord_management.infrastructure.persistence.repository.jpa.JpaLandlordRepository;
import com.tz.rental.landlord_management.infrastructure.persistence.repository.jpa.JpaRoomRepository;
import com.tz.rental.landlord_management.infrastructure.persistence.repository.jpa.JpaTenantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    @Mock
    private JpaHouseRepository houseRepository;

    @Mock
    private JpaRoomRepository roomRepository;

    @Mock
    private JpaTenantRepository tenantRepository;

    @Mock
    private JpaLandlordRepository landlordRepository;

    @InjectMocks
    private DashboardService dashboardService;

    private LandlordEntity testLandlord;

    @BeforeEach
    void setUp() {
        // Mock the security context to simulate a logged-in user
        testLandlord = new LandlordEntity();
        testLandlord.setId(UUID.randomUUID());
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("testuser", "password"));

        when(landlordRepository.findByUserUsername("testuser")).thenReturn(Optional.of(testLandlord));
    }

    @Test
    void getDashboardStats_shouldReturnCorrectlyCalculatedStats() {
        // Arrange: Define what the mocked repositories should return when called
        when(houseRepository.countByLandlord(testLandlord)).thenReturn(5L);
        when(roomRepository.countByHouseLandlord(testLandlord)).thenReturn(20L);
        when(roomRepository.countByHouseLandlordAndStatus(testLandlord, RoomStatus.OCCUPIED)).thenReturn(15L);
        when(tenantRepository.countByLandlord(testLandlord)).thenReturn(12L);
        when(roomRepository.sumMonthlyRentByLandlordAndStatus(testLandlord, RoomStatus.OCCUPIED))
                .thenReturn(new BigDecimal("2500000.00"));

        // Act: Call the method we are testing
        DashboardResponse response = dashboardService.getDashboardStats();

        // Assert: Verify that the service logic correctly calculated the final values
        assertEquals(5L, response.getTotalProperties());
        assertEquals(20L, response.getTotalRooms());
        assertEquals(15L, response.getOccupiedRooms());
        assertEquals(5L, response.getVacantRooms()); // 20 total - 15 occupied
        assertEquals(12L, response.getTotalTenants());
        assertEquals(new BigDecimal("2500000.00"), response.getExpectedMonthlyIncome());
        assertEquals(BigDecimal.ZERO, response.getActualMonthlyIncome()); // As per current implementation
    }
}