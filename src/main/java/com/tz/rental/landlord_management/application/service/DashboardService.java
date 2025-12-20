package com.tz.rental.landlord_management.application.service;

import com.tz.rental.landlord_management.application.dto.DashboardStatsDTO;
import com.tz.rental.landlord_management.domain.model.aggregate.House;
import com.tz.rental.landlord_management.domain.model.aggregate.Landlord;
import com.tz.rental.landlord_management.domain.model.aggregate.Lease;
import com.tz.rental.landlord_management.domain.model.valueobject.LeaseStatus;
import com.tz.rental.landlord_management.domain.repository.HouseRepository;
import com.tz.rental.landlord_management.domain.repository.LeaseRepository;
import com.tz.rental.landlord_management.domain.repository.PaymentRepository;
import com.tz.rental.landlord_management.infrastructure.persistence.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final HouseRepository houseRepository;
    private final LeaseRepository leaseRepository;
    private final PaymentRepository paymentRepository;

    private Landlord.LandlordId getCurrentLandlordId() {
        UserEntity currentUser = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (currentUser.getLandlord() == null) {
            throw new IllegalStateException("The current user is not a landlord.");
        }
        return new Landlord.LandlordId(currentUser.getLandlord().getId());
    }

    public DashboardStatsDTO getLandlordStats() {
        Landlord.LandlordId landlordId = getCurrentLandlordId();

        long totalProperties = houseRepository.countByLandlordId(landlordId);
        List<Lease> activeLeases = leaseRepository.findByLandlordId(landlordId).stream()
                .filter(lease -> lease.getStatus() == LeaseStatus.ACTIVE)
                .toList();

        long occupiedProperties = activeLeases.stream()
                .map(lease -> lease.getRoomId().value())
                .collect(Collectors.toSet()) // Get unique rooms
                .size();

        long vacantProperties = totalProperties - occupiedProperties;
        long totalTenants = activeLeases.stream().map(Lease::getTenantId).distinct().count();

        BigDecimal expectedMonthlyIncome = activeLeases.stream()
                .map(Lease::getRentAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        YearMonth currentMonth = YearMonth.now();
        LocalDate startOfMonth = currentMonth.atDay(1);
        LocalDate endOfMonth = currentMonth.atEndOfMonth();

        List<java.util.UUID> leaseIds = activeLeases.stream().map(lease -> lease.getId().value()).collect(Collectors.toList());
        BigDecimal receivedMonthlyIncome = paymentRepository.sumAmountByLeaseIdInAndPaymentDateBetween(leaseIds, startOfMonth, endOfMonth);
        if (receivedMonthlyIncome == null) {
            receivedMonthlyIncome = BigDecimal.ZERO;
        }

        return DashboardStatsDTO.builder()
                .totalProperties(totalProperties)
                .occupiedProperties(occupiedProperties)
                .vacantProperties(vacantProperties)
                .totalTenants(totalTenants)
                .expectedMonthlyIncome(expectedMonthlyIncome)
                .receivedMonthlyIncome(receivedMonthlyIncome)
                .build();
    }
}