package com.tz.rental.landlord_management.api.rest.controller;

import com.tz.rental.landlord_management.api.rest.dto.ApiResponse;
import com.tz.rental.landlord_management.api.rest.dto.StandardErrorResponse;
import com.tz.rental.landlord_management.application.dto.*;
import com.tz.rental.landlord_management.application.service.AuthService;
import com.tz.rental.landlord_management.application.service.JwtUtil;
import com.tz.rental.landlord_management.application.service.UserDetailsServiceImpl;
import com.tz.rental.landlord_management.infrastructure.persistence.entity.LandlordEntity;
import com.tz.rental.landlord_management.infrastructure.persistence.entity.UserEntity;
import com.tz.rental.landlord_management.application.dto.UserSummary;
import com.tz.rental.landlord_management.application.dto.LandlordSummary;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Endpoints for user authentication and registration")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsServiceImpl userDetailsService;
    private final AuthService authService;
    private final JwtUtil jwtUtil;

    @PostMapping("/login")
    @Operation(summary = "Authenticate user and get JWT", description = "Authenticates a user with username and password, and returns a JWT token along with user profile details.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Successfully authenticated",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthenticationResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Invalid username or password",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = StandardErrorResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = StandardErrorResponse.class)))
    })
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword())
            );
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Incorrect username or password."));
        }

        final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUsername());
        final String jwt = jwtUtil.generateToken(userDetails);
        final String refreshToken = jwtUtil.generateRefreshToken(userDetails);

        UserEntity userEntity = (UserEntity) userDetails;
        LandlordEntity landlordEntity = userEntity.getLandlord();

        UserSummary userSummary = UserSummary.builder()
                .id(userEntity.getId())
                .username(userEntity.getUsername())
                .role(userEntity.getRole())
                .build();

        LandlordSummary landlordSummary = null;
        if (landlordEntity != null) {
            landlordSummary = LandlordSummary.builder()
                    .id(landlordEntity.getId())
                    .firstName(landlordEntity.getFirstName())
                    .lastName(landlordEntity.getLastName())
                    .email(landlordEntity.getEmail())
                    .phoneNumber(landlordEntity.getPhoneNumber())
                    .build();
        }

        return ResponseEntity.ok(AuthenticationResponse.builder()
                .token(jwt)
                .refreshToken(refreshToken)
                .user(userSummary)
                .landlord(landlordSummary)
                .build());
    }

    @PostMapping("/register/landlord")
    @Operation(summary = "Register a new landlord", description = "Creates a new user account and a corresponding landlord profile in one step.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Landlord registered successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthenticationResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Validation failed (e.g., missing fields, invalid email format)",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = StandardErrorResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Username, email, or phone number already exists",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = StandardErrorResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = StandardErrorResponse.class)))
    })
    public ResponseEntity<AuthenticationResponse> registerLandlord(@Valid @RequestBody LandlordRegistrationRequest registrationRequest) {
        UserEntity userEntity = authService.registerLandlord(registrationRequest);
        
        final UserDetails userDetails = userDetailsService.loadUserByUsername(registrationRequest.getUsername());
        final String jwt = jwtUtil.generateToken(userDetails);
        final String refreshToken = jwtUtil.generateRefreshToken(userDetails);

        UserSummary userSummary = UserSummary.builder()
                .id(userEntity.getId())
                .username(userEntity.getUsername())
                .role(userEntity.getRole())
                .build();

        LandlordEntity landlordEntity = userEntity.getLandlord();
        LandlordSummary landlordSummary = LandlordSummary.builder()
                .id(landlordEntity.getId())
                .firstName(landlordEntity.getFirstName())
                .lastName(landlordEntity.getLastName())
                .email(landlordEntity.getEmail())
                .phoneNumber(landlordEntity.getPhoneNumber())
                .build();

        AuthenticationResponse response = AuthenticationResponse.builder()
                .token(jwt)
                .refreshToken(refreshToken)
                .user(userSummary)
                .landlord(landlordSummary)
                .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh JWT token", description = "Refreshes an expired JWT token using a valid refresh token.")
    public ResponseEntity<?> refreshToken(@Valid @RequestBody RefreshTokenRequest refreshTokenRequest) {
        String refreshToken = refreshTokenRequest.getRefreshToken();
        String username = jwtUtil.extractUsername(refreshToken);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        if (jwtUtil.validateToken(refreshToken, userDetails)) {
            String newAccessToken = jwtUtil.generateToken(userDetails);
            String newRefreshToken = jwtUtil.generateRefreshToken(userDetails);

            return ResponseEntity.ok(AuthenticationResponse.builder()
                    .token(newAccessToken)
                    .refreshToken(newRefreshToken)
                    .build());
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error("Invalid refresh token."));
        }
    }

    @PostMapping("/forgot-password")
    @Operation(summary = "Request password reset", description = "Initiates the password reset process for a user.")
    public ResponseEntity<ApiResponse<String>> forgotPassword(@Valid @RequestBody ForgotPasswordRequest forgotPasswordRequest) {
        authService.forgotPassword(forgotPasswordRequest.getEmail());
        return ResponseEntity.ok(ApiResponse.success("Password reset link sent to your email."));
    }

    @PostMapping("/reset-password")
    @Operation(summary = "Reset password", description = "Resets the user's password using a valid reset token.")
    public ResponseEntity<ApiResponse<String>> resetPassword(@Valid @RequestBody ResetPasswordRequest resetPasswordRequest) {
        authService.resetPassword(resetPasswordRequest.getToken(), resetPasswordRequest.getNewPassword());
        return ResponseEntity.ok(ApiResponse.success("Password has been reset successfully."));
    }
}