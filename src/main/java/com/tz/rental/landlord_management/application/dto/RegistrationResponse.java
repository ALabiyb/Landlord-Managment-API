package com.tz.rental.landlord_management.application.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RegistrationResponse {
    private boolean success;
    private String message;
    private UserSummary user;
    private LandlordSummary landlord;
}