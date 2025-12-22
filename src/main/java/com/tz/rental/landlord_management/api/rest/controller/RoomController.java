package com.tz.rental.landlord_management.api.rest.controller;

import com.tz.rental.landlord_management.api.rest.dto.ApiResponse;
import com.tz.rental.landlord_management.api.rest.dto.StandardErrorResponse;
import com.tz.rental.landlord_management.application.dto.CreateRoomRequest;
import com.tz.rental.landlord_management.application.dto.RoomResponse;
import com.tz.rental.landlord_management.application.dto.UpdateRoomStatusRequest;
import com.tz.rental.landlord_management.application.service.RoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Room created successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = RoomResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Validation failed",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = StandardErrorResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "House not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = StandardErrorResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = StandardErrorResponse.class)))
    })
    public ResponseEntity<ApiResponse<RoomResponse>> createRoom(@Valid @RequestBody CreateRoomRequest request) {
        RoomResponse response = roomService.createRoom(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Room created successfully", response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get room by ID", description = "Retrieves details of a specific room.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Room retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = RoomResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Room not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = StandardErrorResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = StandardErrorResponse.class)))
    })
    public ResponseEntity<ApiResponse<RoomResponse>> getRoomById(@PathVariable UUID id) {
        RoomResponse response = roomService.getRoomById(id);
        return ResponseEntity.ok(ApiResponse.success("Room retrieved successfully", response));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a room", description = "Updates the details of an existing room.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Room updated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = RoomResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Validation failed",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = StandardErrorResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Room not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = StandardErrorResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = StandardErrorResponse.class)))
    })
    public ResponseEntity<ApiResponse<RoomResponse>> updateRoom(@PathVariable UUID id, @Valid @RequestBody CreateRoomRequest request) {
        RoomResponse response = roomService.updateRoom(id, request);
        return ResponseEntity.ok(ApiResponse.success("Room updated successfully", response));
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "Update room status", description = "Updates the status of a room (e.g., VACANT, OCCUPIED, MAINTENANCE).")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Room status updated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = RoomResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid status value",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = StandardErrorResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Room not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = StandardErrorResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = StandardErrorResponse.class)))
    })
    public ResponseEntity<ApiResponse<RoomResponse>> updateRoomStatus(@PathVariable UUID id, @Valid @RequestBody UpdateRoomStatusRequest request) {
        RoomResponse response = roomService.updateRoomStatus(id, request);
        return ResponseEntity.ok(ApiResponse.success("Room status updated successfully", response));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a room", description = "Deletes a room.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Room deleted successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Room not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = StandardErrorResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = StandardErrorResponse.class)))
    })
    public ResponseEntity<ApiResponse<Void>> deleteRoom(@PathVariable UUID id) {
        roomService.deleteRoom(id);
        return ResponseEntity.ok(ApiResponse.success("Room deleted successfully"));
    }
}