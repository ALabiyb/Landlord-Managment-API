package com.tz.rental.landlord_management.application.service;

import com.tz.rental.landlord_management.application.dto.CreateHouseRequest;
import com.tz.rental.landlord_management.application.dto.HouseResponse;
import com.tz.rental.landlord_management.application.mapper.HouseMapper;
import com.tz.rental.landlord_management.domain.exception.AlreadyExistsException;
import com.tz.rental.landlord_management.domain.exception.NotFoundException;
import com.tz.rental.landlord_management.domain.exception.UnauthorizedException;
import com.tz.rental.landlord_management.domain.model.aggregate.House;
import com.tz.rental.landlord_management.domain.model.aggregate.Landlord;
import com.tz.rental.landlord_management.domain.model.valueobject.Address;
import com.tz.rental.landlord_management.domain.repository.HouseRepository;
import com.tz.rental.landlord_management.domain.repository.LandlordRepository;
import com.tz.rental.landlord_management.infrastructure.persistence.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class HouseService {

    private final HouseRepository houseRepository;
    private final LandlordRepository landlordRepository;
    private final HouseMapper houseMapper;

    private Landlord.LandlordId getCurrentLandlordId() {
        UserEntity currentUser = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (currentUser.getLandlord() == null) {
            throw new IllegalStateException("The current user is not a landlord.");
        }
        return new Landlord.LandlordId(currentUser.getLandlord().getId());
    }

    @Transactional
    public HouseResponse createHouse(CreateHouseRequest request) {
        log.info("Creating house: {}", request.getName());
        Landlord.LandlordId landlordIdVO = getCurrentLandlordId();

        // Check if property code already exists for this landlord
        // Note: This check might need to be adjusted if property codes should be globally unique
        if (houseRepository.existsByPropertyCode(request.getPropertyCode())) {
            throw new AlreadyExistsException("House", "propertyCode=" + request.getPropertyCode());
        }

        Landlord landlord = landlordRepository.findById(landlordIdVO)
                .orElseThrow(() -> new NotFoundException("Landlord", landlordIdVO.value().toString()));

        Address address = new Address(
                request.getStreetAddress(),
                null,
                request.getDistrict(),
                request.getRegion(),
                request.getCountry(),
                null
        );

        House.HouseType houseType = House.HouseType.valueOf(request.getHouseType().toUpperCase());

        House house = House.create(
                request.getPropertyCode(),
                request.getName(),
                houseType,
                landlordIdVO,
                address
        );

        house.updateInformation(request.getName(), request.getDescription(), houseType);
        house.updateAmenities(request.getTotalFloors(), request.getYearBuilt(), request.getHasParking(), request.getHasSecurity());
        if (request.getMonthlyCommonCharges() != null && request.getMonthlyCommonCharges().compareTo(BigDecimal.ZERO) > 0) {
            house.updateCharges(request.getMonthlyCommonCharges());
        }

        House savedHouse = houseRepository.save(house);
        log.info("Created house with ID: {} for Landlord ID: {}", savedHouse.getId().value(), landlordIdVO.value());

        return houseMapper.toResponse(savedHouse, landlord);
    }

    public HouseResponse getHouse(UUID id) {
        log.info("Getting house by ID: {}", id);
        Landlord.LandlordId currentLandlordId = getCurrentLandlordId();

        House.HouseId houseId = new House.HouseId(id);
        House house = houseRepository.findById(houseId)
                .orElseThrow(() -> new NotFoundException("House", id));

        if (!house.getLandlordId().equals(currentLandlordId)) {
            throw new UnauthorizedException("You are not authorized to view this house.");
        }

        Landlord landlord = landlordRepository.findById(house.getLandlordId())
                .orElseThrow(() -> new NotFoundException("Landlord", house.getLandlordId().value()));

        return houseMapper.toResponse(house, landlord);
    }

    public List<HouseResponse> getAllHouses() {
        log.info("Getting all houses for current landlord");
        Landlord.LandlordId currentLandlordId = getCurrentLandlordId();

        List<House> houses = houseRepository.findByLandlordId(currentLandlordId);
        Landlord landlord = landlordRepository.findById(currentLandlordId)
                .orElseThrow(() -> new NotFoundException("Landlord", currentLandlordId.value()));

        return houses.stream()
                .map(house -> houseMapper.toResponse(house, landlord))
                .toList();
    }

    // This endpoint is now redundant if a landlord can only see their own houses.
    // Kept for now, but could be removed.
    public List<HouseResponse> getHousesByLandlord(UUID landlordId) {
        log.warn("getHousesByLandlord is being called. This should be restricted or used only by ADMINs.");
        Landlord.LandlordId currentLandlordId = getCurrentLandlordId();
        if (!currentLandlordId.value().equals(landlordId)) {
            throw new UnauthorizedException("You can only view your own houses.");
        }
        return getAllHouses();
    }

    public List<HouseResponse> getHousesByStatus(String status) {
        log.info("Getting houses by status: {} for current landlord", status);
        Landlord.LandlordId currentLandlordId = getCurrentLandlordId();

        House.HouseStatus houseStatus = House.HouseStatus.valueOf(status.toUpperCase());
        List<House> houses = houseRepository.findByLandlordIdAndStatus(currentLandlordId, houseStatus);

        Landlord landlord = landlordRepository.findById(currentLandlordId).orElse(null);
        return houses.stream()
                .map(house -> houseMapper.toResponse(house, landlord))
                .toList();
    }

    @Transactional
    public HouseResponse updateHouse(UUID id, CreateHouseRequest request) {
        log.info("Updating house ID: {}", id);
        Landlord.LandlordId currentLandlordId = getCurrentLandlordId();

        House.HouseId houseId = new House.HouseId(id);
        House house = houseRepository.findById(houseId)
                .orElseThrow(() -> new NotFoundException("House", id));

        if (!house.getLandlordId().equals(currentLandlordId)) {
            throw new UnauthorizedException("You are not authorized to update this house.");
        }

        Landlord landlord = landlordRepository.findById(currentLandlordId)
                .orElseThrow(() -> new NotFoundException("Landlord", currentLandlordId.value()));

        House.HouseType houseType = House.HouseType.valueOf(request.getHouseType().toUpperCase());
        house.updateInformation(request.getName(), request.getDescription(), houseType);

        Address newAddress = new Address(
                request.getStreetAddress(), null, request.getDistrict(), request.getRegion(), request.getCountry(), null);
        house.updateAddress(newAddress);

        house.updateAmenities(request.getTotalFloors(), request.getYearBuilt(), request.getHasParking(), request.getHasSecurity());
        if (request.getMonthlyCommonCharges() != null) {
            house.updateCharges(request.getMonthlyCommonCharges());
        }

        House updatedHouse = houseRepository.save(house);
        return houseMapper.toResponse(updatedHouse, landlord);
    }

    @Transactional
    public HouseResponse markHouseForMaintenance(UUID id) {
        log.info("Marking house for maintenance ID: {}", id);
        Landlord.LandlordId currentLandlordId = getCurrentLandlordId();

        House.HouseId houseId = new House.HouseId(id);
        House house = houseRepository.findById(houseId)
                .orElseThrow(() -> new NotFoundException("House", id));

        if (!house.getLandlordId().equals(currentLandlordId)) {
            throw new UnauthorizedException("You are not authorized to modify this house.");
        }

        house.markForMaintenance();
        House updatedHouse = houseRepository.save(house);

        Landlord landlord = landlordRepository.findById(house.getLandlordId()).orElse(null);
        return houseMapper.toResponse(updatedHouse, landlord);
    }

    @Transactional
    public HouseResponse markHouseAsActive(UUID id) {
        log.info("Marking house as active ID: {}", id);
        Landlord.LandlordId currentLandlordId = getCurrentLandlordId();

        House.HouseId houseId = new House.HouseId(id);
        House house = houseRepository.findById(houseId)
                .orElseThrow(() -> new NotFoundException("House", id));

        if (!house.getLandlordId().equals(currentLandlordId)) {
            throw new UnauthorizedException("You are not authorized to modify this house.");
        }

        house.markAsActive();
        House updatedHouse = houseRepository.save(house);

        Landlord landlord = landlordRepository.findById(house.getLandlordId()).orElse(null);
        return houseMapper.toResponse(updatedHouse, landlord);
    }

    @Transactional
    public HouseResponse markHouseAsVacant(UUID id) {
        log.info("Marking house as vacant ID: {}", id);
        Landlord.LandlordId currentLandlordId = getCurrentLandlordId();

        House.HouseId houseId = new House.HouseId(id);
        House house = houseRepository.findById(houseId)
                .orElseThrow(() -> new NotFoundException("House", id));

        if (!house.getLandlordId().equals(currentLandlordId)) {
            throw new UnauthorizedException("You are not authorized to modify this house.");
        }

        house.markAsVacant();
        House updatedHouse = houseRepository.save(house);

        Landlord landlord = landlordRepository.findById(house.getLandlordId()).orElse(null);
        return houseMapper.toResponse(updatedHouse, landlord);
    }

    @Transactional
    public void deleteHouse(UUID id) {
        log.info("Deleting house ID: {}", id);
        Landlord.LandlordId currentLandlordId = getCurrentLandlordId();

        House.HouseId houseId = new House.HouseId(id);
        House house = houseRepository.findById(houseId)
                .orElseThrow(() -> new NotFoundException("House", id));

        if (!house.getLandlordId().equals(currentLandlordId)) {
            throw new UnauthorizedException("You are not authorized to delete this house.");
        }

        houseRepository.delete(houseId);
    }
}