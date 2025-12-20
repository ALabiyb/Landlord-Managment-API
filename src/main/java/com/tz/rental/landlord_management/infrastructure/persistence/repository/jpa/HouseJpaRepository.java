package com.tz.rental.landlord_management.infrastructure.persistence.repository.jpa;

import com.tz.rental.landlord_management.infrastructure.persistence.entity.HouseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface HouseJpaRepository extends JpaRepository<HouseEntity, UUID> {
    Optional<HouseEntity> findByPropertyCode(String propertyCode);
    boolean existsByPropertyCode(String propertyCode);
    List<HouseEntity> findByLandlordId(UUID landlordId);
    long countByLandlordId(UUID landlordId); // New method
    List<HouseEntity> findByStatus(String status);
    List<HouseEntity> findByLandlordIdAndStatus(UUID landlordId, String status);
}