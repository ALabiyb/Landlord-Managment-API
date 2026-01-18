package com.tz.rental.landlord_management.application.service;

import com.tz.rental.landlord_management.application.dto.LandlordRegistrationRequest;
import com.tz.rental.landlord_management.domain.exception.AccountLockedException;
import com.tz.rental.landlord_management.domain.model.valueobject.Role;
import com.tz.rental.landlord_management.infrastructure.persistence.entity.LandlordEntity;
import com.tz.rental.landlord_management.infrastructure.persistence.entity.PasswordResetToken;
import com.tz.rental.landlord_management.infrastructure.persistence.entity.UserEntity;
import com.tz.rental.landlord_management.infrastructure.persistence.repository.jpa.JpaLandlordRepository;
import com.tz.rental.landlord_management.infrastructure.persistence.repository.jpa.JpaPasswordResetTokenRepository;
import com.tz.rental.landlord_management.infrastructure.persistence.repository.jpa.JpaUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final JpaUserRepository userRepository;
    private final JpaLandlordRepository landlordRepository;
    private final JpaPasswordResetTokenRepository passwordResetTokenRepository;
    private final PasswordEncoder passwordEncoder;

    // Account lockout configuration
    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final Duration LOCKOUT_DURATION = Duration.ofMinutes(30);

    // =====================================================
    // Account Lockout Methods
    // =====================================================

    /**
     * Check if the user account is currently locked.
     * Auto-unlocks if the lockout duration has passed.
     *
     * @param user The user entity to check
     * @return true if account is locked, false otherwise
     */
    public boolean isAccountLocked(UserEntity user) {
        if (user.getLockoutTime() == null) {
            return false;
        }

        LocalDateTime unlockTime = user.getLockoutTime().plus(LOCKOUT_DURATION);
        if (LocalDateTime.now().isAfter(unlockTime)) {
            // Lockout period has expired, auto-unlock
            unlockAccount(user.getUsername());
            return false;
        }

        return true;
    }

    /**
     * Check if the user account is currently locked by username.
     *
     * @param username The username to check
     * @return true if account is locked, false otherwise
     */
    public boolean isAccountLocked(String username) {
        return userRepository.findByUsername(username)
                .map(this::isAccountLocked)
                .orElse(false);
    }

    /**
     * Record a failed login attempt and lock the account if threshold is reached.
     *
     * @param username The username of the user who failed to login
     */
    @Transactional
    public void recordFailedLoginAttempt(String username) {
        userRepository.findByUsername(username).ifPresent(user -> {
            int attempts = user.getFailedLoginAttempts() == null ? 0 : user.getFailedLoginAttempts();
            attempts++;
            user.setFailedLoginAttempts(attempts);

            if (attempts >= MAX_FAILED_ATTEMPTS) {
                user.setLockoutTime(LocalDateTime.now());
                user.setAccountNonLocked(false);
                log.warn("Account locked for user {} after {} failed attempts", username, attempts);
            } else {
                log.info("Failed login attempt {} of {} for user {}", attempts, MAX_FAILED_ATTEMPTS, username);
            }

            userRepository.save(user);
        });
    }

    /**
     * Reset failed login attempts counter on successful login.
     *
     * @param username The username of the user who successfully logged in
     */
    @Transactional
    public void resetFailedLoginAttempts(String username) {
        userRepository.findByUsername(username).ifPresent(user -> {
            if (user.getFailedLoginAttempts() != null && user.getFailedLoginAttempts() > 0) {
                user.setFailedLoginAttempts(0);
                user.setLockoutTime(null);
                user.setAccountNonLocked(true);
                userRepository.save(user);
                log.info("Reset failed login attempts for user {}", username);
            }
        });
    }

    /**
     * Manually unlock a user account.
     *
     * @param username The username of the user to unlock
     */
    @Transactional
    public void unlockAccount(String username) {
        userRepository.findByUsername(username).ifPresent(user -> {
            user.setFailedLoginAttempts(0);
            user.setLockoutTime(null);
            user.setAccountNonLocked(true);
            userRepository.save(user);
            log.info("Account unlocked for user {}", username);
        });
    }

    /**
     * Check if account is locked and throw exception if it is.
     *
     * @param user The user to check
     * @throws AccountLockedException if the account is locked
     */
    public void checkAccountLockStatus(UserEntity user) {
        if (isAccountLocked(user)) {
            throw new AccountLockedException(user.getLockoutTime(), LOCKOUT_DURATION);
        }
    }

    // =====================================================
    // Registration and Password Reset Methods
    // =====================================================

    @Transactional
    public UserEntity registerLandlord(LandlordRegistrationRequest request) {
        // Check for existing username, email, etc.
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username is already taken.");
        }
        if (landlordRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email is already in use.");
        }
        if (landlordRepository.findByPhoneNumber(request.getPhoneNumber()).isPresent()) {
            throw new IllegalArgumentException("Phone number is already in use.");
        }

        // 1. Create the Landlord data entity
        LandlordEntity landlordEntity = new LandlordEntity();
        landlordEntity.setId(UUID.randomUUID());
        landlordEntity.setFirstName(request.getFirstName());
        landlordEntity.setLastName(request.getLastName());
        landlordEntity.setEmail(request.getEmail());
        landlordEntity.setPhoneNumber(request.getPhoneNumber());
        landlordEntity.setNationalId(request.getNationalId());
        // Don't save it yet!

        // 2. Create the User and link it to the Landlord entity
        UserEntity newUser = new UserEntity();
        newUser.setUsername(request.getUsername());
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));
        newUser.setRole(Role.ROLE_LANDLORD);
        newUser.setLandlord(landlordEntity); // Link the user to the landlord entity

        // 3. Save the User. CascadeType.ALL will save the new LandlordEntity.
        return userRepository.save(newUser);
    }

    @Transactional
    public void forgotPassword(String email) {
        LandlordEntity landlord = landlordRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User with email " + email + " not found."));
        UserEntity user = userRepository.findByLandlord(landlord)
                .orElseThrow(
                        () -> new IllegalArgumentException("User for landlord with email " + email + " not found."));

        String token = UUID.randomUUID().toString();
        PasswordResetToken passwordResetToken = new PasswordResetToken(token, user);
        passwordResetTokenRepository.save(passwordResetToken);

        // Simulate sending email
        log.info("Password reset token for user {}: {}", user.getUsername(), token);
    }

    @Transactional
    public void resetPassword(String token, String newPassword) {
        PasswordResetToken passwordResetToken = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid password reset token."));

        if (passwordResetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Password reset token has expired.");
        }

        UserEntity user = passwordResetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        passwordResetTokenRepository.delete(passwordResetToken);
    }
}