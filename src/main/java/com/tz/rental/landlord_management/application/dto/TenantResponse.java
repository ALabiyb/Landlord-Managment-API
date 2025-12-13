package com.tz.rental.landlord_management.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Schema(description = "Response containing tenant details")
public class TenantResponse {

    @Schema(description = "ID of the tenant")
    private UUID id;

    @Schema(description = "First name of the tenant")
    private String firstName;

    @Schema(description = "Last name of the tenant")
    private String lastName;

    @Schema(description = "Email address of the tenant")
    private String email;

    @Schema(description = "Phone number of the tenant")
    private String phoneNumber;

    @Schema(description = "National ID of the tenant")
    private String nationalId;

    @Schema(description = "Name of the emergency contact")
    private String emergencyContactName;

    @Schema(description = "Phone number of the emergency contact")
    private String emergencyContactPhone;

    @Schema(description = "Whether the tenant is active")
    private boolean isActive;

    @Schema(description = "Date and time the tenant was created")
    private LocalDateTime createdAt;

    @Schema(description = "Date and time the tenant was last updated")
    private LocalDateTime updatedAt;
}