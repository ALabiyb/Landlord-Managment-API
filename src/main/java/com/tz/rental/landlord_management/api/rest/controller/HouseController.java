package com.tz.rental.landlord_management.api.rest.controller;

import com.tz.rental.landlord_management.api.rest.dto.ApiResponse;
import com.tz.rental.landlord_management.api.rest.dto.StandardErrorResponse;
import com.tz.rental.landlord_management.application.dto.CreateHouseRequest;
import com.tz.rental.landlord_management.application.dto.HouseResponse;
import com.tz.rental.landlord_management.application.dto.PaginatedResponse;
import com.tz.rental.landlord_management.application.dto.RoomResponse;
import com.tz.rental.landlord_management.application.service.HouseService;
import com.tz.rental.landlord_management.application.service.RoomService;
import com.tz.rental.landlord_management.domain.model.valueobject.RoomStatus;
import com.tz.rental.landlord_management.infrastructure.persistence.entity.UserEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/houses")
@RequiredArgsConstructor
@Tag(name = "Houses", description = "Endpoints for managing houses (properties)")
public class HouseController {

        private final HouseService houseService;
        private final RoomService roomService;
        private final com.tz.rental.landlord_management.application.service.HouseImageService houseImageService;

        @PostMapping
        @Operation(summary = "Create a new house", description = "Creates a new property listing.")
        @ApiResponses(value = {
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "House created successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = HouseResponse.class))),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Validation failed (e.g., missing required fields)", content = @Content(mediaType = "application/json", schema = @Schema(implementation = StandardErrorResponse.class))),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "House with this property code already exists", content = @Content(mediaType = "application/json", schema = @Schema(implementation = StandardErrorResponse.class))),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = StandardErrorResponse.class)))
        })
        public ResponseEntity<ApiResponse<HouseResponse>> createHouse(
                        @AuthenticationPrincipal UserEntity currentUser,
                        @Valid @RequestBody CreateHouseRequest request) {
                HouseResponse response = houseService.createHouse(request);
                return ResponseEntity.status(HttpStatus.CREATED)
                                .body(ApiResponse.success("House created successfully", response));
        }

        @PostMapping(value = "/{houseId}/images", consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
        @Operation(summary = "Upload house image", description = "Uploads an image for a specific house.")
        public ResponseEntity<ApiResponse<com.tz.rental.landlord_management.application.dto.ImageUploadResponse>> uploadHouseImage(
                        @PathVariable UUID houseId,
                        @RequestParam("file") org.springframework.web.multipart.MultipartFile file,
                        @RequestParam(value = "caption", required = false) String caption,
                        @RequestParam(value = "isPrimary", defaultValue = "false") boolean isPrimary)
                        throws java.io.IOException {
                com.tz.rental.landlord_management.domain.model.aggregate.HouseImage image = houseImageService
                                .uploadImage(houseId, file, caption, isPrimary);

                com.tz.rental.landlord_management.application.dto.ImageUploadResponse response = com.tz.rental.landlord_management.application.dto.ImageUploadResponse
                                .builder()
                                .id(image.getId())
                                .fileName(image.getFileName())
                                .fileSize(image.getFileSize())
                                .imageUrl("/api/images/" + image.getFileName())
                                .thumbnailUrl(image.getThumbnailPath() != null
                                                ? "/api/images/thumbnails/" + java.nio.file.Paths
                                                                .get(image.getThumbnailPath()).getFileName().toString()
                                                : null)
                                .isPrimary(image.isPrimary())
                                .uploadedAt(image.getUploadedAt())
                                .build();

                return ResponseEntity.status(HttpStatus.CREATED)
                                .body(ApiResponse.success("Image uploaded successfully", response));
        }

        @GetMapping("/{id}")
        @Operation(summary = "Get house by ID", description = "Retrieves details of a specific house.")
        @ApiResponses(value = {
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "House retrieved successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = HouseResponse.class))),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "House not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = StandardErrorResponse.class))),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = StandardErrorResponse.class)))
        })
        public ResponseEntity<ApiResponse<HouseResponse>> getHouseById(
                        @AuthenticationPrincipal UserEntity currentUser,
                        @PathVariable UUID id,
                        @RequestParam(required = false) Boolean includeRooms) {
                HouseResponse response = houseService.getHouseById(id, includeRooms);
                return ResponseEntity.ok(ApiResponse.success("House retrieved successfully", response));
        }

        @GetMapping
        @Operation(summary = "Get all houses", description = "Retrieves a paginated list of all houses for the current landlord.")
        @ApiResponses(value = {
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Houses retrieved successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PaginatedResponse.class))),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = StandardErrorResponse.class)))
        })
        public ResponseEntity<ApiResponse<PaginatedResponse<List<HouseResponse>>>> getAllHouses(
                        @AuthenticationPrincipal UserEntity currentUser,
                        Pageable pageable,
                        @RequestParam(required = false) String status) {
                PaginatedResponse<List<HouseResponse>> response = houseService.getAllHouses(pageable, status);
                return ResponseEntity.ok(ApiResponse.success("Houses retrieved successfully", response));
        }

        @PutMapping("/{id}")
        @Operation(summary = "Update a house", description = "Updates the details of an existing house.")
        @ApiResponses(value = {
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "House updated successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = HouseResponse.class))),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Validation failed", content = @Content(mediaType = "application/json", schema = @Schema(implementation = StandardErrorResponse.class))),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "House not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = StandardErrorResponse.class))),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Property code already exists", content = @Content(mediaType = "application/json", schema = @Schema(implementation = StandardErrorResponse.class))),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = StandardErrorResponse.class)))
        })
        public ResponseEntity<ApiResponse<HouseResponse>> updateHouse(
                        @AuthenticationPrincipal UserEntity currentUser,
                        @PathVariable UUID id,
                        @Valid @RequestBody CreateHouseRequest request) {
                HouseResponse response = houseService.updateHouse(id, request);
                return ResponseEntity.ok(ApiResponse.success("House updated successfully", response));
        }

        @DeleteMapping("/{id}")
        @Operation(summary = "Delete a house", description = "Deletes a house and all its associated rooms.")
        @ApiResponses(value = {
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "House deleted successfully"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "House not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = StandardErrorResponse.class))),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = StandardErrorResponse.class)))
        })
        public ResponseEntity<ApiResponse<Void>> deleteHouse(
                        @AuthenticationPrincipal UserEntity currentUser,
                        @PathVariable UUID id) {
                houseService.deleteHouse(id);
                return ResponseEntity.ok(ApiResponse.success("House deleted successfully"));
        }

        @GetMapping("/{houseId}/rooms")
        @Operation(summary = "Get all rooms for a house", description = "Retrieves a list of all rooms for a specific house.")
        @ApiResponses(value = {
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Rooms retrieved successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = RoomResponse.class))),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "House not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = StandardErrorResponse.class))),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = StandardErrorResponse.class)))
        })
        public ResponseEntity<ApiResponse<List<RoomResponse>>> getAllRoomsForHouse(
                        @AuthenticationPrincipal UserEntity currentUser,
                        @PathVariable UUID houseId,
                        @RequestParam(required = false) RoomStatus status) {
                UUID landlordId = currentUser.getLandlord().getId();
                List<RoomResponse> response = roomService.getAllRoomsForHouse(landlordId, houseId, status);
                return ResponseEntity.ok(ApiResponse.success("Rooms retrieved successfully", response));
        }

        @GetMapping("/{houseId}/vacant-rooms")
        @Operation(summary = "Get vacant rooms for a house", description = "Helper endpoint specifically for fetching vacant rooms for dropdowns.")
        public ResponseEntity<ApiResponse<List<RoomResponse>>> getVacantRoomsForHouse(
                        @AuthenticationPrincipal UserEntity currentUser,
                        @PathVariable UUID houseId) {
                UUID landlordId = currentUser.getLandlord().getId();
                List<RoomResponse> response = roomService.getAllRoomsForHouse(landlordId, houseId, RoomStatus.VACANT);
                return ResponseEntity.ok(ApiResponse.success("Vacant rooms retrieved successfully", response));
        }
}