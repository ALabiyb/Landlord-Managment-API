package com.tz.rental.landlord_management.application.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class LandlordResponse {
    private UUID id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String nationalId;
    private String taxId;
    private boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}