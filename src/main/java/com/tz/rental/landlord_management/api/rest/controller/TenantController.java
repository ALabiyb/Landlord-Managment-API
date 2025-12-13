package com.tz.rental.landlord_management.api.rest.controller;

import com.tz.rental.landlord_management.api.rest.dto.ApiResponse;
import com.tz.rental.landlord_management.application.dto.CreateTenantRequest;
import com.tz.rental.landlord_management.application.dto.TenantResponse;
import com.tz.rental.landlord_management.application.service.TenantService;
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
@RequestMapping("/api/v1/tenants")
@RequiredArgsConstructor
@Tag(name = "Tenants", description = "Tenant management endpoints")
public class TenantController {

    private final TenantService tenantService;

    @PostMapping
    @Operation(summary = "Create a new tenant", description = "Register a new tenant in the system.")
    public ResponseEntity<ApiResponse<TenantResponse>> createTenant(@Valid @RequestBody CreateTenantRequest request) {
        TenantResponse response = tenantService.createTenant(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Tenant created successfully", response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get tenant by ID", description = "Retrieve detailed information for a specific tenant.")
    public ResponseEntity<ApiResponse<TenantResponse>> getTenant(@PathVariable UUID id) {
        TenantResponse response = tenantService.getTenant(id);
        return ResponseEntity.ok(ApiResponse.success("Tenant retrieved successfully", response));
    }

    @GetMapping
    @Operation(summary = "Get all tenants", description = "Retrieve a list of all tenants in the system.")
    public ResponseEntity<ApiResponse<List<TenantResponse>>> getAllTenants() {
        List<TenantResponse> response = tenantService.getAllTenants();
        return ResponseEntity.ok(ApiResponse.success("Tenants retrieved successfully", response));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update tenant details", description = "Update the information for a specific tenant.")
    public ResponseEntity<ApiResponse<TenantResponse>> updateTenant(@PathVariable UUID id, @Valid @RequestBody CreateTenantRequest request) {
        TenantResponse response = tenantService.updateTenant(id, request);
        return ResponseEntity.ok(ApiResponse.success("Tenant updated successfully", response));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deactivate a tenant", description = "Deactivate a tenant's profile (soft delete).")
    public ResponseEntity<ApiResponse<Void>> deleteTenant(@PathVariable UUID id) {
        tenantService.deleteTenant(id);
        return ResponseEntity.ok(ApiResponse.success("Tenant deactivated successfully"));
    }
}