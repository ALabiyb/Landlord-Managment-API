package com.tz.rental.landlord_management.api.rest.controller;

import com.tz.rental.landlord_management.api.rest.dto.ApiResponse;
import com.tz.rental.landlord_management.application.dto.CreateTenantRequest;
import com.tz.rental.landlord_management.application.dto.TenantResponse;
import com.tz.rental.landlord_management.application.service.JpaTenantService;
import com.tz.rental.landlord_management.infrastructure.persistence.entity.UserEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/tenants")
@RequiredArgsConstructor
@Tag(name = "Tenants", description = "Tenant management endpoints")
public class TenantController {

    private final JpaTenantService tenantService;

    @PostMapping
    @Operation(summary = "Create a new tenant", description = "Register a new tenant in the system.")
    public ResponseEntity<ApiResponse<TenantResponse>> createTenant(
            @AuthenticationPrincipal UserEntity currentUser,
            @Valid @RequestBody CreateTenantRequest request) {
        UUID landlordId = currentUser.getLandlord().getId();
        TenantResponse response = tenantService.createTenant(landlordId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Tenant created successfully", response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get tenant by ID", description = "Retrieve detailed information for a specific tenant.")
    public ResponseEntity<ApiResponse<TenantResponse>> getTenant(
            @AuthenticationPrincipal UserEntity currentUser,
            @PathVariable UUID id) {
        UUID landlordId = currentUser.getLandlord().getId();
        TenantResponse response = tenantService.getTenant(landlordId, id);
        return ResponseEntity.ok(ApiResponse.success("Tenant retrieved successfully", response));
    }

    @GetMapping
    @Operation(summary = "Get all tenants", description = "Retrieve a list of all tenants for the current landlord.")
    public ResponseEntity<ApiResponse<List<TenantResponse>>> getAllTenants(
            @AuthenticationPrincipal UserEntity currentUser) {
        UUID landlordId = currentUser.getLandlord().getId();
        List<TenantResponse> response = tenantService.getAllTenants(landlordId);
        return ResponseEntity.ok(ApiResponse.success("Tenants retrieved successfully", response));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update tenant details", description = "Update the information for a specific tenant.")
    public ResponseEntity<ApiResponse<TenantResponse>> updateTenant(
            @AuthenticationPrincipal UserEntity currentUser,
            @PathVariable UUID id,
            @Valid @RequestBody CreateTenantRequest request) {
        UUID landlordId = currentUser.getLandlord().getId();
        TenantResponse response = tenantService.updateTenant(landlordId, id, request);
        return ResponseEntity.ok(ApiResponse.success("Tenant updated successfully", response));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deactivate a tenant", description = "Deactivate a tenant's profile (soft delete).")
    public ResponseEntity<ApiResponse<Void>> deleteTenant(
            @AuthenticationPrincipal UserEntity currentUser,
            @PathVariable UUID id) {
        UUID landlordId = currentUser.getLandlord().getId();
        tenantService.deleteTenant(landlordId, id);
        return ResponseEntity.ok(ApiResponse.success("Tenant deactivated successfully"));
    }
}
