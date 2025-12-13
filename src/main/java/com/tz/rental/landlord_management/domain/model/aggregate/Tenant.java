package com.tz.rental.landlord_management.domain.model.aggregate;

import com.tz.rental.landlord_management.domain.model.valueobject.Email;
import com.tz.rental.landlord_management.domain.model.valueobject.PhoneNumber;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class Tenant {

    private final TenantId id;
    private String firstName;
    private String lastName;
    private Email email;
    private PhoneNumber phoneNumber;
    private String nationalId;
    private String emergencyContactName;
    private PhoneNumber emergencyContactPhone;
    private boolean isActive;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public record TenantId(UUID value) {
        public TenantId {
            if (value == null) {
                throw new IllegalArgumentException("Tenant ID cannot be null");
            }
        }
    }

    private Tenant(TenantId id, String firstName, String lastName, Email email, PhoneNumber phoneNumber, String nationalId,
                   String emergencyContactName, PhoneNumber emergencyContactPhone, boolean isActive,
                   LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.nationalId = nationalId;
        this.emergencyContactName = emergencyContactName;
        this.emergencyContactPhone = emergencyContactPhone;
        this.isActive = isActive;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        validate();
    }

    // Factory method - main way to create new tenants
    public static Tenant create(String firstName, String lastName, String email, String phoneNumber, String nationalId) {
        return new Tenant(
                new TenantId(UUID.randomUUID()),
                firstName,
                lastName,
                new Email(email),
                new PhoneNumber(phoneNumber),
                nationalId,
                null, // emergencyContactName
                null, // emergencyContactPhone
                true, // isActive
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    // Factory method for existing tenants (from database)
    public static Tenant fromExisting(UUID id, String firstName, String lastName,
                                      String email, String phoneNumber, String nationalId,
                                      String emergencyContactName, String emergencyContactPhone,
                                      boolean isActive, LocalDateTime createdAt, LocalDateTime updatedAt) {
        return new Tenant(
                new TenantId(id),
                firstName,
                lastName,
                new Email(email),
                new PhoneNumber(phoneNumber),
                nationalId,
                emergencyContactName,
                (emergencyContactPhone != null && !emergencyContactPhone.isEmpty()) ? new PhoneNumber(emergencyContactPhone) : null,
                isActive,
                createdAt,
                updatedAt
        );
    }

    public void updateDetails(String firstName, String lastName, String email, String phoneNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = new Email(email);
        this.phoneNumber = new PhoneNumber(phoneNumber);
        this.updatedAt = LocalDateTime.now();
        validate();
    }

    public void addEmergencyContact(String name, String phone) {
        this.emergencyContactName = name;
        this.emergencyContactPhone = new PhoneNumber(phone);
        this.updatedAt = LocalDateTime.now();
    }

    public void deactivate() {
        if (!this.isActive) {
            throw new IllegalStateException("Tenant is already inactive");
        }
        this.isActive = false;
        this.updatedAt = LocalDateTime.now();
    }

    public void activate() {
        if (this.isActive) {
            throw new IllegalStateException("Tenant is already active");
        }
        this.isActive = true;
        this.updatedAt = LocalDateTime.now();
    }

    private void validate() {
        if (firstName == null || firstName.trim().isEmpty()) {
            throw new IllegalArgumentException("First name is required");
        }
        if (lastName == null || lastName.trim().isEmpty()) {
            throw new IllegalArgumentException("Last name is required");
        }
        if (nationalId == null || nationalId.trim().isEmpty()) {
            throw new IllegalArgumentException("National ID is required");
        }
    }
}