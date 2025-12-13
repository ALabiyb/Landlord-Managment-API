package com.tz.rental.landlord_management.api.rest.controller;

import com.tz.rental.landlord_management.api.rest.dto.ApiResponse;
import com.tz.rental.landlord_management.application.dto.CreateHouseRequest;
import com.tz.rental.landlord_management.application.dto.HouseResponse;
import com.tz.rental.landlord_management.application.service.HouseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/houses")
@RequiredArgsConstructor
@Tag(name = "Houses", description = "House management endpoints")
public class HouseController {

    private final HouseService houseService;

    @PostMapping
    @Operation(summary = "Create a new house",
            description = "Register a new rental house/property")
    public ResponseEntity<ApiResponse<HouseResponse>> createHouse(
            @Valid @RequestBody CreateHouseRequest request) {

        HouseResponse response = houseService.createHouse(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("House created successfully", response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get house by ID",
            description = "Retrieve detailed information about a specific house")
    public ResponseEntity<ApiResponse<HouseResponse>> getHouse(
            @PathVariable UUID id) {

        HouseResponse response = houseService.getHouse(id);
        return ResponseEntity.ok(
                ApiResponse.success("House retrieved successfully", response)
        );
    }

    @GetMapping
    @Operation(summary = "Get all houses",
            description = "Retrieve a list of all houses in the system")
    public ResponseEntity<ApiResponse<List<HouseResponse>>> getAllHouses() {
        List<HouseResponse> response = houseService.getAllHouses();
        return ResponseEntity.ok(
                ApiResponse.success("Houses retrieved successfully", response)
        );
    }

    @GetMapping("/landlord/{landlordId}")
    @Operation(summary = "Get houses by landlord",
            description = "Retrieve all houses owned by a specific landlord")
    public ResponseEntity<ApiResponse<List<HouseResponse>>> getHousesByLandlord(
            @PathVariable UUID landlordId) {

        List<HouseResponse> response = houseService.getHousesByLandlord(landlordId);
        return ResponseEntity.ok(
                ApiResponse.success("Houses retrieved successfully", response)
        );
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get houses by status",
            description = "Retrieve houses by their status (ACTIVE, MAINTENANCE, VACANT)")
    public ResponseEntity<ApiResponse<List<HouseResponse>>> getHousesByStatus(
            @PathVariable String status) {

        List<HouseResponse> response = houseService.getHousesByStatus(status);
        return ResponseEntity.ok(
                ApiResponse.success("Houses retrieved successfully", response)
        );
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update house information",
            description = "Update house details and information")
    public ResponseEntity<ApiResponse<HouseResponse>> updateHouse(
            @PathVariable UUID id,
            @Valid @RequestBody CreateHouseRequest request) {

        HouseResponse response = houseService.updateHouse(id, request);
        return ResponseEntity.ok(
                ApiResponse.success("House updated successfully", response)
        );
    }

    @PutMapping("/{id}/maintenance")
    @Operation(summary = "Mark house for maintenance",
            description = "Change house status to MAINTENANCE for repairs")
    public ResponseEntity<ApiResponse<HouseResponse>> markForMaintenance(
            @PathVariable UUID id) {

        HouseResponse response = houseService.markHouseForMaintenance(id);
        return ResponseEntity.ok(
                ApiResponse.success("House marked for maintenance", response)
        );
    }

    @PutMapping("/{id}/active")
    @Operation(summary = "Mark house as active",
            description = "Change house status to ACTIVE")
    public ResponseEntity<ApiResponse<HouseResponse>> markAsActive(
            @PathVariable UUID id) {

        HouseResponse response = houseService.markHouseAsActive(id);
        return ResponseEntity.ok(
                ApiResponse.success("House marked as active", response)
        );
    }

    @PutMapping("/{id}/vacant")
    @Operation(summary = "Mark house as vacant",
            description = "Change house status to VACANT")
    public ResponseEntity<ApiResponse<HouseResponse>> markAsVacant(
            @PathVariable UUID id) {

        HouseResponse response = houseService.markHouseAsVacant(id);
        return ResponseEntity.ok(
                ApiResponse.success("House marked as vacant", response)
        );
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete house",
            description = "Remove a house from the system")
    public ResponseEntity<ApiResponse<Void>> deleteHouse(
            @PathVariable UUID id) {

        houseService.deleteHouse(id);
        return ResponseEntity.ok(
                ApiResponse.success("House deleted successfully")
        );
    }

    @GetMapping("/{id}/status")
    @Operation(summary = "Check house status",
            description = "Get current status of a house")
    public ResponseEntity<ApiResponse<Object>> checkHouseStatus(
            @PathVariable UUID id) {

        HouseResponse house = houseService.getHouse(id);

        var statusInfo = new Object() {
            public final UUID id = house.getId();
            public final String name = house.getName();
            public final String status = house.getStatus();
            public final boolean isActive = "ACTIVE".equals(house.getStatus());
            public final boolean isUnderMaintenance = "MAINTENANCE".equals(house.getStatus());
            public final boolean isVacant = "VACANT".equals(house.getStatus());
        };

        return ResponseEntity.ok(
                ApiResponse.success("House status retrieved", statusInfo)
        );
    }
}