package com.tz.rental.landlord_management.domain.model.aggregate;

import com.tz.rental.landlord_management.domain.model.valueobject.RoomStatus;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class Room {

    private final RoomId id;
    private final House.HouseId houseId;
    private String roomNumber;
    private String description;
    private BigDecimal monthlyRent;
    private RoomStatus status;
    private String size;
    private List<String> imageUrls;
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
        this.imageUrls = new ArrayList<>();
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

    // Builder for reconstructing existing rooms
    public static class RoomBuilder {
        private final Room room;

        public RoomBuilder(UUID id, UUID houseId, LocalDateTime createdAt) {
            // A minimal constructor for the builder
            this.room = new Room(new RoomId(id), new House.HouseId(houseId), null, null, null, null, createdAt, createdAt);
        }

        public RoomBuilder roomNumber(String roomNumber) {
            room.roomNumber = roomNumber;
            return this;
        }

        public RoomBuilder monthlyRent(BigDecimal monthlyRent) {
            room.monthlyRent = monthlyRent;
            return this;
        }

        public RoomBuilder description(String description) {
            room.description = description;
            return this;
        }

        public RoomBuilder status(RoomStatus status) {
            room.status = status;
            return this;
        }

        public RoomBuilder updatedAt(LocalDateTime updatedAt) {
            room.updatedAt = updatedAt;
            return this;
        }

        public RoomBuilder size(String size) {
            room.size = size;
            return this;
        }

        public RoomBuilder imageUrls(List<String> imageUrls) {
            room.imageUrls = imageUrls != null ? imageUrls : new ArrayList<>();
            return this;
        }

        public Room build() {
            room.validate(); // Validate the fully constructed object
            return room;
        }
    }

    public void updateDetails(String roomNumber, BigDecimal monthlyRent, String description, String size) {
        this.roomNumber = roomNumber;
        this.monthlyRent = monthlyRent;
        this.description = description;
        this.size = size;
        this.updatedAt = LocalDateTime.now();
        validate();
    }

    public void addImageUrl(String url) {
        if (this.imageUrls == null) {
            this.imageUrls = new ArrayList<>();
        }
        this.imageUrls.add(url);
        this.updatedAt = LocalDateTime.now();
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