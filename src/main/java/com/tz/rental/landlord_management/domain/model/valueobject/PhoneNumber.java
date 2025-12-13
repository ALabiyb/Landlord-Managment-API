package com.tz.rental.landlord_management.domain.model.valueobject;

import com.tz.rental.landlord_management.common.util.ValidationUtil;
import lombok.Getter;

@Getter
public class PhoneNumber {
    private final String value;

    public PhoneNumber(String value) {
        if (!ValidationUtil.isValidTanzanianPhone(value)) {
            throw new IllegalArgumentException("Invalid Tanzanian phone number: " + value);
        }
        this.value = value.trim();
    }

    public String getLocalFormat() {
        if (value.startsWith("+255")) {
            String local = "0" + value.substring(4);
            return local.replaceFirst("(\\d{4})(\\d{3})(\\d{3})", "$1 $2 $3");
        }
        return value;
    }

    public String getInternationalFormat() {
        if (value.startsWith("+")) {
            return value;
        } else if (value.startsWith("0")) {
            return "+255" + value.substring(1);
        } else if (value.startsWith("255")) {
            return "+" + value;
        }
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PhoneNumber that)) return false;
        return value.equals(that.value);
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