package com.tz.rental.landlord_management.infrastructure.persistence.repository.jpa;

import com.tz.rental.landlord_management.infrastructure.persistence.entity.HouseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface HouseJpaRepository extends JpaRepository<HouseEntity, UUID> {
    // This repository is deprecated. Use JpaHouseRepository instead.
}