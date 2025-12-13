package com.tz.rental.landlord_management.infrastructure.persistence.repository;

import com.tz.rental.landlord_management.domain.model.aggregate.Landlord;
import com.tz.rental.landlord_management.domain.model.valueobject.Email;
import com.tz.rental.landlord_management.domain.model.valueobject.PhoneNumber;
import com.tz.rental.landlord_management.domain.repository.LandlordRepository;
import com.tz.rental.landlord_management.infrastructure.persistence.mapper.PersistenceLandlordMapper; // Corrected import
import com.tz.rental.landlord_management.infrastructure.persistence.repository.jpa.JpaLandlordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class LandlordRepositoryImpl implements LandlordRepository {

    private final JpaLandlordRepository jpaLandlordRepository;
    private final PersistenceLandlordMapper persistenceLandlordMapper; // Injected persistence mapper

    @Override
    public Landlord save(Landlord landlord) {
        var entity = persistenceLandlordMapper.toEntity(landlord);
        var savedEntity = jpaLandlordRepository.save(entity);
        return persistenceLandlordMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Landlord> findById(Landlord.LandlordId id) {
        return jpaLandlordRepository.findById(id.value())
                .map(persistenceLandlordMapper::toDomain);
    }

    @Override
    public Optional<Landlord> findByEmail(Email email) {
        return jpaLandlordRepository.findByEmail(email.getValue())
                .map(persistenceLandlordMapper::toDomain);
    }

    @Override
    public Optional<Landlord> findByPhoneNumber(PhoneNumber phoneNumber) {
        return jpaLandlordRepository.findByPhoneNumber(phoneNumber.getValue())
                .map(persistenceLandlordMapper::toDomain);
    }

    @Override
    public List<Landlord> findAll() {
        return jpaLandlordRepository.findAll()
                .stream()
                .map(persistenceLandlordMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Landlord.LandlordId id) {
        jpaLandlordRepository.deleteById(id.value());
    }
}