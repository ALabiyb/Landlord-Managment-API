package com.tz.rental.landlord_management.infrastructure.persistence.repository;

import com.tz.rental.landlord_management.domain.model.aggregate.Tenant;
import com.tz.rental.landlord_management.domain.repository.TenantRepository;
import com.tz.rental.landlord_management.infrastructure.persistence.entity.TenantEntity;
import com.tz.rental.landlord_management.infrastructure.persistence.mapper.TenantMapper;
import com.tz.rental.landlord_management.infrastructure.persistence.repository.jpa.JpaTenantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TenantRepositoryImpl implements TenantRepository {

    private final JpaTenantRepository jpaTenantRepository;
    private final TenantMapper tenantMapper;

    @Override
    public Tenant save(Tenant tenant) {
        TenantEntity entity = tenantMapper.toEntity(tenant);
        TenantEntity savedEntity = jpaTenantRepository.save(entity);
        return tenantMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Tenant> findById(UUID id) {
        return jpaTenantRepository.findById(id).map(tenantMapper::toDomain);
    }

    @Override
    public List<Tenant> findAll() {
        return jpaTenantRepository.findAll().stream()
                .map(tenantMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Tenant> findAllById(List<UUID> ids) {
        return jpaTenantRepository.findAllById(ids).stream()
                .map(tenantMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(UUID id) {
        jpaTenantRepository.deleteById(id);
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpaTenantRepository.existsByEmail(email);
    }

    @Override
    public boolean existsByPhoneNumber(String phoneNumber) {
        return jpaTenantRepository.existsByPhoneNumber(phoneNumber);
    }

    @Override
    public boolean existsByNationalId(String nationalId) {
        return jpaTenantRepository.existsByNationalId(nationalId);
    }
}