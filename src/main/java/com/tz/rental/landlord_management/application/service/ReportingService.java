package com.tz.rental.landlord_management.application.service;

import com.tz.rental.landlord_management.application.dto.MonthlyIncomeReport;
import com.tz.rental.landlord_management.application.dto.MonthlyIncomeReportEntry;
import com.tz.rental.landlord_management.application.dto.VacancyReport;
import com.tz.rental.landlord_management.application.dto.VacancyReportEntry;
import com.tz.rental.landlord_management.domain.model.aggregate.House;
import com.tz.rental.landlord_management.domain.model.aggregate.Landlord;
import com.tz.rental.landlord_management.domain.model.aggregate.Lease;
import com.tz.rental.landlord_management.domain.model.aggregate.Payment;
import com.tz.rental.landlord_management.domain.model.aggregate.Room;
import com.tz.rental.landlord_management.domain.model.aggregate.Tenant;
import com.tz.rental.landlord_management.domain.model.valueobject.LeaseStatus;
import com.tz.rental.landlord_management.domain.model.valueobject.RoomStatus;
import com.tz.rental.landlord_management.domain.repository.*;
import com.tz.rental.landlord_management.infrastructure.persistence.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportingService {

    private final LeaseRepository leaseRepository;
    private final PaymentRepository paymentRepository;
    private final TenantRepository tenantRepository;
    private final RoomRepository roomRepository;
    private final HouseRepository houseRepository;

    private Landlord.LandlordId getCurrentLandlordId() {
        UserEntity currentUser = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (currentUser.getLandlord() == null) {
            throw new IllegalStateException("The current user is not a landlord.");
        }
        return new Landlord.LandlordId(currentUser.getLandlord().getId());
    }

    public MonthlyIncomeReport generateMonthlyIncomeReport(YearMonth yearMonth) {
        log.info("Generating monthly income report for {}", yearMonth);
        Landlord.LandlordId currentLandlordId = getCurrentLandlordId();

        List<MonthlyIncomeReportEntry> entries = new ArrayList<>();
        BigDecimal totalExpectedIncome = BigDecimal.ZERO;
        BigDecimal totalActualIncome = BigDecimal.ZERO;
        BigDecimal totalOutstandingBalance = BigDecimal.ZERO;

        // Get all active or upcoming leases that belong to the current landlord
        List<Lease> relevantLeases = leaseRepository.findByLandlordId(currentLandlordId).stream()
                .filter(lease -> (lease.getStatus() == LeaseStatus.ACTIVE || lease.getStatus() == LeaseStatus.UPCOMING) &&
                        !lease.getStartDate().isAfter(yearMonth.atEndOfMonth()) &&
                        !lease.getEndDate().isBefore(yearMonth.atDay(1)))
                .collect(Collectors.toList());

        for (Lease lease : relevantLeases) {
            // Fetch related entities
            Tenant tenant = tenantRepository.findById(lease.getTenantId().value()).orElse(null);
            Room room = roomRepository.findById(lease.getRoomId().value()).orElse(null);
            House house = (room != null) ? houseRepository.findById(room.getHouseId()).orElse(null) : null;

            if (tenant == null || room == null || house == null) {
                log.warn("Skipping lease {} due to missing related entities (tenant, room, or house).", lease.getId().value());
                continue;
            }

            // Calculate expected rent for the month (simplified: assumes full month)
            BigDecimal expectedRent = lease.getRentAmount();
            totalExpectedIncome = totalExpectedIncome.add(expectedRent);

            // Calculate actual payments for this lease in the report month
            List<Payment> paymentsInMonth = paymentRepository.findByLeaseId(lease.getId().value()).stream()
                    .filter(payment -> YearMonth.from(payment.getPaymentDate()).equals(yearMonth))
                    .collect(Collectors.toList());

            BigDecimal paidAmount = paymentsInMonth.stream()
                    .map(Payment::getAmountPaid)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            totalActualIncome = totalActualIncome.add(paidAmount);

            BigDecimal balance = expectedRent.subtract(paidAmount);
            totalOutstandingBalance = totalOutstandingBalance.add(balance);

            entries.add(MonthlyIncomeReportEntry.builder()
                    .leaseId(lease.getId().value())
                    .tenantName(tenant.getFirstName() + " " + tenant.getLastName())
                    .roomNumber(room.getRoomNumber())
                    .houseName(house.getName())
                    .expectedRent(expectedRent)
                    .amountPaid(paidAmount)
                    .balance(balance)
                    .build());
        }

        return MonthlyIncomeReport.builder()
                .reportMonth(yearMonth)
                .totalExpectedIncome(totalExpectedIncome)
                .totalActualIncome(totalActualIncome)
                .totalOutstandingBalance(totalOutstandingBalance)
                .entries(entries)
                .build();
    }

    public VacancyReport generateVacancyReport() {
        log.info("Generating vacancy report.");
        Landlord.LandlordId currentLandlordId = getCurrentLandlordId();

        List<VacancyReportEntry> entries = new ArrayList<>();

        // Get all houses belonging to the current landlord
        List<House> landlordsHouses = houseRepository.findByLandlordId(currentLandlordId);
        List<House.HouseId> landlordsHouseIds = landlordsHouses.stream()
                .map(House::getId)
                .collect(Collectors.toList());

        // Find vacant rooms only within these houses
        List<Room> vacantRooms = roomRepository.findAll().stream()
                .filter(room -> room.getStatus() == RoomStatus.VACANT && landlordsHouseIds.contains(room.getHouseId()))
                .collect(Collectors.toList());

        for (Room room : vacantRooms) {
            House house = houseRepository.findById(room.getHouseId()).orElse(null);
            if (house == null) {
                log.warn("Skipping vacant room {} due to missing house {}.", room.getId().value(), room.getHouseId().value());
                continue;
            }

            entries.add(VacancyReportEntry.builder()
                    .roomId(room.getId().value())
                    .roomNumber(room.getRoomNumber())
                    .houseName(house.getName())
                    .houseId(house.getId().value())
                    .roomDescription(room.getDescription())
                    .build());
        }

        return VacancyReport.builder()
                .reportDate(LocalDate.now())
                .totalVacantRooms(entries.size())
                .entries(entries)
                .build();
    }
}