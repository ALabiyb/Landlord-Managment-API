package com.tz.rental.landlord_management.common.constant;

public class ApplicationConstants {

    private ApplicationConstants() {
        // Private constructor to prevent instantiation
    }

    // =============== APPLICATION ===============
    public static final String APPLICATION_NAME = "Tanzanian Landlord Management System";
    public static final String APPLICATION_VERSION = "1.0.0";

    // =============== COUNTRY SPECIFIC ===============
    public static final String DEFAULT_COUNTRY = "Tanzania";
    public static final String DEFAULT_CURRENCY = "TZS";
    public static final String DEFAULT_TIMEZONE = "Africa/Dar_es_Salaam";
    public static final String DEFAULT_LANGUAGE = "sw";

    // =============== VALIDATION PATTERNS ===============
    public static final String PHONE_NUMBER_PATTERN = "^\\+255[0-9]{9}$";
    public static final String EMAIL_PATTERN = "^[A-Za-z0-9+_.-]+@(.+)$";
    public static final String NATIONAL_ID_PATTERN = "^[0-9]{16}$";

    // =============== HOUSE TYPES ===============
    public static final String HOUSE_TYPE_APARTMENT = "APARTMENT";
    public static final String HOUSE_TYPE_STANDALONE = "STANDALONE";
    public static final String HOUSE_TYPE_COMPLEX = "COMPLEX";

    // =============== STATUSES ===============
    public static final String STATUS_ACTIVE = "ACTIVE";
    public static final String STATUS_INACTIVE = "INACTIVE";
    public static final String STATUS_VACANT = "VACANT";
    public static final String STATUS_OCCUPIED = "OCCUPIED";
    public static final String STATUS_MAINTENANCE = "MAINTENANCE";
    public static final String STATUS_PENDING = "PENDING";

    // =============== FINANCIAL ===============
    public static final double DEFAULT_LATE_FEE_PERCENTAGE = 5.0;
    public static final int DEFAULT_GRACE_PERIOD_DAYS = 5;

    // =============== TANZANIAN REGIONS ===============
    public static final String[] TANZANIAN_REGIONS = {
            "Dar es Salaam", "Arusha", "Dodoma", "Mwanza", "Mbeya",
            "Morogoro", "Tanga", "Kigoma", "Moshi", "Tabora",
            "Songea", "Musoma", "Iringa", "Shinyanga", "Bukoba",
            "Mtwara", "Lindi", "Ruvuma", "Kagera", "Geita",
            "Simiyu", "Manyara", "Katavi", "Njombe", "Pwani"
    };

    // =============== ERROR MESSAGES ===============
    public static final String ERROR_VALIDATION_FAILED = "Validation failed";
    public static final String ERROR_NOT_FOUND = "Resource not found";
    public static final String ERROR_ALREADY_EXISTS = "Resource already exists";
    public static final String ERROR_INVALID_INPUT = "Invalid input provided";
}