package com.tz.rental.landlord_management.infrastructure.persistence.repository;

import com.tz.rental.landlord_management.domain.model.aggregate.House;
import com.tz.rental.landlord_management.domain.model.aggregate.Landlord;
import com.tz.rental.landlord_management.domain.repository.HouseRepository;
import com.tz.rental.landlord_management.infrastructure.persistence.mapper.HouseDomainMapper;
import com.tz.rental.landlord_management.infrastructure.persistence.repository.jpa.HouseJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class HouseRepositoryImpl implements HouseRepository {

    private final HouseJpaRepository houseJpaRepository;
    private final HouseDomainMapper houseDomainMapper;

    @Override
    public House save(House house) {
        var entity = houseDomainMapper.toEntity(house);
        var savedEntity = houseJpaRepository.save(entity);
        return houseDomainMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<House> findById(House.HouseId id) {
        return houseJpaRepository.findById(id.value())
                .map(houseDomainMapper::toDomain);
    }

    @Override
    public Optional<House> findByPropertyCode(String propertyCode) {
        return houseJpaRepository.findByPropertyCode(propertyCode)
                .map(houseDomainMapper::toDomain);
    }

    @Override
    public boolean existsByPropertyCode(String propertyCode) {
        return houseJpaRepository.existsByPropertyCode(propertyCode);
    }

    @Override
    public List<House> findAll() {
        return houseJpaRepository.findAll()
                .stream()
                .map(houseDomainMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<House> findByLandlordId(Landlord.LandlordId landlordId) {
        return houseJpaRepository.findByLandlordId(landlordId.value())
                .stream()
                .map(houseDomainMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public long countByLandlordId(Landlord.LandlordId landlordId) {
        return houseJpaRepository.countByLandlordId(landlordId.value());
    }

    @Override
    public List<House> findByStatus(House.HouseStatus status) {
        return houseJpaRepository.findByStatus(status.name())
                .stream()
                .map(houseDomainMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<House> findByLandlordIdAndStatus(Landlord.LandlordId landlordId, House.HouseStatus status) {
        return houseJpaRepository.findByLandlordIdAndStatus(landlordId.value(), status.name())
                .stream()
                .map(houseDomainMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public long count() {
        return houseJpaRepository.count();
    }

    @Override
    public void delete(House.HouseId id) {
        houseJpaRepository.deleteById(id.value());
    }
}