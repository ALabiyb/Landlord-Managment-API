package com.tz.rental.landlord_management.infrastructure.persistence.entity;

import com.tz.rental.landlord_management.domain.model.valueobject.ActivityType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entity for storing activity logs.
 */
@Entity
@Table(name = "activity_logs")
@Getter
@Setter
public class ActivityLogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "landlord_id", nullable = true) // Can be null for system events or pre-login
    private LandlordEntity landlord;

    @Enumerated(EnumType.STRING)
    @Column(name = "activity_type", nullable = false)
    private ActivityType activityType;

    @Column(nullable = false)
    private String description;

    @Column(name = "entity_id")
    private UUID entityId; // ID of the related entity (Lease, Payment, etc.)

    @Column(name = "entity_type")
    private String entityType; // Class name or type string

    @Column(name = "ip_address")
    private String ipAddress;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
