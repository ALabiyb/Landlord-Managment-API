package com.tz.rental.landlord_management.domain.exception;

public class NotFoundException extends DomainException {
    public NotFoundException(String resource, Object identifier) {
        super(String.format("%s not found with identifier: %s", resource, identifier));
    }

    public NotFoundException(String message) {
        super(message);
    }
}