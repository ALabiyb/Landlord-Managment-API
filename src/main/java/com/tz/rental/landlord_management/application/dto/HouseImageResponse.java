package com.tz.rental.landlord_management.application.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class HouseImageResponse {
    private UUID id;
    private String imageUrl;
    private String thumbnailUrl;
    private String caption;
    private boolean isPrimary;
    private int displayOrder;
}
