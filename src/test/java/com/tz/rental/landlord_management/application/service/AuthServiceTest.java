package com.tz.rental.landlord_management.application.service;

import com.tz.rental.landlord_management.application.dto.LandlordRegistrationRequest;
import com.tz.rental.landlord_management.infrastructure.persistence.entity.LandlordEntity;
import com.tz.rental.landlord_management.infrastructure.persistence.entity.PasswordResetToken;
import com.tz.rental.landlord_management.infrastructure.persistence.entity.UserEntity;
import com.tz.rental.landlord_management.infrastructure.persistence.repository.jpa.JpaLandlordRepository;
import com.tz.rental.landlord_management.infrastructure.persistence.repository.jpa.JpaPasswordResetTokenRepository;
import com.tz.rental.landlord_management.infrastructure.persistence.repository.jpa.JpaUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private JpaUserRepository userRepository;

    @Mock
    private JpaLandlordRepository landlordRepository;

    @Mock
    private JpaPasswordResetTokenRepository passwordResetTokenRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    private LandlordRegistrationRequest registrationRequest;

    @BeforeEach
    void setUp() {
        registrationRequest = new LandlordRegistrationRequest();
        registrationRequest.setUsername("newuser");
        registrationRequest.setPassword("Password123!");
        registrationRequest.setEmail("new.user@example.com");
        registrationRequest.setPhoneNumber("+255123456789");
        registrationRequest.setFirstName("New");
        registrationRequest.setLastName("User");
        registrationRequest.setNationalId("12345678901234567890");
    }

    @Test
    void registerLandlord_shouldSucceedAndSaveUserAndLandlord() {
        // Arrange
        when(userRepository.findByUsername(registrationRequest.getUsername())).thenReturn(Optional.empty());
        when(landlordRepository.findByEmail(registrationRequest.getEmail())).thenReturn(Optional.empty());
        when(landlordRepository.findByPhoneNumber(registrationRequest.getPhoneNumber())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(registrationRequest.getPassword())).thenReturn("encodedPassword");

        // Act
        authService.registerLandlord(registrationRequest);

        // Assert
        ArgumentCaptor<UserEntity> userCaptor = ArgumentCaptor.forClass(UserEntity.class);
        verify(userRepository).save(userCaptor.capture());

        UserEntity savedUser = userCaptor.getValue();
        assertEquals("newuser", savedUser.getUsername());
        assertEquals("encodedPassword", savedUser.getPassword());
        assertNotNull(savedUser.getLandlord());
        assertEquals("New", savedUser.getLandlord().getFirstName());
    }

    @Test
    void registerLandlord_whenUsernameExists_shouldThrowException() {
        // Arrange
        when(userRepository.findByUsername(registrationRequest.getUsername())).thenReturn(Optional.of(new UserEntity()));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            authService.registerLandlord(registrationRequest);
        });
    }

    @Test
    void forgotPassword_shouldCreateAndSaveResetToken() {
        // Arrange
        LandlordEntity landlord = new LandlordEntity();
        UserEntity user = new UserEntity();
        landlord.setUser(user);
        when(landlordRepository.findByEmail("test@example.com")).thenReturn(Optional.of(landlord));

        // Act
        authService.forgotPassword("test@example.com");

        // Assert
        verify(passwordResetTokenRepository).save(any(PasswordResetToken.class));
    }

    @Test
    void resetPassword_withValidToken_shouldChangePassword() {
        // Arrange
        UserEntity user = new UserEntity();
        user.setPassword("oldPassword");
        PasswordResetToken token = new PasswordResetToken();
        token.setUser(user);
        token.setExpiryDate(LocalDateTime.now().plusHours(1));

        when(passwordResetTokenRepository.findByToken("valid_token")).thenReturn(Optional.of(token));
        when(passwordEncoder.encode("newPassword")).thenReturn("encodedNewPassword");

        // Act
        authService.resetPassword("valid_token", "newPassword");

        // Assert
        ArgumentCaptor<UserEntity> userCaptor = ArgumentCaptor.forClass(UserEntity.class);
        verify(userRepository).save(userCaptor.capture());
        assertEquals("encodedNewPassword", userCaptor.getValue().getPassword());
        verify(passwordResetTokenRepository).delete(token);
    }

    @Test
    void resetPassword_withExpiredToken_shouldThrowException() {
        // Arrange
        PasswordResetToken token = new PasswordResetToken();
        token.setExpiryDate(LocalDateTime.now().minusHours(1));
        when(passwordResetTokenRepository.findByToken("expired_token")).thenReturn(Optional.of(token));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            authService.resetPassword("expired_token", "newPassword");
        });
    }
}