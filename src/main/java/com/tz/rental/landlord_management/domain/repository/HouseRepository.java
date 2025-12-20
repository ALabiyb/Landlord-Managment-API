package com.tz.rental.landlord_management.domain.repository;

import com.tz.rental.landlord_management.domain.model.aggregate.House;
import com.tz.rental.landlord_management.domain.model.aggregate.Landlord;

import java.util.List;
import java.util.Optional;

public interface HouseRepository {
    House save(House house);
    Optional<House> findById(House.HouseId id);
    Optional<House> findByPropertyCode(String propertyCode);
    boolean existsByPropertyCode(String propertyCode);
    List<House> findAll();
    List<House> findByLandlordId(Landlord.LandlordId landlordId);
    long countByLandlordId(Landlord.LandlordId landlordId); // New method
    List<House> findByStatus(House.HouseStatus status);
    List<House> findByLandlordIdAndStatus(Landlord.LandlordId landlordId, House.HouseStatus status);
    long count();
    void delete(House.HouseId id);
}