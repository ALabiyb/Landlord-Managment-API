package com.tz.rental.landlord_management.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Request to create or update a contract template")
public class CreateContractTemplateRequest {

    @NotBlank(message = "Template name is required")
    @Schema(description = "Unique name for the contract template", example = "Standard Lease Agreement")
    private String name;

    @NotBlank(message = "Template content is required")
    @Schema(description = "The actual content of the template, with placeholders (e.g., {{tenantName}}, {{rentAmount}})",
            example = "This Lease Agreement is made between {{landlordName}} and {{tenantName}} for the property at {{propertyAddress}}...")
    private String content;

    @Schema(description = "A brief description of the template's purpose", example = "Standard template for 12-month residential leases.")
    private String description;
}