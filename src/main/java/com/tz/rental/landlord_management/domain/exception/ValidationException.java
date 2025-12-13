package com.tz.rental.landlord_management.domain.exception;

public class ValidationException extends DomainException {
    public ValidationException(String message) {
        super(message);
    }
}