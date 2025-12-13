package com.tz.rental.landlord_management.domain.model.aggregate;

import com.tz.rental.landlord_management.domain.exception.ValidationException;
import com.tz.rental.landlord_management.domain.model.valueobject.Email;
import com.tz.rental.landlord_management.domain.model.valueobject.PhoneNumber;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class Landlord {
    private final LandlordId id;
    private String firstName;
    private String lastName;
    private Email email;
    private PhoneNumber phoneNumber;
    private String nationalId;
    private String taxId;
    private boolean isActive;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Value object for ID
    public record LandlordId(UUID value) {
        public LandlordId {
            if (value == null) {
                throw new IllegalArgumentException("Landlord ID cannot be null");
            }
        }
    }

    // Private constructor for internal use
    private Landlord(LandlordId id, String firstName, String lastName,
                     Email email, PhoneNumber phoneNumber, String nationalId, String taxId,
                     boolean isActive, LocalDateTime createdAt, LocalDateTime updatedAt) { // Updated constructor
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.nationalId = nationalId;
        this.taxId = taxId;
        this.isActive = isActive;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        validate();
    }

    // Factory method - main way to create landlords
    public static Landlord create(String firstName, String lastName,
                                  String email, String phoneNumber) {
        LandlordId id = new LandlordId(UUID.randomUUID());
        Email emailVO = new Email(email);
        PhoneNumber phoneVO = new PhoneNumber(phoneNumber);
        LocalDateTime now = LocalDateTime.now();

        return new Landlord(id, firstName, lastName, emailVO, phoneVO,
                null, null, true, now, now); // Pass null for nationalId/taxId, true for isActive, and current time
    }

    // Factory method for existing landlords (from database)
    public static Landlord fromExisting(UUID id, String firstName, String lastName,
                                        String email, String phoneNumber,
                                        String nationalId, String taxId,
                                        boolean isActive,
                                        LocalDateTime createdAt, LocalDateTime updatedAt) {
        LandlordId landlordId = new LandlordId(id);
        Email emailVO = new Email(email);
        PhoneNumber phoneVO = new PhoneNumber(phoneNumber);

        return new Landlord(landlordId, firstName, lastName, emailVO, phoneVO,
                nationalId, taxId, isActive, createdAt, updatedAt); // Use updated constructor
    }

    // Business methods
    public void updateContactInfo(String email, String phoneNumber) {
        this.email = new Email(email);
        this.phoneNumber = new PhoneNumber(phoneNumber);
        this.updatedAt = LocalDateTime.now();
        validate();
    }

    public void updatePersonalInfo(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.updatedAt = LocalDateTime.now();
        validate();
    }

    public void updateIdentity(String nationalId, String taxId) {
        this.nationalId = nationalId;
        this.taxId = taxId;
        this.updatedAt = LocalDateTime.now();
    }

    public void deactivate() {
        if (!this.isActive) {
            throw new IllegalStateException("Landlord is already inactive");
        }
        this.isActive = false;
        this.updatedAt = LocalDateTime.now();
    }

    public void activate() {
        if (this.isActive) {
            throw new IllegalStateException("Landlord is already active");
        }
        this.isActive = true;
        this.updatedAt = LocalDateTime.now();
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public boolean hasTaxId() {
        return taxId != null && !taxId.trim().isEmpty();
    }

    public boolean hasNationalId() {
        return nationalId != null && !nationalId.trim().isEmpty();
    }

    // Validation
    private void validate() {
        if (firstName == null || firstName.trim().isEmpty()) {
            throw new ValidationException("First name is required");
        }
        if (lastName == null || lastName.trim().isEmpty()) {
            throw new ValidationException("Last name is required");
        }
        if (firstName.length() < 2 || firstName.length() > 100) {
            throw new ValidationException("First name must be between 2 and 100 characters");
        }
        if (lastName.length() < 2 || lastName.length() > 100) {
            throw new ValidationException("Last name must be between 2 and 100 characters");
        }
    }

    @Override
    public String toString() {
        return String.format("Landlord[id=%s, name=%s %s, email=%s]",
                id.value(), firstName, lastName, email.getValue());
    }
}