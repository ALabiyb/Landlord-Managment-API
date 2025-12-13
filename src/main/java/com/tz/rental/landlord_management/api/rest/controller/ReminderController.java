package com.tz.rental.landlord_management.api.rest.controller;

import com.tz.rental.landlord_management.api.rest.dto.ApiResponse;
import com.tz.rental.landlord_management.application.service.ReminderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/reminders")
@RequiredArgsConstructor
@Tag(name = "Reminders", description = "Endpoints for managing and triggering notifications")
public class ReminderController {

    private final ReminderService reminderService;

    @PostMapping("/trigger-rent-due")
    @Operation(summary = "Manually trigger rent due reminders",
            description = "Triggers the process to send rent due reminders to tenants with upcoming payments.")
    public ResponseEntity<ApiResponse<String>> triggerRentDueReminders() {
        reminderService.sendRentDueReminders();
        return ResponseEntity.ok(ApiResponse.success("Rent due reminders triggered successfully."));
    }

    @PostMapping("/trigger-lease-expiry")
    @Operation(summary = "Manually trigger lease expiry reminders",
            description = "Triggers the process to send lease expiry reminders to tenants with upcoming lease end dates.")
    public ResponseEntity<ApiResponse<String>> triggerLeaseExpiryReminders() {
        reminderService.sendLeaseExpiryReminders();
        return ResponseEntity.ok(ApiResponse.success("Lease expiry reminders triggered successfully."));
    }

    @PostMapping("/trigger-overdue-payments")
    @Operation(summary = "Manually trigger overdue payment alerts",
            description = "Triggers the process to send alerts for overdue payments. (Requires payment tracking logic)")
    public ResponseEntity<ApiResponse<String>> triggerOverduePaymentAlerts() {
        reminderService.sendOverduePaymentAlerts();
        return ResponseEntity.ok(ApiResponse.success("Overdue payment alerts triggered successfully (if logic implemented)."));
    }
}