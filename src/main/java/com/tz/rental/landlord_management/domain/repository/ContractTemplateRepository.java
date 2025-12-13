package com.tz.rental.landlord_management.domain.repository;

import com.tz.rental.landlord_management.domain.model.aggregate.ContractTemplate;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ContractTemplateRepository {
    ContractTemplate save(ContractTemplate template);
    Optional<ContractTemplate> findById(UUID id);
    List<ContractTemplate> findAll();
    Optional<ContractTemplate> findByName(String name);
    void deleteById(UUID id);
}