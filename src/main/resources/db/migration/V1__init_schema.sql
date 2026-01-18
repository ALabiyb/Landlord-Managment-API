-- =====================================================
-- Tanzania Landlord Management System
-- Initial Schema Migration
-- Version: V1
-- Description: Creates all base tables for the system
-- =====================================================

-- =====================================================
-- 1. USERS TABLE (Authentication & Authorization)
-- =====================================================
CREATE TABLE IF NOT EXISTS users (
    id UUID PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,
    landlord_id UUID,
    is_account_non_expired BOOLEAN NOT NULL DEFAULT TRUE,
    is_account_non_locked BOOLEAN NOT NULL DEFAULT TRUE,
    is_credentials_non_expired BOOLEAN NOT NULL DEFAULT TRUE,
    is_enabled BOOLEAN NOT NULL DEFAULT TRUE,
    failed_login_attempts INTEGER NOT NULL DEFAULT 0,
    lockout_time TIMESTAMP,
    CONSTRAINT chk_role CHECK (role IN ('ROLE_LANDLORD', 'ROLE_ADMIN', 'ROLE_TENANT'))
);

CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_landlord_id ON users(landlord_id);

-- =====================================================
-- 2. LANDLORDS TABLE
-- =====================================================
CREATE TABLE IF NOT EXISTS landlords (
    id UUID PRIMARY KEY,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    phone_number VARCHAR(20) NOT NULL UNIQUE,
    national_id VARCHAR(50) UNIQUE,
    tax_id VARCHAR(50),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_landlords_email ON landlords(email);
CREATE INDEX idx_landlords_phone ON landlords(phone_number);
CREATE INDEX idx_landlords_active ON landlords(is_active);

-- Add foreign key constraint from users to landlords
ALTER TABLE users ADD CONSTRAINT fk_users_landlord
    FOREIGN KEY (landlord_id) REFERENCES landlords(id) ON DELETE CASCADE;

-- =====================================================
-- 3. PASSWORD RESET TOKENS TABLE
-- =====================================================
CREATE TABLE IF NOT EXISTS password_reset_token (
    id UUID PRIMARY KEY,
    token VARCHAR(255) NOT NULL UNIQUE,
    user_id UUID NOT NULL,
    expiry_date TIMESTAMP NOT NULL,
    CONSTRAINT fk_password_reset_user FOREIGN KEY (user_id)
        REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX idx_password_reset_token ON password_reset_token(token);
CREATE INDEX idx_password_reset_user ON password_reset_token(user_id);

-- =====================================================
-- 4. HOUSES TABLE
-- =====================================================
CREATE TABLE IF NOT EXISTS houses (
    id UUID PRIMARY KEY,
    property_code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    house_type VARCHAR(50) NOT NULL,
    landlord_id UUID NOT NULL,
    street_address VARCHAR(255),
    district VARCHAR(100),
    region VARCHAR(100),
    country VARCHAR(100),
    total_floors INTEGER,
    year_built INTEGER,
    has_parking BOOLEAN,
    has_security BOOLEAN,
    has_water BOOLEAN,
    has_electricity BOOLEAN,
    monthly_common_charges DECIMAL(19, 2),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_houses_landlord FOREIGN KEY (landlord_id)
        REFERENCES landlords(id) ON DELETE CASCADE,
    CONSTRAINT chk_house_type CHECK (house_type IN ('STANDALONE', 'APARTMENT', 'DUPLEX', 'VILLA', 'TOWNHOUSE', 'STUDIO'))
);

CREATE INDEX idx_houses_landlord ON houses(landlord_id);
CREATE INDEX idx_houses_property_code ON houses(property_code);
CREATE INDEX idx_houses_type ON houses(house_type);

-- =====================================================
-- 5. HOUSE IMAGE URLS TABLE (Collection Table)
-- =====================================================
CREATE TABLE IF NOT EXISTS house_image_urls (
    house_id UUID NOT NULL,
    image_url VARCHAR(500) NOT NULL,
    CONSTRAINT fk_house_images_house FOREIGN KEY (house_id)
        REFERENCES houses(id) ON DELETE CASCADE
);

CREATE INDEX idx_house_images_house ON house_image_urls(house_id);

-- =====================================================
-- 6. ROOMS TABLE
-- =====================================================
CREATE TABLE IF NOT EXISTS rooms (
    id UUID PRIMARY KEY,
    house_id UUID NOT NULL,
    room_number VARCHAR(50) NOT NULL,
    description TEXT,
    monthly_rent DECIMAL(19, 2) NOT NULL,
    status VARCHAR(50) NOT NULL,
    size VARCHAR(50),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_rooms_house FOREIGN KEY (house_id)
        REFERENCES houses(id) ON DELETE CASCADE,
    CONSTRAINT chk_room_status CHECK (status IN ('VACANT', 'OCCUPIED', 'MAINTENANCE')),
    CONSTRAINT chk_monthly_rent CHECK (monthly_rent >= 0)
);

CREATE INDEX idx_rooms_house ON rooms(house_id);
CREATE INDEX idx_rooms_status ON rooms(status);
CREATE INDEX idx_rooms_house_number ON rooms(house_id, room_number);

-- =====================================================
-- 7. ROOM IMAGE URLS TABLE (Collection Table)
-- =====================================================
CREATE TABLE IF NOT EXISTS room_image_urls (
    room_id UUID NOT NULL,
    image_url VARCHAR(500) NOT NULL,
    CONSTRAINT fk_room_images_room FOREIGN KEY (room_id)
        REFERENCES rooms(id) ON DELETE CASCADE
);

CREATE INDEX idx_room_images_room ON room_image_urls(room_id);

-- =====================================================
-- 8. TENANTS TABLE
-- =====================================================
CREATE TABLE IF NOT EXISTS tenants (
    id UUID PRIMARY KEY,
    landlord_id UUID NOT NULL,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    phone_number VARCHAR(20) NOT NULL UNIQUE,
    national_id VARCHAR(50) UNIQUE,
    emergency_contact_name VARCHAR(255),
    emergency_contact_phone VARCHAR(20),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_tenants_landlord FOREIGN KEY (landlord_id)
        REFERENCES landlords(id) ON DELETE CASCADE
);

CREATE INDEX idx_tenants_landlord ON tenants(landlord_id);
CREATE INDEX idx_tenants_email ON tenants(email);
CREATE INDEX idx_tenants_phone ON tenants(phone_number);
CREATE INDEX idx_tenants_active ON tenants(active);

-- =====================================================
-- 9. LEASES TABLE
-- =====================================================
CREATE TABLE IF NOT EXISTS leases (
    id UUID PRIMARY KEY,
    tenant_id UUID NOT NULL,
    room_id UUID NOT NULL UNIQUE,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    rent_amount DECIMAL(19, 2) NOT NULL,
    payment_period VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL,
    contract_document_url VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_leases_tenant FOREIGN KEY (tenant_id)
        REFERENCES tenants(id) ON DELETE CASCADE,
    CONSTRAINT fk_leases_room FOREIGN KEY (room_id)
        REFERENCES rooms(id) ON DELETE CASCADE,
    CONSTRAINT chk_lease_dates CHECK (end_date > start_date),
    CONSTRAINT chk_rent_amount CHECK (rent_amount > 0),
    CONSTRAINT chk_payment_period CHECK (payment_period IN ('MONTHLY', 'QUARTERLY', 'YEARLY')),
    CONSTRAINT chk_lease_status CHECK (status IN ('ACTIVE', 'EXPIRED', 'TERMINATED', 'PENDING'))
);

CREATE INDEX idx_leases_tenant ON leases(tenant_id);
CREATE INDEX idx_leases_room ON leases(room_id);
CREATE INDEX idx_leases_status ON leases(status);
CREATE INDEX idx_leases_dates ON leases(start_date, end_date);

-- =====================================================
-- 10. PAYMENTS TABLE
-- =====================================================
CREATE TABLE IF NOT EXISTS payments (
    id UUID PRIMARY KEY,
    lease_id UUID NOT NULL,
    amount_paid DECIMAL(19, 2) NOT NULL,
    payment_date DATE NOT NULL,
    due_date DATE NOT NULL,
    status VARCHAR(50) NOT NULL,
    transaction_reference VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_payments_lease FOREIGN KEY (lease_id)
        REFERENCES leases(id) ON DELETE CASCADE,
    CONSTRAINT chk_amount_paid CHECK (amount_paid >= 0),
    CONSTRAINT chk_payment_status CHECK (status IN ('PENDING', 'PAID', 'OVERDUE', 'PARTIAL', 'CANCELLED'))
);

CREATE INDEX idx_payments_lease ON payments(lease_id);
CREATE INDEX idx_payments_status ON payments(status);
CREATE INDEX idx_payments_dates ON payments(payment_date, due_date);
CREATE INDEX idx_payments_due_date ON payments(due_date);

-- =====================================================
-- 11. CONTRACT TEMPLATES TABLE
-- =====================================================
CREATE TABLE IF NOT EXISTS contract_templates (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    content TEXT NOT NULL,
    description TEXT,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_contract_templates_active ON contract_templates(is_active);

-- =====================================================
-- 12. ACTIVITY LOGS TABLE (NEW)
-- =====================================================
CREATE TABLE IF NOT EXISTS activity_logs (
    id UUID PRIMARY KEY,
    landlord_id UUID,
    activity_type VARCHAR(50) NOT NULL,
    description TEXT NOT NULL,
    entity_id UUID,
    entity_type VARCHAR(100),
    ip_address VARCHAR(50),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_activity_logs_landlord FOREIGN KEY (landlord_id)
        REFERENCES landlords(id) ON DELETE SET NULL
);

CREATE INDEX idx_activity_logs_landlord ON activity_logs(landlord_id);
CREATE INDEX idx_activity_logs_type ON activity_logs(activity_type);
CREATE INDEX idx_activity_logs_created ON activity_logs(created_at DESC);

-- =====================================================
-- 13. NOTIFICATIONS TABLE (NEW)
-- =====================================================
CREATE TABLE IF NOT EXISTS notifications (
    id UUID PRIMARY KEY,
    landlord_id UUID,
    tenant_id UUID,
    type VARCHAR(50) NOT NULL,
    priority VARCHAR(50) NOT NULL,
    title VARCHAR(255) NOT NULL,
    message TEXT NOT NULL,
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    CONSTRAINT fk_notifications_landlord FOREIGN KEY (landlord_id)
        REFERENCES landlords(id) ON DELETE CASCADE,
    CONSTRAINT fk_notifications_tenant FOREIGN KEY (tenant_id)
        REFERENCES tenants(id) ON DELETE CASCADE
);

CREATE INDEX idx_notifications_landlord ON notifications(landlord_id);
CREATE INDEX idx_notifications_tenant ON notifications(tenant_id);
CREATE INDEX idx_notifications_created ON notifications(created_at DESC);
CREATE INDEX idx_notifications_unread ON notifications(is_read);

-- =====================================================
-- END OF MIGRATION V1
-- =====================================================
