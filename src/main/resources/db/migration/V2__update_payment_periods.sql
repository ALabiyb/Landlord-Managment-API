-- =====================================================
-- Update Payment Period Constraints
-- =====================================================

-- Drop the old constraint
ALTER TABLE leases DROP CONSTRAINT IF EXISTS chk_payment_period;

-- Add the updated constraint with 4 and 6 month options
ALTER TABLE leases ADD CONSTRAINT chk_payment_period 
    CHECK (payment_period IN ('MONTHLY', 'QUARTERLY', 'FOUR_MONTHS', 'SIX_MONTHS', 'ANNUALLY', 'YEARLY'));
