package com.tz.rental.landlord_management.infrastructure.persistence.repository;

import com.tz.rental.landlord_management.domain.model.aggregate.HouseImage;
import com.tz.rental.landlord_management.domain.repository.HouseImageRepository;
import com.tz.rental.landlord_management.infrastructure.persistence.entity.HouseImageEntity;
import com.tz.rental.landlord_management.infrastructure.persistence.entity.HouseEntity;
import com.tz.rental.landlord_management.infrastructure.persistence.mapper.HouseImageDomainMapper;
import com.tz.rental.landlord_management.infrastructure.persistence.repository.jpa.JpaHouseImageRepository;
import com.tz.rental.landlord_management.infrastructure.persistence.repository.jpa.JpaHouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class HouseImageRepositoryImpl implements HouseImageRepository {

    private final JpaHouseImageRepository jpaHouseImageRepository;
    private final JpaHouseRepository jpaHouseRepository;
    private final HouseImageDomainMapper houseImageDomainMapper;

    @Override
    public HouseImage save(HouseImage image) {
        if (image.getHouseId() == null) {
            throw new IllegalArgumentException("HouseImage must have a houseId");
        }

        HouseEntity houseEntity = jpaHouseRepository.findById(image.getHouseId())
                .orElseThrow(() -> new IllegalArgumentException("House not found with ID: " + image.getHouseId()));

        HouseImageEntity entity = houseImageDomainMapper.toEntity(image, houseEntity);
        entity = jpaHouseImageRepository.save(entity);
        return houseImageDomainMapper.toDomain(entity);
    }

    @Override
    public java.util.List<HouseImage> findByHouseId(java.util.UUID houseId) {
        return jpaHouseImageRepository.findByHouseId(houseId).stream()
                .map(houseImageDomainMapper::toDomain)
                .collect(java.util.stream.Collectors.toList());
    }

    @Override
    public java.util.Optional<HouseImage> findById(java.util.UUID id) {
        return jpaHouseImageRepository.findById(id)
                .map(houseImageDomainMapper::toDomain);
    }

    @Override
    public void delete(UUID id) {
        jpaHouseImageRepository.deleteById(id);
    }
}
