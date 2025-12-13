package com.tz.rental.landlord_management.application.mapper;

import com.tz.rental.landlord_management.application.dto.LandlordResponse;
import com.tz.rental.landlord_management.domain.model.aggregate.Landlord;
import org.springframework.stereotype.Component;

@Component("applicationLandlordMapper") // Give it a unique bean name
public class ApplicationLandlordMapper {

    public LandlordResponse toResponse(Landlord landlord) {
        LandlordResponse response = new LandlordResponse();
        response.setId(landlord.getId().value());
        response.setFirstName(landlord.getFirstName());
        response.setLastName(landlord.getLastName());
        response.setEmail(landlord.getEmail().getValue());
        response.setPhoneNumber(landlord.getPhoneNumber().getValue());
        response.setNationalId(landlord.getNationalId());
        response.setTaxId(landlord.getTaxId());
        response.setActive(landlord.isActive());
        response.setCreatedAt(landlord.getCreatedAt());
        response.setUpdatedAt(landlord.getUpdatedAt());
        return response;
    }
}