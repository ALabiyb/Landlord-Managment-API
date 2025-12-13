package com.tz.rental.landlord_management.infrastructure.persistence.mapper;

import com.tz.rental.landlord_management.domain.model.aggregate.ContractTemplate;
import com.tz.rental.landlord_management.infrastructure.persistence.entity.ContractTemplateEntity;
import org.springframework.stereotype.Component;

@Component("persistenceContractTemplateMapper")
public class ContractTemplateMapper {

    public ContractTemplateEntity toEntity(ContractTemplate domain) {
        ContractTemplateEntity entity = new ContractTemplateEntity();
        entity.setId(domain.getId().value());
        entity.setName(domain.getName());
        entity.setContent(domain.getContent());
        entity.setDescription(domain.getDescription());
        entity.setActive(domain.isActive());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        return entity;
    }

    public ContractTemplate toDomain(ContractTemplateEntity entity) {
        ContractTemplate domain = ContractTemplate.create(
                entity.getName(),
                entity.getContent(),
                entity.getDescription()
        );
        // Reconstruct state from entity
        if (!entity.isActive()) {
            domain.deactivate();
        }
        // Note: createdAt and updatedAt are handled by the aggregate's internal state or can be set via reflection for full reconstruction
        return domain;
    }
}