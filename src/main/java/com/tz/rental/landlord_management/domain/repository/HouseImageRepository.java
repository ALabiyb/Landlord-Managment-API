package com.tz.rental.landlord_management.domain.repository;

import com.tz.rental.landlord_management.domain.model.aggregate.HouseImage;
import java.util.UUID;
import java.util.Optional;

public interface HouseImageRepository {
    HouseImage save(HouseImage image);

    Optional<HouseImage> findById(UUID id);

    java.util.List<HouseImage> findByHouseId(UUID houseId);

    void delete(UUID id);
}
