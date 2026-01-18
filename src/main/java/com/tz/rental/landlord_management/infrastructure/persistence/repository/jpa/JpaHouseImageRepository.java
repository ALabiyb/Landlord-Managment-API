package com.tz.rental.landlord_management.infrastructure.persistence.repository.jpa;

import com.tz.rental.landlord_management.infrastructure.persistence.entity.HouseImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface JpaHouseImageRepository extends JpaRepository<HouseImageEntity, UUID> {
    java.util.List<HouseImageEntity> findByHouseId(UUID houseId);
}
