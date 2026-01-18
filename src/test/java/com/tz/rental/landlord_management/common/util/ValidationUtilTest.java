package com.tz.rental.landlord_management.common.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ValidationUtilTest {

    @Test
    void isValidTanzanianPhone_shouldAcceptInternationalFormat() {
        assertTrue(ValidationUtil.isValidTanzanianPhone("+255712345678"));
        assertTrue(ValidationUtil.isValidTanzanianPhone("+255687654321"));
    }

    @Test
    void isValidTanzanianPhone_shouldAcceptLocalFormat() {
        assertTrue(ValidationUtil.isValidTanzanianPhone("0712345678"));
        assertTrue(ValidationUtil.isValidTanzanianPhone("0687654321"));
    }

    @Test
    void isValidTanzanianPhone_shouldRejectInvalidFormats() {
        assertFalse(ValidationUtil.isValidTanzanianPhone("712345678")); // Missing 0 or +255
        assertFalse(ValidationUtil.isValidTanzanianPhone("+2550712345678")); // Too long
        assertFalse(ValidationUtil.isValidTanzanianPhone("07123456789")); // Too long
        assertFalse(ValidationUtil.isValidTanzanianPhone("071234567")); // Too short
        assertFalse(ValidationUtil.isValidTanzanianPhone("abc7123456")); // Non-numeric
        assertFalse(ValidationUtil.isValidTanzanianPhone(null));
    }
}
