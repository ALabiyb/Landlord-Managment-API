package com.tz.rental.landlord_management.infrastructure.persistence.repository;

import com.tz.rental.landlord_management.domain.model.aggregate.Landlord;
import com.tz.rental.landlord_management.domain.model.aggregate.Lease;
import com.tz.rental.landlord_management.domain.model.valueobject.LeaseStatus;
import com.tz.rental.landlord_management.domain.repository.LeaseRepository;
import com.tz.rental.landlord_management.infrastructure.persistence.mapper.LeaseMapper;
import com.tz.rental.landlord_management.infrastructure.persistence.repository.jpa.JpaLeaseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class LeaseRepositoryImpl implements LeaseRepository {

    private final JpaLeaseRepository jpaLeaseRepository;
    private final LeaseMapper leaseMapper;

    @Override
    public Lease save(Lease lease) {
        var entity = leaseMapper.toEntity(lease);
        var savedEntity = jpaLeaseRepository.save(entity);
        return leaseMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Lease> findById(UUID id) {
        return jpaLeaseRepository.findById(id).map(leaseMapper::toDomain);
    }

    @Override
    public List<Lease> findAll() {
        return jpaLeaseRepository.findAll().stream()
                .map(leaseMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Lease> findByTenantId(UUID tenantId) {
        return jpaLeaseRepository.findByTenantId(tenantId).stream()
                .map(leaseMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Lease> findByLandlordId(Landlord.LandlordId landlordId) {
        return jpaLeaseRepository.findByLandlordId(landlordId.value()).stream()
                .map(leaseMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public long countActiveByLandlordId(Landlord.LandlordId landlordId) {
        return jpaLeaseRepository.countByLandlordIdAndStatus(landlordId.value(), LeaseStatus.ACTIVE);
    }

    @Override
    public Optional<Lease> findByRoomIdAndStatus(UUID roomId, String status) {
        return jpaLeaseRepository.findActiveLeaseByRoomId(roomId).map(leaseMapper::toDomain);
    }

    @Override
    public boolean isRoomOccupied(UUID roomId) {
        return jpaLeaseRepository.existsActiveLeaseForRoom(roomId);
    }

    @Override
    public List<Lease> findAllByStatus(LeaseStatus status) {
        return jpaLeaseRepository.findByStatus(status).stream()
                .map(leaseMapper::toDomain)
                .collect(Collectors.toList());
    }
}