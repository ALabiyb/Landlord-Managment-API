package com.tz.rental.landlord_management.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "Request to create a new house")
public class CreateHouseRequest {

    @NotBlank(message = "Property code is required")
    @Schema(description = "Unique code for the property", example = "PROP001")
    private String propertyCode;

    @NotBlank(message = "House name is required")
    @Schema(description = "Name of the house", example = "Kigamboni House")
    private String name;

    @Schema(description = "Detailed description of the house", example = "A beautiful house near the beach")
    private String description;

    @NotBlank(message = "House type is required")
    @Schema(description = "Type of house (e.g., APARTMENT, STANDALONE, COMPLEX)", example = "STANDALONE")
    private String houseType; // APARTMENT, STANDALONE, COMPLEX

    @NotBlank(message = "Street address is required")
    @Schema(description = "Street address of the house", example = "123 Main Street")
    private String streetAddress;

    @NotBlank(message = "District is required")
    @Schema(description = "District where the house is located", example = "Kigamboni")
    private String district;

    @NotBlank(message = "Region is required")
    @Schema(description = "Region where the house is located", example = "Dar es Salaam")
    private String region;

    @Schema(description = "Country where the house is located", example = "Tanzania")
    private String country = "Tanzania";

    private Integer totalFloors = 1;
    private Integer yearBuilt;
    private Boolean hasParking = false;
    private Boolean hasSecurity = false;
    private BigDecimal monthlyCommonCharges = BigDecimal.ZERO;
}