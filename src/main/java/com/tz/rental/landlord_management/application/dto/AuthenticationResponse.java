package com.tz.rental.landlord_management.application.dto;

import com.tz.rental.landlord_management.domain.model.valueobject.Role;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class AuthenticationResponse {
    private final String jwt;
    private final String username;
    private final Role role;
    private final UUID userId;
    private final UUID landlordId; // Null if not a landlord
}