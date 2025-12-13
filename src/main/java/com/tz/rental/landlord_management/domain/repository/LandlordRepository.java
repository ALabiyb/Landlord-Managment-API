package com.tz.rental.landlord_management.domain.repository;

import com.tz.rental.landlord_management.domain.model.aggregate.Landlord;
import com.tz.rental.landlord_management.domain.model.valueobject.Email;
import com.tz.rental.landlord_management.domain.model.valueobject.PhoneNumber;

import java.util.List;
import java.util.Optional;

public interface LandlordRepository {
    Landlord save(Landlord landlord);
    Optional<Landlord> findById(Landlord.LandlordId id);
    Optional<Landlord> findByEmail(Email email);
    Optional<Landlord> findByPhoneNumber(PhoneNumber phoneNumber);
    List<Landlord> findAll();
    void delete(Landlord.LandlordId id);
}