package com.tz.rental.landlord_management.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReminderScheduler {

    private final ReminderService reminderService;

    // Schedule to run every day at a specific time (e.g., 9 AM)
    // The cron expression "0 0 9 * * ?" means:
    // second (0) minute (0) hour (9) day-of-month (*) month (*) day-of-week (?)
    @Scheduled(cron = "0 0 9 * * ?")
    public void dailyReminderCheck() {
        log.info("Running daily reminder checks...");
        reminderService.sendRentDueReminders();
        reminderService.sendLeaseExpiryReminders();
        // reminderService.sendOverduePaymentAlerts(); // Will be enabled once implemented
        log.info("Daily reminder checks completed.");
    }
}