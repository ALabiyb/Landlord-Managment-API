package com.tz.rental.landlord_management.infrastructure.persistence.repository.jpa;

import com.tz.rental.landlord_management.infrastructure.persistence.entity.HouseEntity;
import com.tz.rental.landlord_management.infrastructure.persistence.entity.LandlordEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaHouseRepository extends JpaRepository<HouseEntity, UUID>, JpaSpecificationExecutor<HouseEntity> {
    Optional<HouseEntity> findByPropertyCode(String propertyCode);
    Page<HouseEntity> findByLandlord(LandlordEntity landlord, Pageable pageable);
    long countByLandlord(LandlordEntity landlord);
    List<HouseEntity> findByLandlordId(UUID landlordId);
    long countByLandlordId(UUID landlordId);
    boolean existsByPropertyCode(String propertyCode);
}