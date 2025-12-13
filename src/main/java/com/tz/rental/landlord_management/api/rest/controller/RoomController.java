package com.tz.rental.landlord_management.api.rest.controller;

import com.tz.rental.landlord_management.api.rest.dto.ApiResponse;
import com.tz.rental.landlord_management.application.dto.CreateRoomRequest;
import com.tz.rental.landlord_management.application.dto.RoomResponse;
import com.tz.rental.landlord_management.application.service.RoomService;
import com.tz.rental.landlord_management.domain.model.valueobject.RoomStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/rooms")
@RequiredArgsConstructor
@Tag(name = "Rooms", description = "Endpoints for managing individual rooms within a house.")
public class RoomController {

    private final RoomService roomService;

    @PostMapping
    @Operation(summary = "Create a new room", description = "Registers a new room under a specific house. The room is initially set to VACANT status.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Room created successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    public ResponseEntity<ApiResponse<RoomResponse>> createRoom(@Valid @RequestBody CreateRoomRequest request) {
        RoomResponse response = roomService.createRoom(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Room created successfully", response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get room by ID", description = "Retrieves detailed information for a specific room by its unique ID.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Room found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Room not found")
    })
    public ResponseEntity<ApiResponse<RoomResponse>> getRoom(@PathVariable UUID id) {
        RoomResponse response = roomService.getRoom(id);
        return ResponseEntity.ok(ApiResponse.success("Room retrieved successfully", response));
    }

    @GetMapping("/house/{houseId}")
    @Operation(summary = "Get all rooms in a house", description = "Retrieves a list of all rooms associated with a specific house ID.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Rooms retrieved successfully"),
    })
    public ResponseEntity<ApiResponse<List<RoomResponse>>> getRoomsByHouse(@PathVariable UUID houseId) {
        List<RoomResponse> response = roomService.getRoomsByHouse(houseId);
        return ResponseEntity.ok(ApiResponse.success("Rooms for house retrieved successfully", response));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update room details", description = "Updates the information for a specific room, such as its number, description, or rent.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Room updated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Room not found")
    })
    public ResponseEntity<ApiResponse<RoomResponse>> updateRoom(@PathVariable UUID id, @Valid @RequestBody CreateRoomRequest request) {
        RoomResponse response = roomService.updateRoom(id, request);
        return ResponseEntity.ok(ApiResponse.success("Room updated successfully", response));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a room", description = "Permanently deletes a room from the system. This action cannot be undone.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Room deleted successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Room not found")
    })
    public ResponseEntity<ApiResponse<Void>> deleteRoom(@PathVariable UUID id) {
        roomService.deleteRoom(id);
        return ResponseEntity.ok(ApiResponse.success("Room deleted successfully"));
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "Update room status", description = "Updates the current status of a room (e.g., from VACANT to OCCUPIED).")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Room status updated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid status value"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Room not found")
    })
    public ResponseEntity<ApiResponse<RoomResponse>> updateRoomStatus(
            @PathVariable UUID id,
            @Schema(description = "The new status for the room.", example = "OCCUPIED")
            @RequestParam RoomStatus status) {
        RoomResponse response = roomService.changeRoomStatus(id, status);
        return ResponseEntity.ok(ApiResponse.success("Room status updated successfully", response));
    }
}