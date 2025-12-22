package com.tz.rental.landlord_management.application.service;

import com.tz.rental.landlord_management.application.dto.LandlordRegistrationRequest;
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
                .orElseThrow(() -> new IllegalArgumentException("User for landlord with email " + email + " not found."));

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