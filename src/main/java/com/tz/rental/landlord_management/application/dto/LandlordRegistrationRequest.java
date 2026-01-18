package com.tz.rental.landlord_management.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Request to register a new landlord user and profile")
public class LandlordRegistrationRequest {

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    @Schema(description = "The desired username for the account", example = "johndoe")
    private String username;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters long")
    @Schema(description = "The password for the account", example = "pass123")
    private String password;

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
    @Pattern(regexp = "^(\\+255|0)\\d{9}$", message = "Phone number must be in format +255XXXXXXXXX or 0XXXXXXXXX")
    @Schema(description = "Phone number of the landlord", example = "+255712345678")
    private String phoneNumber;

    @Schema(description = "National ID of the landlord (Optional)", example = "19900101123456789012")
    private String nationalId;
}