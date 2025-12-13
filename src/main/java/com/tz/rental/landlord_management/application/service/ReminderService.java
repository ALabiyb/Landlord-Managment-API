package com.tz.rental.landlord_management.application.service;

import com.tz.rental.landlord_management.domain.model.aggregate.Lease;
import com.tz.rental.landlord_management.domain.model.aggregate.Tenant;
import com.tz.rental.landlord_management.domain.model.valueobject.LeaseStatus;
import com.tz.rental.landlord_management.domain.model.valueobject.NotificationType;
import com.tz.rental.landlord_management.domain.repository.LeaseRepository;
import com.tz.rental.landlord_management.domain.repository.TenantRepository;
import com.tz.rental.landlord_management.domain.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReminderService {

    private final LeaseRepository leaseRepository;
    private final TenantRepository tenantRepository;
    private final NotificationService notificationService; // Injected WhatsAppNotificationService

    // Configuration for reminder days (can be moved to application.properties)
    private final int RENT_DUE_REMINDER_DAYS_BEFORE = 5;
    private final int LEASE_EXPIRY_REMINDER_DAYS_BEFORE = 30;

    public void sendRentDueReminders() {
        log.info("Checking for rent due reminders...");
        LocalDate today = LocalDate.now();
        LocalDate reminderDate = today.plusDays(RENT_DUE_REMINDER_DAYS_BEFORE);

        // This would ideally query for leases with rent due around 'reminderDate'
        // For simplicity, we'll iterate through all active leases for now.
        // A more robust solution would involve a dedicated rent schedule.
        List<Lease> activeLeases = leaseRepository.findAll().stream()
                .filter(lease -> lease.getStatus() == LeaseStatus.ACTIVE || lease.getStatus() == LeaseStatus.UPCOMING)
                .collect(Collectors.toList());

        for (Lease lease : activeLeases) {
            // Simplified logic: assuming monthly payments and checking if next payment is due soon
            // A real system would have a more complex payment schedule calculation
            if (lease.getStartDate().isBefore(reminderDate) && lease.getEndDate().isAfter(reminderDate)) {
                // This is a very basic check. A full system would calculate the exact next due date.
                // For now, if the lease is active and within the reminder window, we send a reminder.

                tenantRepository.findById(lease.getTenantId().value()).ifPresent(tenant -> {
                    String message = String.format("Hi %s, your rent of %.2f TZS for room %s is due on %s.",
                            tenant.getFirstName(), lease.getRentAmount(), lease.getRoomId().value(), reminderDate);
                    notificationService.sendNotification(tenant.getPhoneNumber().getValue(), message, NotificationType.RENT_DUE_REMINDER);
                    log.info("Sent rent due reminder for Lease ID: {} to Tenant ID: {}", lease.getId().value(), tenant.getId().value());
                });
            }
        }
        log.info("Finished checking for rent due reminders.");
    }

    public void sendLeaseExpiryReminders() {
        log.info("Checking for lease expiry reminders...");
        LocalDate today = LocalDate.now();
        LocalDate expiryReminderDate = today.plusDays(LEASE_EXPIRY_REMINDER_DAYS_BEFORE);

        List<Lease> expiringLeases = leaseRepository.findAll().stream()
                .filter(lease -> lease.getStatus() == LeaseStatus.ACTIVE && lease.getEndDate().isEqual(expiryReminderDate))
                .collect(Collectors.toList());

        for (Lease lease : expiringLeases) {
            tenantRepository.findById(lease.getTenantId().value()).ifPresent(tenant -> {
                String message = String.format("Hi %s, your lease for room %s is expiring on %s. Please contact us to renew.",
                        tenant.getFirstName(), lease.getRoomId().value(), lease.getEndDate());
                notificationService.sendNotification(tenant.getPhoneNumber().getValue(), message, NotificationType.LEASE_EXPIRY_REMINDER);
                log.info("Sent lease expiry reminder for Lease ID: {} to Tenant ID: {}", lease.getId().value(), tenant.getId().value());
            });
        }
        log.info("Finished checking for lease expiry reminders.");
    }

    // Placeholder for overdue payment reminders (requires payment tracking logic)
    public void sendOverduePaymentAlerts() {
        log.info("Checking for overdue payment alerts... (Not yet implemented)");
        // This would require payment tracking and due date calculation logic
    }
}