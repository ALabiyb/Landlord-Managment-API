package com.tz.rental.landlord_management.api;

import com.tz.rental.landlord_management.application.dto.CreateLeaseRequest;
import com.tz.rental.landlord_management.application.dto.LeaseResponse;
import com.tz.rental.landlord_management.application.dto.UpdateLeaseRequest;
import com.tz.rental.landlord_management.application.service.JpaLeaseService;
import com.tz.rental.landlord_management.domain.model.valueobject.LeaseStatus;
import com.tz.rental.landlord_management.infrastructure.persistence.entity.UserEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * REST Controller for managing lease agreements.
 * Provides full CRUD operations with comprehensive validation and
 * landlord-scoped access.
 */
@RestController
@RequestMapping("/api/v1/leases")
@RequiredArgsConstructor
@Tag(name = "Lease Management", description = "APIs for managing lease agreements between landlords and tenants")
public class LeaseController {

        private final JpaLeaseService leaseService;

        @PostMapping
        @Operation(summary = "Create a new lease", description = "Creates a new lease agreement between a tenant and a room. "
                        +
                        "Validates room availability, tenant status, and prevents overlapping leases.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "Lease created successfully", content = @Content(schema = @Schema(implementation = LeaseResponse.class))),
                        @ApiResponse(responseCode = "400", description = "Invalid request data or business rule violation"),
                        @ApiResponse(responseCode = "404", description = "Tenant or room not found"),
                        @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required")
        })
        public ResponseEntity<LeaseResponse> createLease(
                        @AuthenticationPrincipal UserEntity currentUser,
                        @Valid @RequestBody CreateLeaseRequest request) {

                UUID landlordId = currentUser.getLandlord().getId();
                LeaseResponse response = leaseService.createLease(landlordId, request);
                return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }

        @GetMapping
        @Operation(summary = "Get all leases", description = "Retrieves all leases for the authenticated landlord with pagination support")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Leases retrieved successfully", content = @Content(schema = @Schema(implementation = Page.class))),
                        @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required")
        })
        public ResponseEntity<Page<LeaseResponse>> getAllLeases(
                        @AuthenticationPrincipal UserEntity currentUser,
                        @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

                UUID landlordId = currentUser.getLandlord().getId();
                Page<LeaseResponse> leases = leaseService.getAllLeases(landlordId, pageable);
                return ResponseEntity.ok(leases);
        }

        @GetMapping("/{id}")
        @Operation(summary = "Get lease by ID", description = "Retrieves detailed information about a specific lease")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Lease found", content = @Content(schema = @Schema(implementation = LeaseResponse.class))),
                        @ApiResponse(responseCode = "404", description = "Lease not found or access denied"),
                        @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required")
        })
        public ResponseEntity<LeaseResponse> getLease(
                        @AuthenticationPrincipal UserEntity currentUser,
                        @Parameter(description = "Lease ID", required = true) @PathVariable UUID id) {

                UUID landlordId = currentUser.getLandlord().getId();
                LeaseResponse lease = leaseService.getLease(landlordId, id);
                return ResponseEntity.ok(lease);
        }

        @PutMapping("/{id}")
        @Operation(summary = "Update a lease", description = "Updates an existing lease. Only provided fields will be updated.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Lease updated successfully", content = @Content(schema = @Schema(implementation = LeaseResponse.class))),
                        @ApiResponse(responseCode = "400", description = "Invalid update data"),
                        @ApiResponse(responseCode = "404", description = "Lease not found or access denied"),
                        @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required")
        })
        public ResponseEntity<LeaseResponse> updateLease(
                        @AuthenticationPrincipal UserEntity currentUser,
                        @Parameter(description = "Lease ID", required = true) @PathVariable UUID id,
                        @Valid @RequestBody UpdateLeaseRequest request) {

                UUID landlordId = currentUser.getLandlord().getId();
                LeaseResponse response = leaseService.updateLease(landlordId, id, request);
                return ResponseEntity.ok(response);
        }

        @DeleteMapping("/{id}")
        @Operation(summary = "Delete a lease", description = "Deletes a lease. Only PENDING leases can be deleted. Active leases must be terminated first.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "204", description = "Lease deleted successfully"),
                        @ApiResponse(responseCode = "400", description = "Cannot delete active lease"),
                        @ApiResponse(responseCode = "404", description = "Lease not found or access denied"),
                        @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required")
        })
        public ResponseEntity<Void> deleteLease(
                        @AuthenticationPrincipal UserEntity currentUser,
                        @Parameter(description = "Lease ID", required = true) @PathVariable UUID id) {

                UUID landlordId = currentUser.getLandlord().getId();
                leaseService.deleteLease(landlordId, id);
                return ResponseEntity.noContent().build();
        }

        @PostMapping("/{id}/terminate")
        @Operation(summary = "Terminate a lease early", description = "Terminates an active lease before its end date. Updates room status to VACANT.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Lease terminated successfully", content = @Content(schema = @Schema(implementation = LeaseResponse.class))),
                        @ApiResponse(responseCode = "400", description = "Invalid termination date"),
                        @ApiResponse(responseCode = "404", description = "Lease not found or access denied"),
                        @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required")
        })
        public ResponseEntity<LeaseResponse> terminateLease(
                        @AuthenticationPrincipal UserEntity currentUser,
                        @Parameter(description = "Lease ID", required = true) @PathVariable UUID id,
                        @Parameter(description = "Termination date", required = true) @RequestParam LocalDate terminationDate) {

                UUID landlordId = currentUser.getLandlord().getId();
                LeaseResponse response = leaseService.terminateLease(landlordId, id, terminationDate);
                return ResponseEntity.ok(response);
        }

        @GetMapping("/status/{status}")
        @Operation(summary = "Get leases by status", description = "Retrieves all leases with a specific status (ACTIVE, PENDING, EXPIRED, TERMINATED)")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Leases retrieved successfully", content = @Content(schema = @Schema(implementation = Page.class))),
                        @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required")
        })
        public ResponseEntity<Page<LeaseResponse>> getLeasesByStatus(
                        @AuthenticationPrincipal UserEntity currentUser,
                        @Parameter(description = "Lease status", required = true) @PathVariable LeaseStatus status,
                        @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

                UUID landlordId = currentUser.getLandlord().getId();
                Page<LeaseResponse> leases = leaseService.getLeasesByStatus(landlordId, status, pageable);
                return ResponseEntity.ok(leases);
        }

        @GetMapping("/room/{roomId}")
        @Operation(summary = "Get leases by room", description = "Retrieves all leases (past and present) for a specific room")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Leases retrieved successfully", content = @Content(schema = @Schema(implementation = List.class))),
                        @ApiResponse(responseCode = "404", description = "Room not found or access denied"),
                        @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required")
        })
        public ResponseEntity<List<LeaseResponse>> getLeasesByRoom(
                        @AuthenticationPrincipal UserEntity currentUser,
                        @Parameter(description = "Room ID", required = true) @PathVariable UUID roomId) {

                UUID landlordId = currentUser.getLandlord().getId();
                List<LeaseResponse> leases = leaseService.getLeasesByRoom(landlordId, roomId);
                return ResponseEntity.ok(leases);
        }

        @GetMapping(value = "/{id}/contract", produces = org.springframework.http.MediaType.APPLICATION_PDF_VALUE)
        @Operation(summary = "Download lease contract", description = "Generates and downloads the lease contract as a PDF.")
        public ResponseEntity<byte[]> downloadContract(
                        @AuthenticationPrincipal UserEntity currentUser,
                        @PathVariable UUID id) {
                UUID landlordId = currentUser.getLandlord().getId();
                byte[] pdfBytes = leaseService.downloadContract(landlordId, id);

                org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
                headers.setContentType(org.springframework.http.MediaType.APPLICATION_PDF);
                headers.setContentDisposition(org.springframework.http.ContentDisposition.builder("attachment")
                                .filename("lease_contract_" + id + ".pdf").build());

                return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
        }

        @PostMapping("/{id}/contract/share")
        @Operation(summary = "Share lease contract", description = "Shares the lease contract via WhatsApp.")
        public com.tz.rental.landlord_management.api.rest.dto.ApiResponse<Void> shareContract(
                        @AuthenticationPrincipal UserEntity currentUser,
                        @PathVariable UUID id) {
                UUID landlordId = currentUser.getLandlord().getId();
                leaseService.shareContractViaWhatsApp(landlordId, id);
                return com.tz.rental.landlord_management.api.rest.dto.ApiResponse
                                .success("Contract shared successfully via WhatsApp");
        }
}
