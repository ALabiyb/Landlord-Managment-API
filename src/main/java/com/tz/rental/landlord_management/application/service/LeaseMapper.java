package com.tz.rental.landlord_management.application.service;

import com.tz.rental.landlord_management.application.dto.LeaseResponse;
import com.tz.rental.landlord_management.domain.model.aggregate.Lease;
import org.springframework.stereotype.Component;

@Component("applicationLeaseMapper")
public class LeaseMapper {

    public LeaseResponse toResponse(Lease lease) {
        LeaseResponse response = new LeaseResponse();
        response.setId(lease.getId().value());
        response.setTenantId(lease.getTenantId().value());
        response.setRoomId(lease.getRoomId().value());
        response.setStartDate(lease.getStartDate());
        response.setEndDate(lease.getEndDate());
        response.setRentAmount(lease.getRentAmount());
        response.setPaymentPeriod(lease.getPaymentPeriod());
        response.setStatus(lease.getStatus());
        response.setContractDocumentUrl(lease.getContractDocumentUrl());
        response.setCreatedAt(lease.getCreatedAt());
        return response;
    }
}