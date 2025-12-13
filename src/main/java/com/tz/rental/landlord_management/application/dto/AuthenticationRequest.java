package com.tz.rental.landlord_management.application.dto;

import lombok.Data;

@Data
public class AuthenticationRequest {
    private String username;
    private String password;
}