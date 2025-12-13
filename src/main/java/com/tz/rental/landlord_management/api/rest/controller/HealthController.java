package com.tz.rental.landlord_management.api.rest.controller;

import com.tz.rental.landlord_management.api.rest.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/health")
@Tag(name = "Health Check", description = "Endpoints for checking the health and status of the Landlord Management Service")
public class HealthController {

    @Operation(
            summary = "Get Health Status",
            description = "Retrieve the current health status of the Landlord Management Service."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Service is healthy",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "Healthy Response",
                                    summary = "Example of a healthy response",
                                    value = "{\"success\":true,\"message\":\"Service is healthy\",\"data\":{\"service\":\"Landlord Management Service\",\"status\":\"UP\",\"timestamp\":\"2025-01-01T12:00:00.000Z\",\"version\":\"1.0.0\"}}"
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "Internal Server Error",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "Unhealthy Response",
                                    summary = "Example of an unhealthy response",
                                    value = "{\"success\":false,\"message\":\"Service is down\",\"data\":null}"
                            )
                    )
            )
    })
    @GetMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> getHealthStatus() {
        Map<String, Object> data = new HashMap<>();
        data.put("service", "Landlord Management Service");
        data.put("status", "UP");
        data.put("timestamp", LocalDateTime.now().toString());
        data.put("version", "1.0.0");

        return ResponseEntity.ok(
                ApiResponse.success("Service is healthy", data)
        );
    }

    @Operation(
            summary = "Ping the system",
            description = "Simple ping endpoint to check if API is responsive"
    )
    @ApiResponses(
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "API is responsive",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "Ping Response",
                                    summary = "Example of a ping response",
                                    value = "{\"success\":true,\"message\":\"Pong\",\"data\":\"Landlord Management Service is responsive\"}"
                            )
                    )
            )
    )
    @GetMapping("/ping")
    public ResponseEntity<ApiResponse<String>> ping() {
        return ResponseEntity.ok(
                ApiResponse.success("Pong", "Landlord Management Service is responsive")
        );
    }


    @Operation(
            summary = "Get system information",
            description = "Returns detailed system information"
    )
    @ApiResponses(
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "System information retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "System Info Response",
                                    summary = "Example of a system info response",
                                    value = "{\"success\":true,\"message\":\"System information retrieved\",\"data\":{\"service\":\"Landlord Management Service\",\"version\":\"1.0.0\",\"description\":\"Service for managing landlords and their properties in Tanzania.\",\"maintainer\":\"TZ Rental Team\",\"contact\":\"+255 783 944 553\",\"email\":\"abdulmunimsaid82@gmail.com\",\"developer\":\"Abdulmunim Said\",\"purpose\":\"Automate rental property management\"}}"
                            )
                    )
            )
    )
    @GetMapping("/info")
    public ResponseEntity<ApiResponse<Map<String, Object>>> systemInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("service", "Landlord Management Service");
        info.put("version", "1.0.0");
        info.put("description", "Service for managing landlords and their properties in Tanzania.");
        info.put("maintainer", "TZ Rental Team");
        info.put("contact", "+255 783 944 553");
        info.put("email", "abdulmunimsaid82@gmail.com");
        info.put("developer", "Abdulmunim Said");
        info.put("purpose", "Automate rental property management");
        return ResponseEntity.ok(
                ApiResponse.success("System information retrieved", info)
        );
    }
}