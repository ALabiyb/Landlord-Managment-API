package com.tz.rental.landlord_management.domain.exception;

import java.time.LocalDateTime;
import java.time.Duration;

/**
 * Exception thrown when a user account is locked due to multiple failed login
 * attempts.
 * The account will be automatically unlocked after a specified duration (30
 * minutes).
 */
public class AccountLockedException extends RuntimeException {

    private final LocalDateTime lockoutTime;
    private final Duration lockoutDuration;

    public AccountLockedException(LocalDateTime lockoutTime, Duration lockoutDuration) {
        super(buildMessage(lockoutTime, lockoutDuration));
        this.lockoutTime = lockoutTime;
        this.lockoutDuration = lockoutDuration;
    }

    private static String buildMessage(LocalDateTime lockoutTime, Duration lockoutDuration) {
        LocalDateTime unlockTime = lockoutTime.plus(lockoutDuration);
        long minutesRemaining = Duration.between(LocalDateTime.now(), unlockTime).toMinutes();

        if (minutesRemaining <= 0) {
            return "Account is locked. Please try again.";
        }

        return String.format(
                "Account is locked due to multiple failed login attempts. " +
                        "Please try again in %d minutes.",
                minutesRemaining);
    }

    public LocalDateTime getLockoutTime() {
        return lockoutTime;
    }

    public Duration getLockoutDuration() {
        return lockoutDuration;
    }

    public LocalDateTime getUnlockTime() {
        return lockoutTime.plus(lockoutDuration);
    }
}
