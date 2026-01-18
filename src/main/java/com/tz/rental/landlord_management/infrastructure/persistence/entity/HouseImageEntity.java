package com.tz.rental.landlord_management.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "house_images")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HouseImageEntity {
    @Id
    @Column(columnDefinition = "uuid")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "house_id", nullable = false)
    private HouseEntity house;

    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    private String fileType;

    private Long fileSize;

    @Column(nullable = false)
    private String storagePath;

    private String thumbnailPath;

    private String caption;

    @Column(nullable = false)
    private boolean isPrimary;

    @Column(nullable = false)
    private int displayOrder;

    @Column(nullable = false)
    private LocalDateTime uploadedAt;

    @PrePersist
    protected void onCreate() {
        if (id == null) {
            id = UUID.randomUUID();
        }
        if (uploadedAt == null) {
            uploadedAt = LocalDateTime.now();
        }
    }
}
