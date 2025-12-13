package com.tz.rental.landlord_management.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
@Schema(description = "Request to create a new tenant")
public class CreateTenantRequest {

    @NotBlank(message = "First name is required")
    @Schema(description = "First name of the tenant", example = "Jane")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Schema(description = "Last name of the tenant", example = "Smith")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    @Schema(description = "Email address of the tenant", example = "jane.smith@example.com")
    private String email;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^\\+255[0-9]{9}$", message = "Phone number must be in format +255XXXXXXXXX")
    @Schema(description = "Phone number of the tenant", example = "+255787654321")
    private String phoneNumber;

    @NotBlank(message = "National ID is required")
    @Schema(description = "National ID of the tenant", example = "19900101-12345-1")
    private String nationalId;

    @Schema(description = "Name of the emergency contact", example = "John Smith")
    private String emergencyContactName;

    @Pattern(regexp = "^\\+255[0-9]{9}$", message = "Phone number must be in format +255XXXXXXXXX")
    @Schema(description = "Phone number of the emergency contact", example = "+255712345678")
    private String emergencyContactPhone;
}