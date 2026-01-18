package com.tz.rental.landlord_management.api.rest.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/images")
@RequiredArgsConstructor
@Tag(name = "Images", description = "Endpoints for retrieving images")
public class ImageController {

    @Value("${app.storage.local.base-path}")
    private String storageBasePath;

    @GetMapping("/{filename:.+}")
    @Operation(summary = "Get image", description = "Retrieves an image by filename.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Image retrieved successfully", content = @Content(mediaType = "image/jpeg")),
            @ApiResponse(responseCode = "404", description = "Image not found", content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<Resource> serveImage(@PathVariable String filename) {
        try {
            Path file = Paths.get(storageBasePath).resolve(filename).normalize();
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() || resource.isReadable()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG) // Dynamically determine type if possible, assume JPEG/PNG
                                                           // for now
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (MalformedURLException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/thumbnails/{filename:.+}")
    @Operation(summary = "Get thumbnail", description = "Retrieves a thumbnail by filename.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Thumbnail retrieved successfully", content = @Content(mediaType = "image/jpeg")),
            @ApiResponse(responseCode = "404", description = "Thumbnail not found", content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<Resource> serveThumbnail(@PathVariable String filename) {
        try {
            Path file = Paths.get(storageBasePath).resolve("thumbnails").resolve(filename).normalize();
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() || resource.isReadable()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG)
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (MalformedURLException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
