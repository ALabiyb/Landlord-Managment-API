package com.tz.rental.landlord_management.application.mapper;

import com.tz.rental.landlord_management.application.dto.HouseResponse;
import com.tz.rental.landlord_management.domain.model.aggregate.House;
import com.tz.rental.landlord_management.domain.model.valueobject.HouseType;
import org.springframework.stereotype.Component;

@Component("applicationHouseMapper")
public class HouseMapper {

    public HouseResponse toResponse(House house) {
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
                // .hasElectricity(house.getHasElectricity())
                // .imageUrls(house.getImageUrls()) // Removed
                .monthlyCommonCharges(house.getMonthlyCommonCharges())
                .createdAt(house.getCreatedAt())
                .updatedAt(house.getUpdatedAt());

        if (house.getImages() != null) {
            java.util.List<com.tz.rental.landlord_management.application.dto.HouseImageResponse> imageResponses = house
                    .getImages().stream()
                    .map(img -> com.tz.rental.landlord_management.application.dto.HouseImageResponse.builder()
                            .id(img.getId())
                            .imageUrl("/api/images/" + img.getFileName())
                            .thumbnailUrl(img.getThumbnailPath() != null
                                    ? "/api/images/thumbnails/"
                                            + java.nio.file.Paths.get(img.getThumbnailPath()).getFileName().toString()
                                    : null)
                            .caption(img.getCaption())
                            .isPrimary(img.isPrimary())
                            .displayOrder(img.getDisplayOrder())
                            .build())
                    .collect(java.util.stream.Collectors.toList());

            builder.images(imageResponses);

            String primaryImageUrl = imageResponses.stream()
                    .filter(com.tz.rental.landlord_management.application.dto.HouseImageResponse::isPrimary)
                    .findFirst()
                    .map(com.tz.rental.landlord_management.application.dto.HouseImageResponse::getImageUrl)
                    .orElse(imageResponses.isEmpty() ? null : imageResponses.get(0).getImageUrl());

            builder.primaryImageUrl(primaryImageUrl);
        }

        if (house.getAddress() != null) {
            builder.streetAddress(house.getAddress().getStreetAddress())
                    .district(house.getAddress().getDistrict())
                    .region(house.getAddress().getRegion())
                    .country(house.getAddress().getCountry());
        }

        return builder.build();
    }
}