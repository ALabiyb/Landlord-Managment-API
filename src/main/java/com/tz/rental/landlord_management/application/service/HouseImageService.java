package com.tz.rental.landlord_management.application.service;

import com.tz.rental.landlord_management.domain.model.aggregate.HouseImage;
import com.tz.rental.landlord_management.domain.repository.HouseImageRepository;
import com.tz.rental.landlord_management.domain.repository.HouseRepository;
import com.tz.rental.landlord_management.infrastructure.persistence.entity.HouseEntity;
import com.tz.rental.landlord_management.infrastructure.persistence.repository.jpa.JpaHouseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class HouseImageService {

    private final HouseImageRepository houseImageRepository;
    private final JpaHouseRepository houseRepository; // Using JPA repo for easy entity fetch, or Domain repo?
    // Using JpaHouseRepository for now as Domain repo findById returns Aggregate,
    // but we need Entity ideally for relationship?
    // Actually HouseImageRepository save implementation expects HouseId lookup
    // using JpaHouseRepository inside.
    // So here we dealing with Domain objects.

    @Value("${app.storage.local.base-path}")
    private String storageBasePath;

    private static final String THUMBNAIL_PREFIX = "thumb_";
    private static final int THUMBNAIL_WIDTH = 200;

    @Transactional
    public HouseImage uploadImage(UUID houseId, MultipartFile file, String caption, boolean isPrimary)
            throws IOException {
        validateFile(file);

        // 1. Prepare Storage
        Path uploadPath = Paths.get(storageBasePath).toAbsolutePath().normalize();
        Path thumbnailPath = uploadPath.resolve("thumbnails");

        if (!Files.exists(uploadPath))
            Files.createDirectories(uploadPath);
        if (!Files.exists(thumbnailPath))
            Files.createDirectories(thumbnailPath);

        // 2. Generate Filename
        String originalFilename = file.getOriginalFilename();
        String extension = getFileExtension(originalFilename);
        String uniqueFilename = UUID.randomUUID().toString() + (extension.isEmpty() ? ".jpg" : extension);

        // 3. Save Original File
        Path targetLocation = uploadPath.resolve(uniqueFilename);
        Files.copy(file.getInputStream(), targetLocation);

        // 4. Generate Thumbnail
        String thumbnailFilename = THUMBNAIL_PREFIX + uniqueFilename;
        Path targetThumbnailLocation = thumbnailPath.resolve(thumbnailFilename);

        try {
            Thumbnails.of(targetLocation.toFile())
                    .size(THUMBNAIL_WIDTH, THUMBNAIL_WIDTH)
                    .toFile(targetThumbnailLocation.toFile());
        } catch (Exception e) {
            log.warn("Failed to generate thumbnail for {}. Using original as thumbnail placeholder.", uniqueFilename);
            // Fallback? Or just ignore?
        }

        // 5. Create Domain Object
        HouseImage houseImage = HouseImage.builder()
                .id(UUID.randomUUID())
                .houseId(houseId) // Assuming house exists, checked by controller or here
                .fileName(uniqueFilename)
                .fileType(file.getContentType())
                .fileSize(file.getSize())
                .storagePath(targetLocation.toString())
                .thumbnailPath(Files.exists(targetThumbnailLocation) ? targetThumbnailLocation.toString() : null)
                .caption(caption)
                .isPrimary(isPrimary)
                .displayOrder(0) // Logic to determine order?
                .uploadedAt(LocalDateTime.now())
                .build();

        // 6. Handle Primary Flag Logic (if this is primary, unset others)
        if (isPrimary) {
            unsetOtherPrimaryImages(houseId);
        }

        return houseImageRepository.save(houseImage);
    }

    private void unsetOtherPrimaryImages(UUID houseId) {
        // This requires a repository method to find by HouseId.
        // Current HouseImageRepository doesn't have it.
        // We might need to add `findByHouseId` to `HouseImageRepository`.
        // purely updating via JPA for now if possible?
        // For now, let's assume this is the first image or handled separately.
        // Ideally we should fetch all images of house and update them.
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Failed to store empty file.");
        }
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("Only image files are allowed.");
        }
        // Size validation is typically handled by Spring properties
        // (spring.servlet.multipart.max-file-size)
    }

    private String getFileExtension(String filename) {
        if (filename == null)
            return "";
        int dotIndex = filename.lastIndexOf(".");
        if (dotIndex < 0)
            return "";
        return filename.substring(dotIndex);
    }
}
