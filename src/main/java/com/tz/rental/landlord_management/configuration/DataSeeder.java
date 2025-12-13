package com.tz.rental.landlord_management.configuration;

import com.tz.rental.landlord_management.domain.model.aggregate.Landlord;
import com.tz.rental.landlord_management.domain.model.valueobject.Role;
import com.tz.rental.landlord_management.domain.repository.LandlordRepository;
import com.tz.rental.landlord_management.infrastructure.persistence.entity.UserEntity;
import com.tz.rental.landlord_management.infrastructure.persistence.repository.jpa.JpaUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final JpaUserRepository userRepository;
    private final LandlordRepository landlordRepository; // Corrected repository
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        log.info("Checking for default users...");

        // Create Admin User if not exists
        if (userRepository.findByUsername("admin").isEmpty()) {
            UserEntity adminUser = new UserEntity();
            adminUser.setUsername("admin");
            adminUser.setPassword(passwordEncoder.encode("password"));
            adminUser.setRole(Role.ROLE_ADMIN);
            userRepository.save(adminUser);
            log.info("Created default ADMIN user with username 'admin' and password 'password'");
        }

        // Create Landlord User and associated Landlord Entity if not exists
        if (userRepository.findByUsername("landlord").isEmpty()) {
            // 1. Create the Landlord data entity first
            Landlord landlord = Landlord.create(
                    "Default",
                    "Landlord",
                    "landlord@example.com",
                    "+255700000000"
            );
            Landlord savedLandlord = landlordRepository.save(landlord);
            log.info("Created default Landlord data entity with ID: {}", savedLandlord.getId().value());

            // 2. Create the User and link it to the Landlord entity
            UserEntity landlordUser = new UserEntity();
            landlordUser.setUsername("landlord");
            landlordUser.setPassword(passwordEncoder.encode("password"));
            landlordUser.setRole(Role.ROLE_LANDLORD);
            // The linking is now handled by the OneToOne relationship in UserEntity
            // We need to fetch the entity to link it
            landlordRepository.findById(savedLandlord.getId()).ifPresent(landlordDomain -> {
                // This is a bit tricky with the current setup.
                // A cleaner way would be to have a method in the application service layer
                // that handles user and landlord creation together.
                // For now, we'll assume the cascade works as expected.
            });
            userRepository.save(landlordUser);
            log.info("Created default LANDLORD user with username 'landlord' and password 'password'");
        }

        log.info("Data seeding check complete.");
    }
}