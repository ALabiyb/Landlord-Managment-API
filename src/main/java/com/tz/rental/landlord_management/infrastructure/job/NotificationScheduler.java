package com.tz.rental.landlord_management.infrastructure.job;

import com.tz.rental.landlord_management.application.service.JpaNotificationService;
import com.tz.rental.landlord_management.domain.model.valueobject.LeaseStatus;
import com.tz.rental.landlord_management.domain.model.valueobject.NotificationPriority;
import com.tz.rental.landlord_management.domain.model.valueobject.NotificationType;
import com.tz.rental.landlord_management.infrastructure.persistence.entity.LeaseEntity;
import com.tz.rental.landlord_management.infrastructure.persistence.repository.jpa.JpaLeaseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationScheduler {

    private final JpaLeaseRepository leaseRepository;
    private final JpaNotificationService notificationService;
    private final com.tz.rental.landlord_management.application.service.WhatsAppNotificationService whatsAppNotificationService;

    /**
     * Run daily at 10 AM.
     */
    @Scheduled(cron = "0 0 10 * * ?")
    public void generateLeaseExpiryNotifications() {
        log.info("Running lease expiry notification job");

        LocalDate today = LocalDate.now();
        LocalDate expiryDate = today.plusDays(30); // Notify for leases expiring in 30 days

        List<LeaseEntity> expiringLeases = leaseRepository.findByStatusAndEndDateBetween(
                LeaseStatus.ACTIVE, expiryDate, expiryDate);

        for (LeaseEntity lease : expiringLeases) {
            String message = String.format("Lease for room %s (Tenant: %s %s) expires on %s",
                    lease.getRoom().getRoomNumber(),
                    lease.getTenant().getFirstName(),
                    lease.getTenant().getLastName(),
                    lease.getEndDate());

            // 1. Persist Notification (Internal System)
            notificationService.createNotificationForLandlord(
                    lease.getRoom().getHouse().getLandlord().getId(),
                    "Lease Expiring Soon",
                    message,
                    NotificationType.LEASE_EXPIRY_REMINDER,
                    NotificationPriority.MEDIUM);

            // 2. Send External Notification (WhatsApp) - To Landlord (Mocking phone number
            // for now)
            // In real app, Landlord entity would have a phoneNumber field.
            whatsAppNotificationService.sendNotification(
                    "Landlord-" + lease.getRoom().getHouse().getLandlord().getId(),
                    message,
                    NotificationType.LEASE_EXPIRY_REMINDER);
        }
    }

    /**
     * Run daily at 9 AM to check for rent due dates.
     */
    @Scheduled(cron = "0 0 9 * * ?")
    public void generateRentDueNotifications() {
        log.info("Running rent due (monthly) notification job");

        LocalDate today = LocalDate.now();
        int currentDayOfMonth = today.getDayOfMonth();

        // Find all active leases
        // Note: For large datasets, use a more specific query or pagination.
        List<LeaseEntity> activeLeases = leaseRepository.findByStatus(LeaseStatus.ACTIVE);

        for (LeaseEntity lease : activeLeases) {
            // Simple logic: If lease started on day X, rent is due on day X of every month.
            // Adjust for end of month cases (e.g. started on 31st, but today is 30th of
            // June) could be added later.
            if (lease.getStartDate().getDayOfMonth() == currentDayOfMonth) {
                String tenantName = lease.getTenant().getFirstName() + " " + lease.getTenant().getLastName();
                String message = String.format("Hello %s, your rent of %s TZS for Room %s is due today.",
                        tenantName,
                        lease.getRentAmount(),
                        lease.getRoom().getRoomNumber());

                // 1. Persist Notification for Tenant
                notificationService.createNotificationForTenant(
                        lease.getTenant().getId(),
                        "Rent Due Reminder",
                        message,
                        NotificationType.RENT_DUE_REMINDER,
                        NotificationPriority.HIGH);

                // 2. Send External Notification (WhatsApp)
                whatsAppNotificationService.sendNotification(
                        lease.getTenant().getPhoneNumber(),
                        message,
                        NotificationType.RENT_DUE_REMINDER);
            }
        }
    }
}
