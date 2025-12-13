package com.tz.rental.landlord_management.common.util;

public class StringUtil {

    private StringUtil() {
        // Private constructor
    }

    public static String generatePropertyCode(String region, int sequence) {
        if (region == null || region.length() < 3) {
            region = "TZ";
        }
        String regionCode = region.substring(0, Math.min(3, region.length())).toUpperCase();
        return String.format("PROP-%s-%04d", regionCode, sequence);
    }

    public static String generateContractNumber(String prefix) {
        String timestamp = String.valueOf(System.currentTimeMillis() % 1000000);
        return String.format("%s-%s-%s",
                prefix,
                timestamp,
                java.util.UUID.randomUUID().toString().substring(0, 4).toUpperCase());
    }

    public static String trimToLength(String str, int maxLength) {
        if (str == null) return null;
        if (str.length() <= maxLength) return str;
        return str.substring(0, maxLength - 3) + "...";
    }
}