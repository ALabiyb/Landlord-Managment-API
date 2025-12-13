package com.tz.rental.landlord_management.infrastructure.persistence.repository.jpa;

import com.tz.rental.landlord_management.infrastructure.persistence.entity.ContractTemplateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaContractTemplateRepository extends JpaRepository<ContractTemplateEntity, UUID> {
    Optional<ContractTemplateEntity> findByName(String name);
}