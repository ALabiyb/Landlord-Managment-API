package com.tz.rental.landlord_management.application.service;

import com.tz.rental.landlord_management.application.dto.CreateLandlordRequest;
import com.tz.rental.landlord_management.application.dto.LandlordResponse;
import com.tz.rental.landlord_management.application.mapper.ApplicationLandlordMapper; // Corrected import
import com.tz.rental.landlord_management.domain.exception.AlreadyExistsException;
import com.tz.rental.landlord_management.domain.exception.NotFoundException;
import com.tz.rental.landlord_management.domain.exception.UnauthorizedException;
import com.tz.rental.landlord_management.domain.model.aggregate.Landlord;
import com.tz.rental.landlord_management.domain.model.valueobject.Email;
import com.tz.rental.landlord_management.domain.model.valueobject.PhoneNumber;
import com.tz.rental.landlord_management.domain.model.valueobject.Role;
import com.tz.rental.landlord_management.domain.repository.LandlordRepository;
import com.tz.rental.landlord_management.infrastructure.persistence.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class LandlordService {

    private final LandlordRepository landlordRepository;
    private final ApplicationLandlordMapper applicationLandlordMapper; // Injected application mapper

    private UserEntity getCurrentUser() {
        return (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    @Transactional
    public LandlordResponse createLandlord(CreateLandlordRequest request) {
        log.info("Creating landlord: {} {}", request.getFirstName(), request.getLastName());

        if (landlordRepository.findByEmail(new Email(request.getEmail())).isPresent() ||
                landlordRepository.findByPhoneNumber(new PhoneNumber(request.getPhoneNumber())).isPresent()) {
            throw new AlreadyExistsException("Landlord", "email=" + request.getEmail() + " or phone=" + request.getPhoneNumber());
        }

        Landlord landlord = Landlord.create(
                request.getFirstName(),
                request.getLastName(),
                request.getEmail(),
                request.getPhoneNumber()
        );
        landlord.updateIdentity(request.getNationalId(), request.getTaxId()); // Corrected method call

        Landlord savedLandlord = landlordRepository.save(landlord);
        log.info("Created landlord with ID: {}", savedLandlord.getId().value());

        return applicationLandlordMapper.toResponse(savedLandlord);
    }

    public LandlordResponse getLandlord(UUID id) {
        log.info("Getting landlord by ID: {}", id);
        UserEntity currentUser = getCurrentUser();
        Landlord.LandlordId landlordId = new Landlord.LandlordId(id);

        // Security Check: Allow ADMIN to see any landlord, but LANDLORD can only see themselves.
        if (currentUser.getRole() == Role.ROLE_LANDLORD) {
            if (currentUser.getLandlord() == null || !currentUser.getLandlord().getId().equals(id)) {
                throw new UnauthorizedException("You are not authorized to view this landlord's profile.");
            }
        }

        Landlord landlord = landlordRepository.findById(landlordId)
                .orElseThrow(() -> new NotFoundException("Landlord", id));
        return applicationLandlordMapper.toResponse(landlord);
    }

    public List<LandlordResponse> getAllLandlords() {
        log.info("Getting all landlords");
        UserEntity currentUser = getCurrentUser();

        // Security Check: Only ADMIN can see all landlords.
        if (currentUser.getRole() != Role.ROLE_ADMIN) {
            throw new UnauthorizedException("You are not authorized to view all landlords.");
        }

        List<Landlord> landlords = landlordRepository.findAll();
        return landlords.stream()
                .map(applicationLandlordMapper::toResponse)
                .toList();
    }

    @Transactional
    public LandlordResponse updateLandlord(UUID id, CreateLandlordRequest request) {
        log.info("Updating landlord ID: {}", id);
        UserEntity currentUser = getCurrentUser();
        Landlord.LandlordId landlordId = new Landlord.LandlordId(id);

        // Security Check: A landlord can only update their own profile.
        if (currentUser.getRole() == Role.ROLE_LANDLORD) {
            if (currentUser.getLandlord() == null || !currentUser.getLandlord().getId().equals(id)) {
                throw new UnauthorizedException("You are not authorized to update this landlord's profile.");
            }
        }

        Landlord landlord = landlordRepository.findById(landlordId)
                .orElseThrow(() -> new NotFoundException("Landlord", id));

        landlord.updateContactInfo(request.getEmail(), request.getPhoneNumber());
        landlord.updateIdentity(request.getNationalId(), request.getTaxId()); // Corrected method call
        landlord.updatePersonalInfo(request.getFirstName(), request.getLastName());

        Landlord updatedLandlord = landlordRepository.save(landlord);
        return applicationLandlordMapper.toResponse(updatedLandlord);
    }

    @Transactional
    public void deleteLandlord(UUID id) {
        log.info("Deleting landlord ID: {}", id);
        // This should be an ADMIN only operation.
        UserEntity currentUser = getCurrentUser();
        if (currentUser.getRole() != Role.ROLE_ADMIN) {
            throw new UnauthorizedException("Only administrators can delete landlords.");
        }

        Landlord.LandlordId landlordId = new Landlord.LandlordId(id);
        if (landlordRepository.findById(landlordId).isEmpty()) {
            throw new NotFoundException("Landlord", id);
        }
        landlordRepository.delete(landlordId);
    }

    @Transactional
    public LandlordResponse activateLandlord(UUID id) {
        log.info("Activating landlord ID: {}", id);
        Landlord landlord = landlordRepository.findById(new Landlord.LandlordId(id))
                .orElseThrow(() -> new NotFoundException("Landlord", id));
        landlord.activate();
        return applicationLandlordMapper.toResponse(landlordRepository.save(landlord));
    }

    @Transactional
    public LandlordResponse deactivateLandlord(UUID id) {
        log.info("Deactivating landlord ID: {}", id);
        Landlord landlord = landlordRepository.findById(new Landlord.LandlordId(id))
                .orElseThrow(() -> new NotFoundException("Landlord", id));
        landlord.deactivate();
        return applicationLandlordMapper.toResponse(landlordRepository.save(landlord));
    }
}