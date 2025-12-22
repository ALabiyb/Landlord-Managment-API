package com.tz.rental.landlord_management.application.service;

import com.tz.rental.landlord_management.application.dto.DashboardResponse;
import com.tz.rental.landlord_management.domain.model.valueobject.RoomStatus;
import com.tz.rental.landlord_management.infrastructure.persistence.entity.LandlordEntity;
import com.tz.rental.landlord_management.infrastructure.persistence.repository.jpa.JpaHouseRepository;
import com.tz.rental.landlord_management.infrastructure.persistence.repository.jpa.JpaLandlordRepository;
import com.tz.rental.landlord_management.infrastructure.persistence.repository.jpa.JpaRoomRepository;
import com.tz.rental.landlord_management.infrastructure.persistence.repository.jpa.JpaTenantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final JpaHouseRepository houseRepository;
    private final JpaRoomRepository roomRepository;
    private final JpaTenantRepository tenantRepository;
    private final JpaLandlordRepository landlordRepository;

    @Transactional(readOnly = true)
    public DashboardResponse getDashboardStats() {
        LandlordEntity landlord = getCurrentLandlord();

        long totalProperties = houseRepository.countByLandlord(landlord);
        long totalRooms = roomRepository.countByHouseLandlord(landlord);
        long occupiedRooms = roomRepository.countByHouseLandlordAndStatus(landlord, RoomStatus.OCCUPIED);
        long vacantRooms = totalRooms - occupiedRooms;
        long totalTenants = tenantRepository.countByLandlord(landlord);
        BigDecimal expectedMonthlyIncome = roomRepository.sumMonthlyRentByLandlordAndStatus(landlord, RoomStatus.OCCUPIED);
        // Actual monthly income would be calculated from payments, which is not yet implemented
        BigDecimal actualMonthlyIncome = BigDecimal.ZERO;

        return DashboardResponse.builder()
                .totalProperties(totalProperties)
                .totalRooms(totalRooms)
                .occupiedRooms(occupiedRooms)
                .vacantRooms(vacantRooms)
                .totalTenants(totalTenants)
                .expectedMonthlyIncome(expectedMonthlyIncome)
                .actualMonthlyIncome(actualMonthlyIncome)
                .build();
    }

    private LandlordEntity getCurrentLandlord() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return landlordRepository.findByUserUsername(username)
                .orElseThrow(() -> new IllegalStateException("Landlord not found for current user."));
    }
}