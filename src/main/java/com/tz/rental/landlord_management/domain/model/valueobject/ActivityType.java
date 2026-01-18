package com.tz.rental.landlord_management.domain.model.valueobject;

/**
 * Enumeration of activity types for logging purposes.
 */
public enum ActivityType {
    // Authentication
    LOGIN_SUCCESS,
    LOGIN_FAILED,
    ACCOUNT_LOCKED,

    // Lease Management
    LEASE_CREATED,
    LEASE_UPDATED,
    LEASE_TERMINATED,
    LEASE_EXPIRED,
    CONTRACT_GENERATED,
    CONTRACT_SHARED,

    // Payment Management
    PAYMENT_RECORDED,
    PAYMENT_UPDATED,

    // Property Management
    PROPERTY_CREATED,
    PROPERTY_UPDATED,
    ROOM_CREATED,
    ROOM_UPDATED,

    // Tenant Management
    TENANT_REGISTERED,
    TENANT_UPDATED,

    // System
    SYSTEM_ERROR
}
