package com.tz.rental.landlord_management.domain.model.aggregate;

import com.tz.rental.landlord_management.domain.model.valueobject.Address;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
    private Boolean hasWater;
    private Boolean hasElectricity;
    private List<String> imageUrls;
    private BigDecimal monthlyCommonCharges;
    private HouseStatus status;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public record HouseId(UUID value) {
        public HouseId {
            if (value == null) {
                throw new IllegalArgumentException("House ID cannot be null");
            }
        }
    }

    public enum HouseType {
        APARTMENT, STANDALONE, COMPLEX
    }

    public enum HouseStatus {
        ACTIVE, INACTIVE, MAINTENANCE, VACANT, OCCUPIED
    }

    // Private constructor to be used by factory methods and builder
    private House(HouseId id, Landlord.LandlordId landlordId, LocalDateTime createdAt) {
        this.id = id;
        this.landlordId = landlordId;
        this.createdAt = createdAt;
        this.updatedAt = createdAt; // Initially same as createdAt
        this.imageUrls = new ArrayList<>();
        this.status = HouseStatus.ACTIVE;
    }

    // Factory method for creating new houses
    public static House create(String propertyCode, String name, HouseType houseType, Landlord.LandlordId landlordId, Address address) {
        House house = new House(new HouseId(UUID.randomUUID()), landlordId, LocalDateTime.now());
        house.propertyCode = propertyCode;
        house.name = name;
        house.houseType = houseType;
        house.address = address;
        house.validate();
        return house;
    }

    // Builder for reconstructing existing houses
    public static class HouseBuilder {
        private final House house;

        public HouseBuilder(UUID id, UUID landlordId, LocalDateTime createdAt) {
            this.house = new House(new HouseId(id), new Landlord.LandlordId(landlordId), createdAt);
        }

        public HouseBuilder propertyCode(String propertyCode) {
            house.propertyCode = propertyCode;
            return this;
        }

        public HouseBuilder name(String name) {
            house.name = name;
            return this;
        }

        public HouseBuilder description(String description) {
            house.description = description;
            return this;
        }

        public HouseBuilder houseType(HouseType houseType) {
            house.houseType = houseType;
            return this;
        }

        public HouseBuilder address(Address address) {
            house.address = address;
            return this;
        }

        public HouseBuilder totalFloors(Integer totalFloors) {
            house.totalFloors = totalFloors;
            return this;
        }

        public HouseBuilder yearBuilt(Integer yearBuilt) {
            house.yearBuilt = yearBuilt;
            return this;
        }

        public HouseBuilder amenities(Boolean hasParking, Boolean hasSecurity, Boolean hasWater, Boolean hasElectricity) {
            house.hasParking = hasParking;
            house.hasSecurity = hasSecurity;
            house.hasWater = hasWater;
            house.hasElectricity = hasElectricity;
            return this;
        }

        public HouseBuilder imageUrls(List<String> imageUrls) {
            house.imageUrls = imageUrls != null ? imageUrls : new ArrayList<>();
            return this;
        }

        public HouseBuilder monthlyCommonCharges(BigDecimal monthlyCommonCharges) {
            house.monthlyCommonCharges = monthlyCommonCharges;
            return this;
        }

        public HouseBuilder status(HouseStatus status) {
            house.status = status;
            return this;
        }

        public HouseBuilder updatedAt(LocalDateTime updatedAt) {
            house.updatedAt = updatedAt;
            return this;
        }

        public House build() {
            house.validate();
            return house;
        }
    }

    public void updateInformation(String name, String description, HouseType houseType) {
        this.name = name;
        this.description = description;
        this.houseType = houseType;
        this.updatedAt = LocalDateTime.now();
        validate();
    }

    public void updateAddress(Address address) {
        this.address = address;
        this.updatedAt = LocalDateTime.now();
    }

    public void updateAmenities(Integer totalFloors, Integer yearBuilt, Boolean hasParking, Boolean hasSecurity, Boolean hasWater, Boolean hasElectricity) {
        this.totalFloors = totalFloors;
        this.yearBuilt = yearBuilt;
        this.hasParking = hasParking;
        this.hasSecurity = hasSecurity;
        this.hasWater = hasWater;
        this.hasElectricity = hasElectricity;
        this.updatedAt = LocalDateTime.now();
    }

    public void addImageUrl(String url) {
        if (this.imageUrls == null) {
            this.imageUrls = new ArrayList<>();
        }
        this.imageUrls.add(url);
        this.updatedAt = LocalDateTime.now();
    }

    public void updateCharges(BigDecimal monthlyCommonCharges) {
        this.monthlyCommonCharges = monthlyCommonCharges;
        this.updatedAt = LocalDateTime.now();
    }

    public void markForMaintenance() {
        this.status = HouseStatus.MAINTENANCE;
        this.updatedAt = LocalDateTime.now();
    }

    public void markAsActive() {
        this.status = HouseStatus.ACTIVE;
        this.updatedAt = LocalDateTime.now();
    }

    public void markAsVacant() {
        this.status = HouseStatus.VACANT;
        this.updatedAt = LocalDateTime.now();
    }

    private void validate() {
        if (propertyCode == null || propertyCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Property code is required.");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("House name is required.");
        }
        if (houseType == null) {
            throw new IllegalArgumentException("House type is required.");
        }
        if (landlordId == null) {
            throw new IllegalArgumentException("Landlord ID is required.");
        }
        if (address == null) {
            throw new IllegalArgumentException("Address is required.");
        }
    }
}