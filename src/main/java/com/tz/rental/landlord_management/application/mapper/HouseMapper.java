package com.tz.rental.landlord_management.application.mapper;

import com.tz.rental.landlord_management.application.dto.HouseResponse;
import com.tz.rental.landlord_management.domain.model.aggregate.House;
import com.tz.rental.landlord_management.domain.model.aggregate.Landlord;
import com.tz.rental.landlord_management.domain.model.valueobject.HouseType;
import org.springframework.stereotype.Component;

@Component("applicationHouseMapper")
public class HouseMapper {

    public HouseResponse toResponse(House house, Landlord landlord) {
        HouseResponse.HouseResponseBuilder builder = HouseResponse.builder()
                .id(house.getId().value())
                .propertyCode(house.getPropertyCode())
                .name(house.getName())
                .description(house.getDescription())
                .houseType(HouseType.valueOf(house.getHouseType().name()))
                .totalFloors(house.getTotalFloors())
                .yearBuilt(house.getYearBuilt())
                .hasParking(house.getHasParking())
                .hasSecurity(house.getHasSecurity())
                .hasWater(house.getHasWater())
                .hasElectricity(house.getHasElectricity())
                .imageUrls(house.getImageUrls())
                .monthlyCommonCharges(house.getMonthlyCommonCharges())
                .createdAt(house.getCreatedAt())
                .updatedAt(house.getUpdatedAt());

        if (house.getAddress() != null) {
            builder.streetAddress(house.getAddress().getStreetAddress())
                   .district(house.getAddress().getDistrict())
                   .region(house.getAddress().getRegion())
                   .country(house.getAddress().getCountry());
        }

        return builder.build();
    }
}