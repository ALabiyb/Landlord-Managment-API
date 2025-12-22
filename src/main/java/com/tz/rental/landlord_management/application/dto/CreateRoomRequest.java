package com.tz.rental.landlord_management.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
public class CreateRoomRequest {

    @NotNull
    private UUID houseId;

    @NotBlank
    @Size(max = 50)
    private String roomNumber;

    private String description;

    @NotNull
    @Positive
    private BigDecimal monthlyRent;

    @Size(max = 50)
    private String size;

    private List<String> imageUrls;
}