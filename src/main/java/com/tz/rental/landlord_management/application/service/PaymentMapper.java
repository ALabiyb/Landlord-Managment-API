package com.tz.rental.landlord_management.application.service;

import com.tz.rental.landlord_management.application.dto.PaymentResponse;
import com.tz.rental.landlord_management.domain.model.aggregate.Payment;
import org.springframework.stereotype.Component;

@Component("applicationPaymentMapper")
public class PaymentMapper {

    public PaymentResponse toResponse(Payment payment) {
        PaymentResponse response = new PaymentResponse();
        response.setId(payment.getId().value());
        response.setLeaseId(payment.getLeaseId().value());
        response.setAmountPaid(payment.getAmountPaid());
        response.setPaymentDate(payment.getPaymentDate());
        response.setStatus(payment.getStatus());
        response.setTransactionReference(payment.getTransactionReference());
        response.setCreatedAt(payment.getCreatedAt());
        response.setUpdatedAt(payment.getUpdatedAt());
        return response;
    }
}