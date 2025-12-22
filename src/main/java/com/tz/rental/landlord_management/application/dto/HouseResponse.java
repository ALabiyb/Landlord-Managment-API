package com.tz.rental.landlord_management.application.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.tz.rental.landlord_management.domain.model.valueobject.HouseType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class HouseResponse {
    private UUID id;
    private String propertyCode;
    private String name;
    private String description;
    private HouseType houseType;
    private String streetAddress;
    private String district;
    private String region;
    private String country;
    private Integer totalFloors;
    private Integer yearBuilt;
    private Boolean hasParking;
    private Boolean hasSecurity;
    private Boolean hasWater;
    private Boolean hasElectricity;
    private BigDecimal monthlyCommonCharges;
    private List<String> imageUrls;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<RoomSummary> rooms;
    private Stats stats;

    @Data
    @Builder
    public static class RoomSummary {
        private UUID id;
        private String roomNumber;
        private BigDecimal monthlyRent;
        private String size;
        private String status;
        private TenantSummary tenant;
    }

    @Data
    @Builder
    public static class TenantSummary {
        private UUID id;
        private String name;
        private String phoneNumber;
    }

    @Data
    @Builder
    public static class Stats {
        private int totalRooms;
        private int occupiedRooms;
        private int vacantRooms;
        private BigDecimal monthlyIncome;
    }
}