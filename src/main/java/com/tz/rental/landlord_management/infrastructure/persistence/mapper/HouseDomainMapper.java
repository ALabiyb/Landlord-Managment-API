package com.tz.rental.landlord_management.infrastructure.persistence.mapper;

import com.tz.rental.landlord_management.domain.model.aggregate.House;
import com.tz.rental.landlord_management.domain.model.valueobject.Address;
import com.tz.rental.landlord_management.infrastructure.persistence.entity.HouseEntity;
import com.tz.rental.landlord_management.infrastructure.persistence.entity.LandlordEntity;
import org.springframework.stereotype.Component;

@Component
public class HouseDomainMapper {

    public HouseEntity toEntity(House house) {
        HouseEntity entity = new HouseEntity();
        entity.setId(house.getId().value());
        entity.setPropertyCode(house.getPropertyCode());
        entity.setName(house.getName());
        entity.setDescription(house.getDescription());
        // Map domain HouseType to entity HouseType
        entity.setHouseType(com.tz.rental.landlord_management.domain.model.valueobject.HouseType.valueOf(house.getHouseType().name()));

        LandlordEntity landlord = new LandlordEntity();
        landlord.setId(house.getLandlordId().value());
        entity.setLandlord(landlord);

        entity.setStreetAddress(house.getAddress().getStreetAddress());
        entity.setDistrict(house.getAddress().getDistrict());
        entity.setRegion(house.getAddress().getRegion());
        entity.setCountry(house.getAddress().getCountry());
        entity.setTotalFloors(house.getTotalFloors());
        entity.setYearBuilt(house.getYearBuilt());
        entity.setHasParking(house.getHasParking());
        entity.setHasSecurity(house.getHasSecurity());
        entity.setHasWater(house.getHasWater());
        entity.setHasElectricity(house.getHasElectricity());
        entity.setImageUrls(house.getImageUrls());
        entity.setMonthlyCommonCharges(house.getMonthlyCommonCharges());
        // Status is no longer stored in HouseEntity, it's derived

        return entity;
    }

    public House toDomain(HouseEntity entity) {
        Address address = new Address(
                entity.getStreetAddress(),
                null, // ward is not stored in HouseEntity directly
                entity.getDistrict(),
                entity.getRegion(),
                entity.getCountry(),
                null // postalCode is not stored in HouseEntity directly
        );

        House.HouseType houseType = House.HouseType.valueOf(entity.getHouseType().name());
        
        // Defaulting to VACANT as status is not stored in entity anymore
        // In a real scenario, we might want to calculate this or fetch it separately
        House.HouseStatus status = House.HouseStatus.VACANT; 

        return House.fromExisting(
                entity.getId(),
                entity.getPropertyCode(),
                entity.getName(),
                entity.getDescription(),
                houseType,
                entity.getLandlord().getId(),
                address,
                entity.getTotalFloors(),
                entity.getYearBuilt(),
                entity.getHasParking(),
                entity.getHasSecurity(),
                entity.getHasWater(),
                entity.getHasElectricity(),
                entity.getImageUrls(),
                entity.getMonthlyCommonCharges(),
                status,
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}