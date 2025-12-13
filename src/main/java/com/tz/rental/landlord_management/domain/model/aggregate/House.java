package com.tz.rental.landlord_management.domain.model.aggregate;

import com.tz.rental.landlord_management.common.constant.ApplicationConstants;
import com.tz.rental.landlord_management.domain.exception.ValidationException;
import com.tz.rental.landlord_management.domain.model.valueobject.Address;
import com.tz.rental.landlord_management.domain.model.valueobject.Money;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class House {
    private final HouseId id;
    private String propertyCode;
    private String name;
    private String description;
    private HouseType houseType;
    private final Landlord.LandlordId landlordId;
    private Address address;
    private Integer totalFloors;
    private Integer yearBuilt;
    private Boolean hasParking;
    private Boolean hasSecurity;
    private Money monthlyCommonCharges;
    private HouseStatus status;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Value object for ID
    public record HouseId(UUID value) {
        public HouseId {
            if (value == null) {
                throw new IllegalArgumentException("House ID cannot be null");
            }
        }
    }

    // Enums
    public enum HouseType {
        APARTMENT, STANDALONE, COMPLEX
    }

    public enum HouseStatus {
        ACTIVE, MAINTENANCE, VACANT
    }

    // Private constructor
    private House(HouseId id, String propertyCode, String name, HouseType houseType,
                  Landlord.LandlordId landlordId, Address address) {
        this.id = id;
        this.propertyCode = propertyCode;
        this.name = name;
        this.houseType = houseType;
        this.landlordId = landlordId;
        this.address = address;
        this.totalFloors = 1;
        this.hasParking = false;
        this.hasSecurity = false;
        this.monthlyCommonCharges = new Money(BigDecimal.ZERO);
        this.status = HouseStatus.ACTIVE;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        validate();
    }

    // Factory method for new house
    public static House create(String propertyCode, String name, HouseType houseType,
                               Landlord.LandlordId landlordId, Address address) {
        HouseId id = new HouseId(UUID.randomUUID());
        return new House(id, propertyCode, name, houseType, landlordId, address);
    }

    // Factory method for existing house
    public static House fromExisting(UUID id, String propertyCode, String name,
                                     String description, HouseType houseType,
                                     UUID landlordId, Address address,
                                     Integer totalFloors, Integer yearBuilt,
                                     Boolean hasParking, Boolean hasSecurity,
                                     BigDecimal monthlyCommonCharges,
                                     HouseStatus status,
                                     LocalDateTime createdAt, LocalDateTime updatedAt) {
        HouseId houseId = new HouseId(id);
        Landlord.LandlordId landlordIdVO = new Landlord.LandlordId(landlordId);

        House house = new House(houseId, propertyCode, name, houseType, landlordIdVO, address);
        house.description = description;
        house.totalFloors = totalFloors;
        house.yearBuilt = yearBuilt;
        house.hasParking = hasParking;
        house.hasSecurity = hasSecurity;
        house.monthlyCommonCharges = new Money(monthlyCommonCharges);
        house.status = status;

        // Set createdAt/updatedAt (remove final from fields or use reflection)
        try {
            java.lang.reflect.Field createdAtField = House.class.getDeclaredField("createdAt");
            createdAtField.setAccessible(true);
            createdAtField.set(house, createdAt);

            java.lang.reflect.Field updatedAtField = House.class.getDeclaredField("updatedAt");
            updatedAtField.setAccessible(true);
            updatedAtField.set(house, updatedAt);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create House from existing", e);
        }

        return house;
    }

    // Business methods
    public void updateInformation(String name, String description, HouseType houseType) {
        this.name = name;
        this.description = description;
        this.houseType = houseType;
        this.updatedAt = LocalDateTime.now();
        validate();
    }

    public void updateAddress(Address newAddress) {
        this.address = newAddress;
        this.updatedAt = LocalDateTime.now();
        validate();
    }

    public void updateAmenities(Integer totalFloors, Integer yearBuilt,
                                Boolean hasParking, Boolean hasSecurity) {
        this.totalFloors = totalFloors != null ? totalFloors : this.totalFloors;
        this.yearBuilt = yearBuilt;
        this.hasParking = hasParking != null ? hasParking : this.hasParking;
        this.hasSecurity = hasSecurity != null ? hasSecurity : this.hasSecurity;
        this.updatedAt = LocalDateTime.now();
        validate();
    }

    public void updateCharges(BigDecimal monthlyCommonCharges) {
        this.monthlyCommonCharges = new Money(monthlyCommonCharges);
        this.updatedAt = LocalDateTime.now();
        validate();
    }

    public void markForMaintenance() {
        if (this.status == HouseStatus.MAINTENANCE) {
            throw new IllegalStateException("House is already under maintenance");
        }
        this.status = HouseStatus.MAINTENANCE;
        this.updatedAt = LocalDateTime.now();
    }

    public void markAsActive() {
        if (this.status == HouseStatus.ACTIVE) {
            throw new IllegalStateException("House is already active");
        }
        this.status = HouseStatus.ACTIVE;
        this.updatedAt = LocalDateTime.now();
    }

    public void markAsVacant() {
        this.status = HouseStatus.VACANT;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isActive() {
        return status == HouseStatus.ACTIVE;
    }

    public boolean isUnderMaintenance() {
        return status == HouseStatus.MAINTENANCE;
    }

    public boolean isVacant() {
        return status == HouseStatus.VACANT;
    }

    public String getFullAddress() {
        return address != null ? address.getFullAddress() : "No address";
    }

    // Validation
    private void validate() {
        if (propertyCode == null || propertyCode.trim().isEmpty()) {
            throw new ValidationException("Property code is required");
        }
        if (propertyCode.length() > 50) {
            throw new ValidationException("Property code cannot exceed 50 characters");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new ValidationException("House name is required");
        }
        if (name.length() > 200) {
            throw new ValidationException("House name cannot exceed 200 characters");
        }
        if (houseType == null) {
            throw new ValidationException("House type is required");
        }
        if (landlordId == null) {
            throw new ValidationException("Landlord ID is required");
        }
        if (address == null) {
            throw new ValidationException("Address is required");
        }
        if (totalFloors != null && totalFloors < 1) {
            throw new ValidationException("Total floors must be at least 1");
        }
        if (yearBuilt != null && (yearBuilt < 1900 || yearBuilt > LocalDateTime.now().getYear())) {
            throw new ValidationException("Year built must be between 1900 and current year");
        }
    }

    @Override
    public String toString() {
        return String.format("House[id=%s, name=%s, type=%s, landlord=%s]",
                id.value(), name, houseType, landlordId.value());
    }
}