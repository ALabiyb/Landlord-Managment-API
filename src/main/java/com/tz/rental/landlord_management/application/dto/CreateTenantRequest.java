package com.tz.rental.landlord_management.application.dto;

import com.tz.rental.landlord_management.domain.model.valueobject.PaymentPeriod;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to create a new tenant")
public class CreateTenantRequest {

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^(\\+255|0)\\d{9}$", message = "Phone number must be +255XXXXXXXXX or 0XXXXXXXXX")
    private String phoneNumber;

    @NotBlank(message = "National ID is required")
    private String nationalId;

    private String emergencyContactName;

    @Pattern(regexp = "^(\\+255|0)[0-9]{9}$", message = "Phone number must be +255XXXXXXXXX or 0XXXXXXXXX")
    private String emergencyContactPhone;

    @NotNull(message = "Room ID is required")
    private UUID roomId;

    @NotNull(message = "Start date is required")
    @FutureOrPresent(message = "Start date must be in the present or future")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    @Future(message = "End date must be in the future")
    private LocalDate endDate;

    @NotNull(message = "Rent amount is required")
    @Positive(message = "Rent amount must be positive")
    private BigDecimal rentAmount;

    @NotNull(message = "Payment period is required")
    private PaymentPeriod paymentPeriod;

    private String contractDocumentUrl;

    @jakarta.validation.constraints.AssertTrue(message = "End date must be after start date")
    public boolean isEndDateAfterStartDate() {
        if (startDate == null || endDate == null)
            return true;
        return endDate.isAfter(startDate);
    }
}