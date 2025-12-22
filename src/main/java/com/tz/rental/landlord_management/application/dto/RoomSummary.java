package com.tz.rental.landlord_management.application.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class RoomSummary {
    private UUID id;
    private String roomNumber;
    private String status;
}