package com.tz.rental.landlord_management.application.mapper;

import com.tz.rental.landlord_management.application.dto.HouseResponse;
import com.tz.rental.landlord_management.domain.model.aggregate.House;
import com.tz.rental.landlord_management.domain.model.aggregate.Landlord;
import org.springframework.stereotype.Component;

@Component
public class HouseMapper {

    public HouseResponse toResponse(House house, Landlord landlord) {
        HouseResponse response = new HouseResponse();
        response.setId(house.getId().value());
        response.setPropertyCode(house.getPropertyCode());
        response.setName(house.getName());
        response.setDescription(house.getDescription());
        response.setHouseType(house.getHouseType().name());

        if (landlord != null) {
            response.setLandlordId(landlord.getId().value());
            response.setLandlordName(landlord.getFullName());
        }

        response.setStreetAddress(house.getAddress().getStreetAddress());
        response.setDistrict(house.getAddress().getDistrict());
        response.setRegion(house.getAddress().getRegion());
        response.setCountry(house.getAddress().getCountry());
        response.setTotalFloors(house.getTotalFloors());
        response.setYearBuilt(house.getYearBuilt());
        response.setHasParking(house.getHasParking());
        response.setHasSecurity(house.getHasSecurity());
        response.setMonthlyCommonCharges(house.getMonthlyCommonCharges().getAmount());
        response.setStatus(house.getStatus().name());

        return response;
    }
}