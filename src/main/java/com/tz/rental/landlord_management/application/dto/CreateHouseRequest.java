package com.tz.rental.landlord_management.application.dto;

import com.tz.rental.landlord_management.domain.model.valueobject.HouseType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CreateHouseRequest {

    @NotBlank
    @Size(max = 50)
    private String propertyCode;

    @NotBlank
    @Size(max = 100)
    private String name;

    private String description;

    @NotNull
    private HouseType houseType;

    @Size(max = 255)
    private String streetAddress;

    @NotBlank
    @Size(max = 100)
    private String district;

    @NotBlank
    @Size(max = 100)
    private String region;

    @NotBlank
    @Size(max = 100)
    private String country;

    @Positive
    private Integer totalFloors;

    private Integer yearBuilt;

    private Boolean hasParking = false;
    private Boolean hasSecurity = false;
    private Boolean hasWater = false;
    private Boolean hasElectricity = false;

    private List<String> imageUrls;

    private BigDecimal monthlyCommonCharges;
}