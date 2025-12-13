package com.tz.rental.landlord_management.infrastructure.persistence.mapper;

import com.tz.rental.landlord_management.domain.model.aggregate.Landlord;
import com.tz.rental.landlord_management.infrastructure.persistence.entity.LandlordEntity;
import org.springframework.stereotype.Component;

@Component
public class LandlordDomainMapper {

    public LandlordEntity toEntity(Landlord landlord) {
        LandlordEntity entity = new LandlordEntity();
        entity.setId(landlord.getId().value());
        entity.setFirstName(landlord.getFirstName());
        entity.setLastName(landlord.getLastName());
        entity.setEmail(landlord.getEmail().getValue());
        entity.setPhoneNumber(landlord.getPhoneNumber().getValue());
        entity.setNationalId(landlord.getNationalId());
        entity.setTaxId(landlord.getTaxId());
        entity.setActive(landlord.isActive());
        // Note: We don't set createdAt/updatedAt - they're managed by JPA
        return entity;
    }

    public Landlord toDomain(LandlordEntity entity) {
        return Landlord.fromExisting(
                entity.getId(),
                entity.getFirstName(),
                entity.getLastName(),
                entity.getEmail(),
                entity.getPhoneNumber(),
                entity.getNationalId(),
                entity.getTaxId(),
                entity.isActive(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}