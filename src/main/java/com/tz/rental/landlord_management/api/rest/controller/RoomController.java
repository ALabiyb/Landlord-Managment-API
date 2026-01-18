package com.tz.rental.landlord_management.api.rest.controller;

import com.tz.rental.landlord_management.api.rest.dto.ApiResponse;
import com.tz.rental.landlord_management.api.rest.dto.StandardErrorResponse;
import com.tz.rental.landlord_management.application.dto.CreateRoomRequest;
import com.tz.rental.landlord_management.application.dto.RoomResponse;
import com.tz.rental.landlord_management.application.dto.UpdateRoomStatusRequest;
import com.tz.rental.landlord_management.application.service.RoomService;
import com.tz.rental.landlord_management.infrastructure.persistence.entity.UserEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/rooms")
@RequiredArgsConstructor
@Tag(name = "Rooms", description = "Endpoints for managing individual rooms within a house")
public class RoomController {

        private final RoomService roomService;

        @PostMapping
        @Operation(summary = "Create a new room", description = "Creates a new room within an existing house.")
        public ResponseEntity<ApiResponse<RoomResponse>> createRoom(
                        @AuthenticationPrincipal UserEntity currentUser,
                        @Valid @RequestBody CreateRoomRequest request) {
                UUID landlordId = currentUser.getLandlord().getId();
                RoomResponse response = roomService.createRoom(landlordId, request);
                return ResponseEntity.status(HttpStatus.CREATED)
                                .body(ApiResponse.success("Room created successfully", response));
        }

        @GetMapping("/{id}")
        @Operation(summary = "Get room by ID", description = "Retrieves details of a specific room.")
        public ResponseEntity<ApiResponse<RoomResponse>> getRoomById(
                        @AuthenticationPrincipal UserEntity currentUser,
                        @PathVariable UUID id) {
                UUID landlordId = currentUser.getLandlord().getId();
                RoomResponse response = roomService.getRoomById(landlordId, id);
                return ResponseEntity.ok(ApiResponse.success("Room retrieved successfully", response));
        }

        @PutMapping("/{id}")
        @Operation(summary = "Update a room", description = "Updates the details of an existing room.")
        public ResponseEntity<ApiResponse<RoomResponse>> updateRoom(
                        @AuthenticationPrincipal UserEntity currentUser,
                        @PathVariable UUID id,
                        @Valid @RequestBody CreateRoomRequest request) {
                UUID landlordId = currentUser.getLandlord().getId();
                RoomResponse response = roomService.updateRoom(landlordId, id, request);
                return ResponseEntity.ok(ApiResponse.success("Room updated successfully", response));
        }

        @PutMapping("/{id}/status")
        @Operation(summary = "Update room status", description = "Updates the status of a room (e.g., VACANT, OCCUPIED, MAINTENANCE).")
        public ResponseEntity<ApiResponse<RoomResponse>> updateRoomStatus(
                        @AuthenticationPrincipal UserEntity currentUser,
                        @PathVariable UUID id,
                        @Valid @RequestBody UpdateRoomStatusRequest request) {
                UUID landlordId = currentUser.getLandlord().getId();
                RoomResponse response = roomService.updateRoomStatus(landlordId, id, request);
                return ResponseEntity.ok(ApiResponse.success("Room status updated successfully", response));
        }

        @DeleteMapping("/{id}")
        @Operation(summary = "Delete a room", description = "Deletes a room.")
        public ResponseEntity<ApiResponse<Void>> deleteRoom(
                        @AuthenticationPrincipal UserEntity currentUser,
                        @PathVariable UUID id) {
                UUID landlordId = currentUser.getLandlord().getId();
                roomService.deleteRoom(landlordId, id);
                return ResponseEntity.ok(ApiResponse.success("Room deleted successfully"));
        }
}
