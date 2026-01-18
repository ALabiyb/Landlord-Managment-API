package com.tz.rental.landlord_management.application.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class ImageUploadResponse {
    private UUID id;
    private String fileName;
    private Long fileSize;
    private String imageUrl;
    private String thumbnailUrl;
    private boolean isPrimary;
    private LocalDateTime uploadedAt;
}
