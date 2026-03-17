-- =============================================================
-- V3: Auth + Onboarding + KYC Refactor
-- Single-role migration, onboarding_step, account_status
-- =============================================================

-- 1. Add new columns to users
ALTER TABLE users ADD COLUMN IF NOT EXISTS role_id BIGINT REFERENCES roles(id);
ALTER TABLE users ADD COLUMN IF NOT EXISTS onboarding_step VARCHAR(30) NOT NULL DEFAULT 'COMPLETED';
ALTER TABLE users ADD COLUMN IF NOT EXISTS account_status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE';

-- 2. Migrate existing user_roles data → single role (pick first role per user)
UPDATE users u
SET role_id = (
    SELECT ur.role_id FROM user_roles ur WHERE ur.user_id = u.id LIMIT 1
);

-- 3. Migrate is_active → account_status
UPDATE users SET account_status = CASE WHEN is_active = TRUE THEN 'ACTIVE' ELSE 'PENDING' END;

-- 4. Existing verified users should have COMPLETED onboarding
UPDATE users SET onboarding_step = 'COMPLETED' WHERE account_status = 'ACTIVE' AND role_id IS NOT NULL;
UPDATE users SET onboarding_step = 'ROLE_SELECTION' WHERE account_status = 'ACTIVE' AND role_id IS NULL;
UPDATE users SET onboarding_step = 'ACCOUNT_CREATED' WHERE account_status = 'PENDING';

-- 5. Drop old structures
DROP TABLE IF EXISTS user_roles;
ALTER TABLE users DROP COLUMN IF EXISTS is_active;
