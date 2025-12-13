package com.tz.rental.landlord_management.api.rest.controller;

import com.tz.rental.landlord_management.api.rest.dto.ApiResponse;
import com.tz.rental.landlord_management.application.dto.CreateLandlordRequest;
import com.tz.rental.landlord_management.application.dto.LandlordResponse;
import com.tz.rental.landlord_management.application.service.LandlordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/landlords")
@RequiredArgsConstructor
@Tag(name = "Landlords", description = "Landlord management endpoints")
public class LandlordController {

    private final LandlordService landlordService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')") // Only ADMIN can create landlords directly
    @Operation(summary = "Create a new landlord (Admin only)",
            description = "Register a new landlord in the system. This is an admin-only endpoint. Landlords should use the /api/v1/auth/register/landlord endpoint.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Landlord created successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input data"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Landlord already exists")
    })
    public ResponseEntity<ApiResponse<LandlordResponse>> createLandlord(
            @Valid @RequestBody CreateLandlordRequest request) {

        LandlordResponse response = landlordService.createLandlord(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Landlord created successfully", response));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('LANDLORD') and @landlordSecurityService.isOwner(authentication, #id))")
    @Operation(summary = "Get landlord by ID",
            description = "Retrieve detailed information about a specific landlord. Landlords can only view their own profile.")
    public ResponseEntity<ApiResponse<LandlordResponse>> getLandlord(
            @PathVariable UUID id) {

        LandlordResponse response = landlordService.getLandlord(id);
        return ResponseEntity.ok(
                ApiResponse.success("Landlord retrieved successfully", response)
        );
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all landlords (Admin only)",
            description = "Retrieve a list of all landlords in the system. This is an admin-only endpoint.")
    public ResponseEntity<ApiResponse<List<LandlordResponse>>> getAllLandlords() {
        List<LandlordResponse> response = landlordService.getAllLandlords();
        return ResponseEntity.ok(
                ApiResponse.success("Landlords retrieved successfully", response)
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('LANDLORD') and @landlordSecurityService.isOwner(authentication, #id))")
    @Operation(summary = "Update landlord",
            description = "Update landlord information. Landlords can only update their own profile.")
    public ResponseEntity<ApiResponse<LandlordResponse>> updateLandlord(
            @PathVariable UUID id,
            @Valid @RequestBody CreateLandlordRequest request) {

        LandlordResponse response = landlordService.updateLandlord(id, request);
        return ResponseEntity.ok(
                ApiResponse.success("Landlord updated successfully", response)
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete landlord (Admin only)",
            description = "Permanently delete a landlord from the system. This is an admin-only endpoint.")
    public ResponseEntity<ApiResponse<Void>> deleteLandlord(
            @PathVariable UUID id) {

        landlordService.deleteLandlord(id);
        return ResponseEntity.ok(
                ApiResponse.success("Landlord deleted successfully")
        );
    }

    // Other endpoints like activate/deactivate can also be secured similarly
}