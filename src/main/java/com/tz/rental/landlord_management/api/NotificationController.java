package com.tz.rental.landlord_management.api;

import com.tz.rental.landlord_management.application.dto.NotificationResponse;
import com.tz.rental.landlord_management.application.service.JpaNotificationService;
import com.tz.rental.landlord_management.infrastructure.persistence.entity.UserEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@Tag(name = "Notification Management", description = "APIs for managing user notifications")
public class NotificationController {

    private final JpaNotificationService notificationService;

    @GetMapping
    @Operation(summary = "Get user notifications")
    public ResponseEntity<Page<NotificationResponse>> getNotifications(
            @AuthenticationPrincipal UserEntity currentUser,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        // Assuming current user is a landlord for now. logic for tenant users can be
        // added later.
        if (currentUser.getLandlord() != null) {
            UUID landlordId = currentUser.getLandlord().getId();
            return ResponseEntity.ok(notificationService.getLandlordNotifications(landlordId, pageable));
        }

        return ResponseEntity.ok(Page.empty());
    }

    @PatchMapping("/{id}/read")
    @Operation(summary = "Mark notification as read")
    public ResponseEntity<Void> markAsRead(@PathVariable UUID id) {
        notificationService.markAsRead(id);
        return ResponseEntity.ok().build();
    }
}
