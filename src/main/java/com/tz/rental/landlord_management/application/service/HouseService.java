package com.tz.rental.landlord_management.application.service;

import com.tz.rental.landlord_management.application.dto.CreateHouseRequest;
import com.tz.rental.landlord_management.application.dto.HouseResponse;
import com.tz.rental.landlord_management.application.dto.PaginatedResponse;
import com.tz.rental.landlord_management.domain.exception.AlreadyExistsException;
import com.tz.rental.landlord_management.domain.exception.NotFoundException;
import com.tz.rental.landlord_management.domain.model.valueobject.RoomStatus;
import com.tz.rental.landlord_management.infrastructure.persistence.entity.HouseEntity;
import com.tz.rental.landlord_management.infrastructure.persistence.entity.LandlordEntity;
import com.tz.rental.landlord_management.infrastructure.persistence.entity.RoomEntity;
import com.tz.rental.landlord_management.infrastructure.persistence.repository.jpa.JpaHouseRepository;
import com.tz.rental.landlord_management.infrastructure.persistence.repository.jpa.JpaLandlordRepository;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class HouseService {

    private static final String HOUSE_FIELD = "house";
    private static final String HOUSE_NOT_FOUND_MSG = "House not found with ID: ";
    private final JpaHouseRepository houseRepository;
    private final JpaLandlordRepository landlordRepository;

    @Transactional
    public HouseResponse createHouse(CreateHouseRequest request) {
        houseRepository.findByPropertyCode(request.getPropertyCode()).ifPresent(h -> {
            throw new AlreadyExistsException("House with property code " + request.getPropertyCode() + " already exists.");
        });

        LandlordEntity landlord = getCurrentLandlord();

        HouseEntity houseEntity = new HouseEntity();
        houseEntity.setId(UUID.randomUUID());
        houseEntity.setLandlord(landlord);
        // Map request to entity
        mapRequestToEntity(request, houseEntity);

        HouseEntity savedHouse = houseRepository.save(houseEntity);
        return mapEntityToResponse(savedHouse, false);
    }

    @Transactional(readOnly = true)
    public HouseResponse getHouseById(UUID id, Boolean includeRooms) {
        HouseEntity houseEntity = houseRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(HOUSE_NOT_FOUND_MSG + id));
        return mapEntityToResponse(houseEntity, includeRooms != null && includeRooms);
    }

    @Transactional(readOnly = true)
    public PaginatedResponse<List<HouseResponse>> getAllHouses(Pageable pageable, String status) {
        LandlordEntity landlord = getCurrentLandlord();

        Specification<HouseEntity> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("landlord"), landlord));

            if (status != null && !status.equalsIgnoreCase("ALL")) {
                if (status.equalsIgnoreCase("VACANT")) {
                    // Find houses that have at least one vacant room
                    Subquery<RoomEntity> subquery = query.subquery(RoomEntity.class);
                    Root<RoomEntity> subRoot = subquery.from(RoomEntity.class);
                    subquery.select(subRoot);
                    subquery.where(
                            cb.equal(subRoot.get(HOUSE_FIELD), root), // Correlated subquery
                            cb.equal(subRoot.get("status"), RoomStatus.VACANT)
                    );
                    predicates.add(cb.exists(subquery));

                } else if (status.equalsIgnoreCase("OCCUPIED")) {
                    // Find houses where all rooms are occupied (and there's at least one room)
                    // 1. House must have rooms
                    Subquery<Long> countSubquery = query.subquery(Long.class);
                    Root<RoomEntity> countSubRoot = countSubquery.from(RoomEntity.class);
                    countSubquery.select(cb.count(countSubRoot));
                    countSubquery.where(cb.equal(countSubRoot.get(HOUSE_FIELD), root));
                    predicates.add(cb.greaterThan(countSubquery, 0L));

                    // 2. No room exists with a status other than OCCUPIED
                    Subquery<RoomEntity> subquery = query.subquery(RoomEntity.class);
                    Root<RoomEntity> subRoot = subquery.from(RoomEntity.class);
                    subquery.select(subRoot);
                    subquery.where(
                            cb.equal(subRoot.get(HOUSE_FIELD), root), // Correlated subquery
                            cb.notEqual(subRoot.get("status"), RoomStatus.OCCUPIED)
                    );
                    predicates.add(cb.not(cb.exists(subquery)));
                }
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<HouseEntity> housePage = houseRepository.findAll(spec, pageable);

        List<HouseResponse> houseResponses = housePage.getContent().stream()
                .map(house -> mapEntityToResponse(house, false))
                .toList();

        return PaginatedResponse.<List<HouseResponse>>builder()
                .data(houseResponses)
                .pagination(PaginatedResponse.Pagination.builder()
                        .currentPage(housePage.getNumber())
                        .totalPages(housePage.getTotalPages())
                        .totalItems(housePage.getTotalElements())
                        .itemsPerPage(housePage.getSize())
                        .build())
                .build();
    }

    @Transactional
    public HouseResponse updateHouse(UUID id, CreateHouseRequest request) {
        HouseEntity houseEntity = houseRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(HOUSE_NOT_FOUND_MSG + id));

        // Check for property code uniqueness if it's being changed
        if (!houseEntity.getPropertyCode().equals(request.getPropertyCode())) {
            houseRepository.findByPropertyCode(request.getPropertyCode()).ifPresent(h -> {
                throw new AlreadyExistsException("House with property code " + request.getPropertyCode() + " already exists.");
            });
        }

        mapRequestToEntity(request, houseEntity);
        HouseEntity updatedHouse = houseRepository.save(houseEntity);
        return mapEntityToResponse(updatedHouse, false);
    }

    @Transactional
    public void deleteHouse(UUID id) {
        HouseEntity houseEntity = houseRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(HOUSE_NOT_FOUND_MSG + id));
        houseRepository.delete(houseEntity);
    }

    private LandlordEntity getCurrentLandlord() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return landlordRepository.findByUserUsername(username)
                .orElseThrow(() -> new IllegalStateException("Landlord not found for current user."));
    }

    private void mapRequestToEntity(CreateHouseRequest request, HouseEntity entity) {
        entity.setPropertyCode(request.getPropertyCode());
        entity.setName(request.getName());
        entity.setDescription(request.getDescription());
        entity.setHouseType(request.getHouseType());
        entity.setStreetAddress(request.getStreetAddress());
        entity.setDistrict(request.getDistrict());
        entity.setRegion(request.getRegion());
        entity.setCountry(request.getCountry());
        entity.setTotalFloors(request.getTotalFloors());
        entity.setYearBuilt(request.getYearBuilt());
        entity.setHasParking(request.getHasParking());
        entity.setHasSecurity(request.getHasSecurity());
        entity.setHasWater(request.getHasWater());
        entity.setHasElectricity(request.getHasElectricity());
        entity.setImageUrls(request.getImageUrls());
        entity.setMonthlyCommonCharges(request.getMonthlyCommonCharges());
    }

    private HouseResponse mapEntityToResponse(HouseEntity entity, boolean includeRooms) {
        HouseResponse.HouseResponseBuilder builder = HouseResponse.builder()
                .id(entity.getId())
                .propertyCode(entity.getPropertyCode())
                .name(entity.getName())
                .description(entity.getDescription())
                .houseType(entity.getHouseType())
                .streetAddress(entity.getStreetAddress())
                .district(entity.getDistrict())
                .region(entity.getRegion())
                .country(entity.getCountry())
                .totalFloors(entity.getTotalFloors())
                .yearBuilt(entity.getYearBuilt())
                .hasParking(entity.getHasParking())
                .hasSecurity(entity.getHasSecurity())
                .hasWater(entity.getHasWater())
                .hasElectricity(entity.getHasElectricity())
                .monthlyCommonCharges(entity.getMonthlyCommonCharges())
                .imageUrls(entity.getImageUrls())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt());

        int totalRooms = entity.getRooms().size();
        int occupiedRooms = (int) entity.getRooms().stream().filter(r -> r.getStatus() == RoomStatus.OCCUPIED).count();
        int vacantRooms = totalRooms - occupiedRooms;
        BigDecimal monthlyIncome = entity.getRooms().stream()
                .filter(r -> r.getStatus() == RoomStatus.OCCUPIED)
                .map(RoomEntity::getMonthlyRent)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        builder.stats(HouseResponse.Stats.builder()
                .totalRooms(totalRooms)
                .occupiedRooms(occupiedRooms)
                .vacantRooms(vacantRooms)
                .monthlyIncome(monthlyIncome)
                .build());

        if (includeRooms) {
            // This will be implemented later
        }

        return builder.build();
    }
}