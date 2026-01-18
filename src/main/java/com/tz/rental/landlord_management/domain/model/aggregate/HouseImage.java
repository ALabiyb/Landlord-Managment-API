package com.tz.rental.landlord_management.domain.model.aggregate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HouseImage {
    private UUID id;
    private UUID houseId;
    private String fileName;
    private String fileType;
    private Long fileSize;
    private String storagePath;
    private String thumbnailPath;
    private String caption;
    private boolean isPrimary;
    private int displayOrder;
    private LocalDateTime uploadedAt;
}
