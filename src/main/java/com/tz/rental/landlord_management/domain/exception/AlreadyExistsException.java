package com.tz.rental.landlord_management.domain.exception;

public class AlreadyExistsException extends DomainException {
    public AlreadyExistsException(String resource, String identifier) {
        super(String.format("%s already exists with identifier: %s", resource, identifier));
    }

    public AlreadyExistsException(String message) {
        super(message);
    }
}