package com.tz.rental.landlord_management.domain.model.aggregate;

import com.tz.rental.landlord_management.domain.model.valueobject.RoomStatus;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class Room {

    private final RoomId id;
    private final House.HouseId houseId;
    private String roomNumber;
    private String description;
    private BigDecimal monthlyRent;
    private RoomStatus status;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public record RoomId(UUID value) {
        public RoomId {
            if (value == null) {
                throw new IllegalArgumentException("Room ID cannot be null");
            }
        }
    }

    private Room(RoomId id, House.HouseId houseId, String roomNumber, BigDecimal monthlyRent, String description, RoomStatus status, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.houseId = houseId;
        this.roomNumber = roomNumber;
        this.monthlyRent = monthlyRent;
        this.description = description;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        validate();
    }

    // Factory method - main way to create rooms
    public static Room create(House.HouseId houseId, String roomNumber, BigDecimal monthlyRent, String description) {
        return new Room(
                new RoomId(UUID.randomUUID()),
                houseId,
                roomNumber,
                monthlyRent,
                description,
                RoomStatus.VACANT, // Default status for new room
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    // Factory method for existing rooms (from database)
    public static Room fromExisting(UUID id, UUID houseId, String roomNumber, BigDecimal monthlyRent, String description, RoomStatus status, LocalDateTime createdAt, LocalDateTime updatedAt) {
        return new Room(
                new RoomId(id),
                new House.HouseId(houseId),
                roomNumber,
                monthlyRent,
                description,
                status,
                createdAt,
                updatedAt
        );
    }

    public void updateDetails(String roomNumber, BigDecimal monthlyRent, String description) {
        this.roomNumber = roomNumber;
        this.monthlyRent = monthlyRent;
        this.description = description;
        this.updatedAt = LocalDateTime.now();
        validate();
    }

    public void changeStatus(RoomStatus newStatus) {
        if (this.status == newStatus) {
            throw new IllegalStateException("Room is already in status " + newStatus);
        }
        this.status = newStatus;
        this.updatedAt = LocalDateTime.now();
    }

    private void validate() {
        if (houseId == null) {
            throw new IllegalArgumentException("House ID is required");
        }
        if (roomNumber == null || roomNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Room number is required");
        }
        if (monthlyRent == null || monthlyRent.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Monthly rent must be a non-negative value");
        }
    }
}