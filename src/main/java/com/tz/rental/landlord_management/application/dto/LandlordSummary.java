package com.tz.rental.landlord_management.application.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class LandlordSummary {
    private UUID id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
}