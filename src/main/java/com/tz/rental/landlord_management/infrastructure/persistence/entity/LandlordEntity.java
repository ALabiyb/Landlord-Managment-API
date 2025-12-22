package com.tz.rental.landlord_management.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "landlords")
@Getter
@Setter
public class LandlordEntity {

    @Id
    private UUID id;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "phone_number", nullable = false, unique = true)
    private String phoneNumber;

    @Column(name = "national_id", unique = true)
    private String nationalId;

    @Column(name = "tax_id")
    private String taxId;

    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    @OneToOne(mappedBy = "landlord", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private UserEntity user;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Default constructor for JPA
    public LandlordEntity() {}

    // Custom constructor
    public LandlordEntity(String firstName, String lastName,
                          String email, String phoneNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.isActive = true;
    }
}