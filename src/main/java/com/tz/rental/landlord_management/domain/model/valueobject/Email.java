package com.tz.rental.landlord_management.domain.model.valueobject;

import com.tz.rental.landlord_management.common.util.ValidationUtil;
import lombok.Getter;

@Getter
public class Email {
    private final String value;

    public Email(String value) {
        if (!ValidationUtil.isValidEmail(value)) {
            throw new IllegalArgumentException("Invalid email address: " + value);
        }
        this.value = value.toLowerCase().trim();
    }

    public String getDomain() {
        int atIndex = value.indexOf('@');
        return atIndex > 0 ? value.substring(atIndex + 1) : "";
    }

    public String getLocalPart() {
        int atIndex = value.indexOf('@');
        return atIndex > 0 ? value.substring(0, atIndex) : value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Email email)) return false;
        return value.equals(email.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return value;
    }
}