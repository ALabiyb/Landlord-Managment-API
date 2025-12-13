package com.tz.rental.landlord_management.application.service;

import com.tz.rental.landlord_management.infrastructure.persistence.entity.UserEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component("landlordSecurityService") // Bean name for @PreAuthorize
public class LandlordSecurityService {

    public boolean isOwner(Authentication authentication, UUID landlordId) {
        if (authentication.getPrincipal() instanceof UserEntity currentUser) {
            return currentUser.getLandlord() != null && currentUser.getLandlord().getId().equals(landlordId);
        }
        return false;
    }
}