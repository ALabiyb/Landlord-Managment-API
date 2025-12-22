package com.tz.rental.landlord_management.infrastructure.persistence.entity;

import com.tz.rental.landlord_management.domain.model.valueobject.HouseType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "houses")
@Getter
@Setter
public class HouseEntity {

    @Id
    private UUID id;

    @Column(name = "property_code", nullable = false, unique = true)
    private String propertyCode;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "house_type", nullable = false)
    private HouseType houseType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "landlord_id", nullable = false)
    private LandlordEntity landlord;

    @OneToMany(mappedBy = "house", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<RoomEntity> rooms = new ArrayList<>();

    @Column(name = "street_address")
    private String streetAddress;
    private String district;
    private String region;
    private String country;

    @Column(name = "total_floors")
    private Integer totalFloors;

    @Column(name = "year_built")
    private Integer yearBuilt;

    @Column(name = "has_parking")
    private Boolean hasParking;

    @Column(name = "has_security")
    private Boolean hasSecurity;

    @Column(name = "has_water")
    private Boolean hasWater;

    @Column(name = "has_electricity")
    private Boolean hasElectricity;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "house_image_urls", joinColumns = @JoinColumn(name = "house_id"))
    @Column(name = "image_url")
    private List<String> imageUrls;

    @Column(name = "monthly_common_charges")
    private BigDecimal monthlyCommonCharges;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}