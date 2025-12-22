package com.tz.rental.landlord_management.infrastructure.persistence.repository.jpa;

import com.tz.rental.landlord_management.domain.model.valueobject.RoomStatus;
import com.tz.rental.landlord_management.infrastructure.persistence.entity.HouseEntity;
import com.tz.rental.landlord_management.infrastructure.persistence.entity.LandlordEntity;
import com.tz.rental.landlord_management.infrastructure.persistence.entity.RoomEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Repository
public interface JpaRoomRepository extends JpaRepository<RoomEntity, UUID> {
    List<RoomEntity> findByHouse(HouseEntity house);
    List<RoomEntity> findByHouseId(UUID houseId);
    List<RoomEntity> findByHouseAndStatus(HouseEntity house, RoomStatus status);
    List<RoomEntity> findByStatus(RoomStatus status);
    long countByHouseLandlord(LandlordEntity landlord);
    long countByHouseLandlordAndStatus(LandlordEntity landlord, RoomStatus status);

    @Query("SELECT SUM(r.monthlyRent) FROM RoomEntity r WHERE r.house.landlord = :landlord AND r.status = :status")
    BigDecimal sumMonthlyRentByLandlordAndStatus(LandlordEntity landlord, RoomStatus status);
}