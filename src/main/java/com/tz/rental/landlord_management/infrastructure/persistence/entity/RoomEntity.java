package com.tz.rental.landlord_management.infrastructure.persistence.entity;

import com.tz.rental.landlord_management.domain.model.valueobject.RoomStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "rooms")
@Getter
@Setter
public class RoomEntity {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "house_id", nullable = false)
    private HouseEntity house;

    @Column(name = "room_number", nullable = false)
    private String roomNumber;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "monthly_rent", nullable = false)
    private BigDecimal monthlyRent;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoomStatus status;

    @Column(name = "size")
    private String size; // e.g., "15x15", "20 sqm"

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "room_image_urls", joinColumns = @JoinColumn(name = "room_id"))
    @Column(name = "image_url")
    private List<String> imageUrls;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}