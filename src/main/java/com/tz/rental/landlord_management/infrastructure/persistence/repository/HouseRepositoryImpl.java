package com.tz.rental.landlord_management.infrastructure.persistence.repository;

import com.tz.rental.landlord_management.domain.model.aggregate.House;
import com.tz.rental.landlord_management.domain.model.aggregate.Landlord;
import com.tz.rental.landlord_management.domain.model.valueobject.RoomStatus;
import com.tz.rental.landlord_management.domain.repository.HouseRepository;
import com.tz.rental.landlord_management.infrastructure.persistence.entity.HouseEntity;
import com.tz.rental.landlord_management.infrastructure.persistence.entity.RoomEntity;
import com.tz.rental.landlord_management.infrastructure.persistence.mapper.HouseDomainMapper;
import com.tz.rental.landlord_management.infrastructure.persistence.repository.jpa.JpaHouseRepository;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class HouseRepositoryImpl implements HouseRepository {

    private static final String HOUSE_FIELD = "house";
    private final JpaHouseRepository houseRepository;
    private final HouseDomainMapper houseDomainMapper;

    @Override
    public House save(House house) {
        var entity = houseDomainMapper.toEntity(house);
        var savedEntity = houseRepository.save(entity);
        return houseDomainMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<House> findById(House.HouseId id) {
        return houseRepository.findById(id.value())
                .map(houseDomainMapper::toDomain);
    }

    @Override
    public Optional<House> findByPropertyCode(String propertyCode) {
        return houseRepository.findByPropertyCode(propertyCode)
                .map(houseDomainMapper::toDomain);
    }

    @Override
    public boolean existsByPropertyCode(String propertyCode) {
        return houseRepository.existsByPropertyCode(propertyCode);
    }

    @Override
    public List<House> findAll() {
        return houseRepository.findAll()
                .stream()
                .map(houseDomainMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<House> findByLandlordId(Landlord.LandlordId landlordId) {
        return houseRepository.findByLandlordId(landlordId.value())
                .stream()
                .map(houseDomainMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public long countByLandlordId(Landlord.LandlordId landlordId) {
        return houseRepository.countByLandlordId(landlordId.value());
    }

    @Override
    public List<House> findByStatus(House.HouseStatus status) {
        Specification<HouseEntity> spec = createStatusSpecification(status, null);
        return houseRepository.findAll(spec)
                .stream()
                .map(houseDomainMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<House> findByLandlordIdAndStatus(Landlord.LandlordId landlordId, House.HouseStatus status) {
        Specification<HouseEntity> spec = createStatusSpecification(status, landlordId.value());
        return houseRepository.findAll(spec)
                .stream()
                .map(houseDomainMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public long count() {
        return houseRepository.count();
    }

    @Override
    public void delete(House.HouseId id) {
        houseRepository.deleteById(id.value());
    }

    private Specification<HouseEntity> createStatusSpecification(House.HouseStatus status, java.util.UUID landlordId) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (landlordId != null) {
                predicates.add(cb.equal(root.get("landlord").get("id"), landlordId));
            }

            if (status == House.HouseStatus.VACANT) {
                // Find houses that have at least one vacant room
                Subquery<RoomEntity> subquery = query.subquery(RoomEntity.class);
                Root<RoomEntity> subRoot = subquery.from(RoomEntity.class);
                subquery.select(subRoot);
                subquery.where(
                        cb.equal(subRoot.get(HOUSE_FIELD), root),
                        cb.equal(subRoot.get("status"), RoomStatus.VACANT)
                );
                predicates.add(cb.exists(subquery));

            } else if (status == House.HouseStatus.OCCUPIED) {
                // Find houses where all rooms are occupied (and there's at least one room)
                Subquery<Long> countSubquery = query.subquery(Long.class);
                Root<RoomEntity> countSubRoot = countSubquery.from(RoomEntity.class);
                countSubquery.select(cb.count(countSubRoot));
                countSubquery.where(cb.equal(countSubRoot.get(HOUSE_FIELD), root));
                predicates.add(cb.greaterThan(countSubquery, 0L));

                Subquery<RoomEntity> subquery = query.subquery(RoomEntity.class);
                Root<RoomEntity> subRoot = subquery.from(RoomEntity.class);
                subquery.select(subRoot);
                subquery.where(
                        cb.equal(subRoot.get(HOUSE_FIELD), root),
                        cb.notEqual(subRoot.get("status"), RoomStatus.OCCUPIED)
                );
                predicates.add(cb.not(cb.exists(subquery)));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}