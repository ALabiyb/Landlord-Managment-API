package com.tz.rental.landlord_management.api;

import com.tz.rental.landlord_management.application.dto.CreatePaymentRequest;
import com.tz.rental.landlord_management.application.dto.PaymentResponse;
import com.tz.rental.landlord_management.application.dto.UpdatePaymentRequest;
import com.tz.rental.landlord_management.application.service.JpaPaymentService;
import com.tz.rental.landlord_management.domain.model.valueobject.PaymentStatus;
import com.tz.rental.landlord_management.infrastructure.persistence.entity.UserEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * REST Controller for managing payments.
 */
@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@Tag(name = "Payment Management", description = "APIs for recording and managing rent payments")
public class PaymentController {

    private final JpaPaymentService paymentService;

    @PostMapping
    @Operation(summary = "Record a new payment")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Payment recorded successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "404", description = "Lease not found")
    })
    public ResponseEntity<PaymentResponse> createPayment(
            @AuthenticationPrincipal UserEntity currentUser,
            @Valid @RequestBody CreatePaymentRequest request) {

        UUID landlordId = currentUser.getLandlord().getId();
        PaymentResponse response = paymentService.createPayment(landlordId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Get all payments")
    public ResponseEntity<Page<PaymentResponse>> getAllPayments(
            @AuthenticationPrincipal UserEntity currentUser,
            @PageableDefault(size = 20, sort = "paymentDate", direction = Sort.Direction.DESC) Pageable pageable) {

        UUID landlordId = currentUser.getLandlord().getId();
        Page<PaymentResponse> payments = paymentService.getAllPayments(landlordId, pageable);
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get payments by status")
    public ResponseEntity<Page<PaymentResponse>> getPaymentsByStatus(
            @AuthenticationPrincipal UserEntity currentUser,
            @PathVariable PaymentStatus status,
            @PageableDefault(size = 20, sort = "paymentDate", direction = Sort.Direction.DESC) Pageable pageable) {

        UUID landlordId = currentUser.getLandlord().getId();
        Page<PaymentResponse> payments = paymentService.getPaymentsByStatus(landlordId, status, pageable);
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get payment by ID")
    public ResponseEntity<PaymentResponse> getPayment(
            @AuthenticationPrincipal UserEntity currentUser,
            @PathVariable UUID id) {

        UUID landlordId = currentUser.getLandlord().getId();
        PaymentResponse payment = paymentService.getPayment(landlordId, id);
        return ResponseEntity.ok(payment);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a payment")
    public ResponseEntity<PaymentResponse> updatePayment(
            @AuthenticationPrincipal UserEntity currentUser,
            @PathVariable UUID id,
            @Valid @RequestBody UpdatePaymentRequest request) {

        UUID landlordId = currentUser.getLandlord().getId();
        PaymentResponse response = paymentService.updatePayment(landlordId, id, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/lease/{leaseId}")
    @Operation(summary = "Get payments for a specific lease")
    public ResponseEntity<List<PaymentResponse>> getPaymentsByLease(
            @AuthenticationPrincipal UserEntity currentUser,
            @PathVariable UUID leaseId) {

        UUID landlordId = currentUser.getLandlord().getId();
        List<PaymentResponse> payments = paymentService.getPaymentsByLease(landlordId, leaseId);
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/revenue")
    @Operation(summary = "Get monthly revenue")
    public ResponseEntity<BigDecimal> getMonthlyRevenue(
            @AuthenticationPrincipal UserEntity currentUser,
            @RequestParam int year,
            @RequestParam int month) {

        UUID landlordId = currentUser.getLandlord().getId();
        BigDecimal revenue = paymentService.calculateMonthlyRevenue(landlordId, year, month);
        return ResponseEntity.ok(revenue);
    }
}
