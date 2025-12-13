package com.tz.rental.landlord_management.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "houses")
@Getter
@Setter
public class HouseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "property_code", nullable = false, unique = true)
    private String propertyCode;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "house_type", nullable = false)
    private String houseType; // APARTMENT, STANDALONE, COMPLEX

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "landlord_id", nullable = false)
    private LandlordEntity landlord;

    @Column(name = "street_address", nullable = false)
    private String streetAddress;

    @Column(name = "district", nullable = false)
    private String district;

    @Column(name = "region", nullable = false)
    private String region;

    @Column(name = "country", nullable = false)
    private String country = "Tanzania";

    @Column(name = "total_floors")
    private Integer totalFloors = 1;

    @Column(name = "year_built")
    private Integer yearBuilt;

    @Column(name = "has_parking")
    private Boolean hasParking = false;

    @Column(name = "has_security")
    private Boolean hasSecurity = false;

    @Column(name = "monthly_common_charges")
    private BigDecimal monthlyCommonCharges = BigDecimal.ZERO;

    @Column(name = "status", nullable = false)
    private String status = "ACTIVE"; // ACTIVE, MAINTENANCE, VACANT

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Default constructor
    public HouseEntity() {}

    // Manual getters/setters if Lombok fails
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getPropertyCode() { return propertyCode; }
    public void setPropertyCode(String propertyCode) { this.propertyCode = propertyCode; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getHouseType() { return houseType; }
    public void setHouseType(String houseType) { this.houseType = houseType; }
    public LandlordEntity getLandlord() { return landlord; }
    public void setLandlord(LandlordEntity landlord) { this.landlord = landlord; }
    public String getStreetAddress() { return streetAddress; }
    public void setStreetAddress(String streetAddress) { this.streetAddress = streetAddress; }
    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }
    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }
    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }
    public Integer getTotalFloors() { return totalFloors; }
    public void setTotalFloors(Integer totalFloors) { this.totalFloors = totalFloors; }
    public Integer getYearBuilt() { return yearBuilt; }
    public void setYearBuilt(Integer yearBuilt) { this.yearBuilt = yearBuilt; }
    public Boolean getHasParking() { return hasParking; }
    public void setHasParking(Boolean hasParking) { this.hasParking = hasParking; }
    public Boolean getHasSecurity() { return hasSecurity; }
    public void setHasSecurity(Boolean hasSecurity) { this.hasSecurity = hasSecurity; }
    public BigDecimal getMonthlyCommonCharges() { return monthlyCommonCharges; }
    public void setMonthlyCommonCharges(BigDecimal monthlyCommonCharges) { this.monthlyCommonCharges = monthlyCommonCharges; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}