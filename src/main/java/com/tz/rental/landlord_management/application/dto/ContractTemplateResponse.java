package com.tz.rental.landlord_management.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Schema(description = "Response containing contract template details")
public class ContractTemplateResponse {

    @Schema(description = "ID of the contract template")
    private UUID id;

    @Schema(description = "Unique name of the template")
    private String name;

    @Schema(description = "The content of the template")
    private String content;

    @Schema(description = "Description of the template")
    private String description;

    @Schema(description = "Whether the template is active and available for use")
    private boolean isActive;

    @Schema(description = "Date and time the template was created")
    private LocalDateTime createdAt;

    @Schema(description = "Date and time the template was last updated")
    private LocalDateTime updatedAt;
}