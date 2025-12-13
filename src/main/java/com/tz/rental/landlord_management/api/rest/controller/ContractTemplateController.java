package com.tz.rental.landlord_management.api.rest.controller;

import com.tz.rental.landlord_management.api.rest.dto.ApiResponse;
import com.tz.rental.landlord_management.application.dto.ContractTemplateResponse;
import com.tz.rental.landlord_management.application.dto.CreateContractTemplateRequest;
import com.tz.rental.landlord_management.application.service.ContractTemplateService;
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
@RequestMapping("/api/v1/contract-templates")
@RequiredArgsConstructor
@Tag(name = "Contract Templates", description = "Endpoints for managing rental contract templates")
public class ContractTemplateController {

    private final ContractTemplateService contractTemplateService;

    @PostMapping
    @Operation(summary = "Create a new contract template", description = "Uploads a new contract template to be used for lease agreements.")
    public ResponseEntity<ApiResponse<ContractTemplateResponse>> createTemplate(@Valid @RequestBody CreateContractTemplateRequest request) {
        ContractTemplateResponse response = contractTemplateService.createTemplate(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Contract template created successfully", response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get contract template by ID", description = "Retrieves a specific contract template by its ID.")
    public ResponseEntity<ApiResponse<ContractTemplateResponse>> getTemplate(@PathVariable UUID id) {
        ContractTemplateResponse response = contractTemplateService.getTemplate(id);
        return ResponseEntity.ok(ApiResponse.success("Contract template retrieved successfully", response));
    }

    @GetMapping
    @Operation(summary = "Get all contract templates", description = "Retrieves a list of all available contract templates.")
    public ResponseEntity<ApiResponse<List<ContractTemplateResponse>>> getAllTemplates() {
        List<ContractTemplateResponse> response = contractTemplateService.getAllTemplates();
        return ResponseEntity.ok(ApiResponse.success("Contract templates retrieved successfully", response));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update contract template", description = "Updates the details of an existing contract template.")
    public ResponseEntity<ApiResponse<ContractTemplateResponse>> updateTemplate(@PathVariable UUID id, @Valid @RequestBody CreateContractTemplateRequest request) {
        ContractTemplateResponse response = contractTemplateService.updateTemplate(id, request);
        return ResponseEntity.ok(ApiResponse.success("Contract template updated successfully", response));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete contract template", description = "Deletes a contract template by its ID.")
    public ResponseEntity<ApiResponse<Void>> deleteTemplate(@PathVariable UUID id) {
        contractTemplateService.deleteTemplate(id);
        return ResponseEntity.ok(ApiResponse.success("Contract template deleted successfully"));
    }

    @PutMapping("/{id}/activate")
    @Operation(summary = "Activate contract template", description = "Activates a contract template, making it available for use.")
    public ResponseEntity<ApiResponse<ContractTemplateResponse>> activateTemplate(@PathVariable UUID id) {
        ContractTemplateResponse response = contractTemplateService.activateTemplate(id);
        return ResponseEntity.ok(ApiResponse.success("Contract template activated successfully", response));
    }

    @PutMapping("/{id}/deactivate")
    @Operation(summary = "Deactivate contract template", description = "Deactivates a contract template, making it unavailable for use.")
    public ResponseEntity<ApiResponse<ContractTemplateResponse>> deactivateTemplate(@PathVariable UUID id) {
        ContractTemplateResponse response = contractTemplateService.deactivateTemplate(id);
        return ResponseEntity.ok(ApiResponse.success("Contract template deactivated successfully", response));
    }
}