package com.tz.rental.landlord_management.infrastructure.persistence.repository;

import com.tz.rental.landlord_management.domain.model.aggregate.ContractTemplate;
import com.tz.rental.landlord_management.domain.repository.ContractTemplateRepository;
import com.tz.rental.landlord_management.infrastructure.persistence.mapper.ContractTemplateMapper;
import com.tz.rental.landlord_management.infrastructure.persistence.repository.jpa.JpaContractTemplateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ContractTemplateRepositoryImpl implements ContractTemplateRepository {

    private final JpaContractTemplateRepository jpaContractTemplateRepository;
    private final ContractTemplateMapper contractTemplateMapper;

    @Override
    public ContractTemplate save(ContractTemplate template) {
        var entity = contractTemplateMapper.toEntity(template);
        var savedEntity = jpaContractTemplateRepository.save(entity);
        return contractTemplateMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<ContractTemplate> findById(UUID id) {
        return jpaContractTemplateRepository.findById(id).map(contractTemplateMapper::toDomain);
    }

    @Override
    public List<ContractTemplate> findAll() {
        return jpaContractTemplateRepository.findAll().stream()
                .map(contractTemplateMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<ContractTemplate> findByName(String name) {
        return jpaContractTemplateRepository.findByName(name).map(contractTemplateMapper::toDomain);
    }

    @Override
    public void deleteById(UUID id) {
        jpaContractTemplateRepository.deleteById(id);
    }
}