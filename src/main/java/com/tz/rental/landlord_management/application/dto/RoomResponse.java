package com.tz.rental.landlord_management.application.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.tz.rental.landlord_management.domain.model.valueobject.RoomStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RoomResponse {
    private UUID id;
    private UUID houseId;
    private String roomNumber;
    private String description;
    private BigDecimal monthlyRent;
    private String size;
    private RoomStatus status;
    private List<String> imageUrls;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private HouseSummary house;
    private TenantSummary tenant;
    private LeaseSummary lease;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HouseSummary {
        private UUID id;
        private String name;
        private String propertyCode;
        private String district;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TenantSummary {
        private UUID id;
        private String name;
        private String phoneNumber;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LeaseSummary {
        private UUID id;
        private LocalDateTime startDate;
        private LocalDateTime endDate;
    }
}