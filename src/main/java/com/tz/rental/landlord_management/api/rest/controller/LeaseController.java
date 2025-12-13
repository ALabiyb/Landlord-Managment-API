package com.tz.rental.landlord_management.api.rest.controller;

import com.tz.rental.landlord_management.api.rest.dto.ApiResponse;
import com.tz.rental.landlord_management.application.dto.CreateLeaseRequest;
import com.tz.rental.landlord_management.application.dto.LeaseResponse;
import com.tz.rental.landlord_management.application.service.LeaseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/leases")
@RequiredArgsConstructor
@Tag(name = "Leases", description = "Lease management endpoints")
public class LeaseController {

    private final LeaseService leaseService;

    @PostMapping
    @Operation(summary = "Create a new lease", description = "Assign a tenant to a room by creating a lease agreement.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Lease created and room assigned successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input data"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Tenant or Room not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Room is not vacant")
    })
    public ResponseEntity<ApiResponse<LeaseResponse>> createLease(@Valid @RequestBody CreateLeaseRequest request) {
        LeaseResponse response = leaseService.createLease(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Lease created and room assigned successfully", response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get lease by ID", description = "Retrieve details of a specific lease agreement.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lease retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Lease not found")
    })
    public ResponseEntity<ApiResponse<LeaseResponse>> getLease(@PathVariable UUID id) {
        LeaseResponse response = leaseService.getLease(id);
        return ResponseEntity.ok(ApiResponse.success("Lease retrieved successfully", response));
    }

    @PostMapping("/{id}/generate-contract")
    @Operation(summary = "Generate PDF contract",
            description = "Generates a PDF rental agreement for the specified lease using an optional template and attaches its URL to the lease record.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Contract generated and attached successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = LeaseResponse.class),
                            examples = @ExampleObject(value = "{\"success\":true,\"message\":\"Contract generated and attached successfully\",\"data\":{\"id\":\"123e4567-e89b-12d3-a456-426614174000\",\"tenantId\":\"a1b2c3d4-e5f6-7890-1234-567890abcdef\",\"roomId\":\"f0e9d8c7-b6a5-4321-fedc-ba9876543210\",\"startDate\":\"2025-01-01\",\"endDate\":\"2026-01-01\",\"rentAmount\":500000.00,\"paymentPeriod\":\"MONTHLY\",\"status\":\"ACTIVE\",\"contractDocumentUrl\":\"contracts/lease-123e4567-e89b-12d3-a456-426614174000.pdf\",\"createdAt\":\"2024-12-09T12:00:00\"}}"))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Lease, Tenant or Template not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error generating or saving PDF")
    })
    public ResponseEntity<ApiResponse<LeaseResponse>> generateContract(
            @PathVariable UUID id,
            @Parameter(description = "Optional ID of the contract template to use. If not provided, a default simple contract will be generated.", example = "a1b2c3d4-e5f6-7890-1234-567890abcdef")
            @RequestParam(required = false) UUID templateId) {
        LeaseResponse response = leaseService.generateContract(id, templateId);
        return ResponseEntity.ok(ApiResponse.success("Contract generated and attached successfully", response));
    }

    @PostMapping("/{id}/share-contract/whatsapp")
    @Operation(summary = "Share contract via WhatsApp",
            description = "Sends the generated contract document URL to the tenant via WhatsApp.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Contract shared successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Lease or Tenant not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "No contract document URL found for this lease. Generate the contract first.")
    })
    public ResponseEntity<ApiResponse<LeaseResponse>> shareContractViaWhatsApp(@PathVariable UUID id) {
        LeaseResponse response = leaseService.shareContractViaWhatsApp(id);
        return ResponseEntity.ok(ApiResponse.success("Contract shared successfully via WhatsApp", response));
    }
}