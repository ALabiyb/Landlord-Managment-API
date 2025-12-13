package com.tz.rental.landlord_management.infrastructure.persistence.mapper;

import com.tz.rental.landlord_management.domain.model.aggregate.Landlord;
import com.tz.rental.landlord_management.infrastructure.persistence.entity.LandlordEntity;
import org.springframework.stereotype.Component;

@Component("persistenceLandlordMapper") // Give it a unique bean name
public class PersistenceLandlordMapper {

    public LandlordEntity toEntity(Landlord domain) {
        LandlordEntity entity = new LandlordEntity();
        entity.setId(domain.getId().value());
        entity.setFirstName(domain.getFirstName());
        entity.setLastName(domain.getLastName());
        entity.setEmail(domain.getEmail().getValue());
        entity.setPhoneNumber(domain.getPhoneNumber().getValue());
        entity.setNationalId(domain.getNationalId());
        entity.setTaxId(domain.getTaxId());
        entity.setActive(domain.isActive());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        return entity;
    }

    public Landlord toDomain(LandlordEntity entity) {
        // Reconstruct the domain aggregate from the entity
        Landlord landlord = Landlord.fromExisting(
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
        return landlord;
    }
}