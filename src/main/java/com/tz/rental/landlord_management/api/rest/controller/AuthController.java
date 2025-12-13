package com.tz.rental.landlord_management.api.rest.controller;

import com.tz.rental.landlord_management.api.rest.dto.ApiResponse; // Added import
import com.tz.rental.landlord_management.application.dto.AuthenticationRequest;
import com.tz.rental.landlord_management.application.dto.AuthenticationResponse;
import com.tz.rental.landlord_management.application.dto.LandlordRegistrationRequest;
import com.tz.rental.landlord_management.application.service.AuthService;
import com.tz.rental.landlord_management.application.service.JwtUtil;
import com.tz.rental.landlord_management.application.service.UserDetailsServiceImpl;
import com.tz.rental.landlord_management.infrastructure.persistence.entity.UserEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus; // Added import
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

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
    @Operation(summary = "Authenticate user and get JWT", description = "Authenticates a user with username and password, and returns a JWT token upon success.")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest) { // Removed 'throws Exception'
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword())
            );
        } catch (BadCredentialsException e) {
            // Return 401 Unauthorized for bad credentials
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Incorrect username or password."));
        }

        final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUsername());
        final String jwt = jwtUtil.generateToken(userDetails);

        UserEntity userEntity = (UserEntity) userDetails;
        UUID landlordId = (userEntity.getLandlord() != null) ? userEntity.getLandlord().getId() : null;

        return ResponseEntity.ok(AuthenticationResponse.builder()
                .jwt(jwt)
                .username(userEntity.getUsername())
                .role(userEntity.getRole())
                .userId(userEntity.getId())
                .landlordId(landlordId)
                .build());
    }

    @PostMapping("/register/landlord")
    @Operation(summary = "Register a new landlord", description = "Creates a new user account and a corresponding landlord profile in one step.")
    public ResponseEntity<String> registerLandlord(@Valid @RequestBody LandlordRegistrationRequest registrationRequest) {
        authService.registerLandlord(registrationRequest);
        return ResponseEntity.ok("Landlord registered successfully. You can now log in.");
    }
}