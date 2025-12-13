package com.tz.rental.landlord_management.api.rest.controller;

import com.tz.rental.landlord_management.api.rest.dto.ApiResponse;
import com.tz.rental.landlord_management.application.dto.CreatePaymentRequest;
import com.tz.rental.landlord_management.application.dto.PaymentResponse;
import com.tz.rental.landlord_management.application.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@Tag(name = "Payments", description = "Endpoints for managing rent payments")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    @Operation(summary = "Record a new payment", description = "Records a new rent payment for a specific lease.")
    public ResponseEntity<ApiResponse<PaymentResponse>> createPayment(@Valid @RequestBody CreatePaymentRequest request) {
        PaymentResponse response = paymentService.createPayment(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Payment recorded successfully", response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get payment by ID", description = "Retrieves details of a specific payment by its ID.")
    public ResponseEntity<ApiResponse<PaymentResponse>> getPayment(@PathVariable UUID id) {
        PaymentResponse response = paymentService.getPayment(id);
        return ResponseEntity.ok(ApiResponse.success("Payment retrieved successfully", response));
    }

    @GetMapping("/lease/{leaseId}")
    @Operation(summary = "Get payments by lease ID", description = "Retrieves all payments made for a specific lease.")
    public ResponseEntity<ApiResponse<List<PaymentResponse>>> getPaymentsByLeaseId(@PathVariable UUID leaseId) {
        List<PaymentResponse> response = paymentService.getPaymentsByLeaseId(leaseId);
        return ResponseEntity.ok(ApiResponse.success("Payments retrieved successfully for lease", response));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update payment details", description = "Updates the details of an existing payment.")
    public ResponseEntity<ApiResponse<PaymentResponse>> updatePayment(@PathVariable UUID id, @Valid @RequestBody CreatePaymentRequest request) {
        PaymentResponse response = paymentService.updatePayment(id, request);
        return ResponseEntity.ok(ApiResponse.success("Payment updated successfully", response));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a payment", description = "Deletes a payment record by its ID.")
    public ResponseEntity<ApiResponse<Void>> deletePayment(@PathVariable UUID id) {
        paymentService.deletePayment(id);
        return ResponseEntity.ok(ApiResponse.success("Payment deleted successfully"));
    }
}