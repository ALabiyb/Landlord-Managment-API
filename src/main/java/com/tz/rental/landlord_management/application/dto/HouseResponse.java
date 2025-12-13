package com.tz.rental.landlord_management.application.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class HouseResponse {
    private UUID id;
    private String propertyCode;
    private String name;
    private String description;
    private String houseType;
    private String landlordName;
    private UUID landlordId;
    private String streetAddress;
    private String district;
    private String region;
    private String country;
    private Integer totalFloors;
    private Integer yearBuilt;
    private Boolean hasParking;
    private Boolean hasSecurity;
    private BigDecimal monthlyCommonCharges;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}