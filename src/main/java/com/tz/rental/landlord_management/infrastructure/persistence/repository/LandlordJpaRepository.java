package com.tz.rental.landlord_management.infrastructure.persistence.repository;

import com.tz.rental.landlord_management.infrastructure.persistence.entity.LandlordEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface LandlordJpaRepository extends JpaRepository<LandlordEntity, UUID> {
    Optional<LandlordEntity> findByEmail(String email);
    Optional<LandlordEntity> findByPhoneNumber(String phoneNumber);
    boolean existsByEmail(String email);
    boolean existsByPhoneNumber(String phoneNumber);
}