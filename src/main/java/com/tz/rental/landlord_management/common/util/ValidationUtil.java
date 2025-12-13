package com.tz.rental.landlord_management.common.util;

import com.tz.rental.landlord_management.common.constant.ApplicationConstants;

import java.util.Arrays;

public class ValidationUtil {

    private ValidationUtil() {
        // Private constructor
    }

    public static boolean isValidTanzanianPhone(String phone) {
        if (phone == null) return false;
        return phone.matches(ApplicationConstants.PHONE_NUMBER_PATTERN);
    }

    public static boolean isValidEmail(String email) {
        if (email == null) return false;
        return email.matches(ApplicationConstants.EMAIL_PATTERN);
    }

    public static boolean isValidNationalId(String nationalId) {
        if (nationalId == null) return false;
        return nationalId.matches(ApplicationConstants.NATIONAL_ID_PATTERN);
    }

    public static boolean isValidTanzanianRegion(String region) {
        if (region == null) return false;
        return Arrays.stream(ApplicationConstants.TANZANIAN_REGIONS)
                .anyMatch(r -> r.equalsIgnoreCase(region.trim()));
    }

    public static boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    public static boolean isNullOrBlank(String str) {
        return str == null || str.isBlank();
    }

    public static String capitalizeWords(String str) {
        if (isNullOrEmpty(str)) return str;

        String[] words = str.split("\\s+");
        StringBuilder result = new StringBuilder();

        for (String word : words) {
            if (!word.isEmpty()) {
                result.append(Character.toUpperCase(word.charAt(0)))
                        .append(word.substring(1).toLowerCase())
                        .append(" ");
            }
        }

        return result.toString().trim();
    }

    public static String formatPhoneForDisplay(String phone) {
        if (phone == null) return null;

        // Convert +255712345678 to 0712 345 678
        if (phone.startsWith("+255")) {
            String local = "0" + phone.substring(4);
            if (local.length() == 10) {
                return local.replaceFirst("(\\d{4})(\\d{3})(\\d{3})", "$1 $2 $3");
            }
        }
        return phone;
    }

    public static String formatCurrency(double amount) {
        return String.format("TZS %,.2f", amount);
    }
}