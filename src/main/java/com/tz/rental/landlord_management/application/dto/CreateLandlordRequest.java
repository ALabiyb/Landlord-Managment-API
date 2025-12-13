package com.tz.rental.landlord_management.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
@Schema(description = "Request to create a new landlord")
public class CreateLandlordRequest {

    @NotBlank(message = "First name is required")
    @Schema(description = "First name of the landlord", example = "John")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Schema(description = "Last name of the landlord", example = "Doe")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    @Schema(description = "Email address of the landlord", example = "john.doe@example.com")
    private String email;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^\\+255[0-9]{9}$",
            message = "Phone number must be in format +255XXXXXXXXX")
    @Schema(description = "Phone number of the landlord", example = "+255712345678")
    private String phoneNumber;

    @Schema(description = "National ID of the landlord", example = "1234567890123456")
    private String nationalId;

    @Schema(description = "Tax ID of the landlord", example = "123-456-789")
    private String taxId;
}