package com.tz.rental.landlord_management.application.service;

import com.tz.rental.landlord_management.application.dto.ContractTemplateResponse;
import com.tz.rental.landlord_management.domain.model.aggregate.ContractTemplate;
import org.springframework.stereotype.Component;

@Component("applicationContractTemplateMapper")
public class ContractTemplateMapper {

    public ContractTemplateResponse toResponse(ContractTemplate domain) {
        ContractTemplateResponse response = new ContractTemplateResponse();
        response.setId(domain.getId().value());
        response.setName(domain.getName());
        response.setContent(domain.getContent());
        response.setDescription(domain.getDescription());
        response.setActive(domain.isActive());
        response.setCreatedAt(domain.getCreatedAt());
        response.setUpdatedAt(domain.getUpdatedAt());
        return response;
    }
}