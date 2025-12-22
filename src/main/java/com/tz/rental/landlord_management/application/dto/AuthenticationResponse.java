package com.tz.rental.landlord_management.application.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthenticationResponse {
    private String token;
    private String refreshToken;
    @Builder.Default
    private String type = "Bearer";
    private UserSummary user;
    private LandlordSummary landlord; // Null if user is not a landlord
}