package com.tz.rental.landlord_management.infrastructure.persistence.mapper;

import com.tz.rental.landlord_management.domain.model.aggregate.Lease;
import com.tz.rental.landlord_management.domain.model.aggregate.Room;
import com.tz.rental.landlord_management.domain.model.aggregate.Tenant;
import com.tz.rental.landlord_management.infrastructure.persistence.entity.LeaseEntity;
import com.tz.rental.landlord_management.infrastructure.persistence.entity.RoomEntity;
import com.tz.rental.landlord_management.infrastructure.persistence.entity.TenantEntity;
import org.springframework.stereotype.Component;

@Component("persistenceLeaseMapper")
public class LeaseMapper {

    public LeaseEntity toEntity(Lease lease) {
        LeaseEntity entity = new LeaseEntity();
        entity.setId(lease.getId().value());

        TenantEntity tenantEntity = new TenantEntity();
        tenantEntity.setId(lease.getTenantId().value());
        entity.setTenant(tenantEntity);

        RoomEntity roomEntity = new RoomEntity();
        roomEntity.setId(lease.getRoomId().value());
        entity.setRoom(roomEntity);

        entity.setStartDate(lease.getStartDate());
        entity.setEndDate(lease.getEndDate());
        entity.setRentAmount(lease.getRentAmount());
        entity.setPaymentPeriod(lease.getPaymentPeriod());
        entity.setStatus(lease.getStatus());
        entity.setContractDocumentUrl(lease.getContractDocumentUrl());
        entity.setCreatedAt(lease.getCreatedAt()); // Set createdAt from domain
        entity.setUpdatedAt(lease.getUpdatedAt()); // Set updatedAt from domain
        return entity;
    }

    public Lease toDomain(LeaseEntity entity) {
        // Use the fromExisting factory method to reconstruct the Lease aggregate
        Lease lease = Lease.fromExisting(
                entity.getId(),
                entity.getTenant().getId(),
                entity.getRoom().getId(),
                entity.getStartDate(),
                entity.getEndDate(),
                entity.getRentAmount(),
                entity.getPaymentPeriod(),
                entity.getStatus(),
                entity.getContractDocumentUrl(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
        return lease;
    }
}