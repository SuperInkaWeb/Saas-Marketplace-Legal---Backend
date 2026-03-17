CREATE EXTENSION IF NOT EXISTS pgcrypto;
CREATE EXTENSION IF NOT EXISTS btree_gist;
CREATE EXTENSION IF NOT EXISTS cube;
CREATE EXTENSION IF NOT EXISTS earthdistance;

CREATE TYPE timerange AS RANGE (
    subtype = time
);

CREATE TABLE IF NOT EXISTS roles (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL,
    description VARCHAR(255),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    public_id UUID UNIQUE DEFAULT gen_random_uuid(),
    email VARCHAR(255) UNIQUE NOT NULL,
    password_secret VARCHAR(255) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name_father VARCHAR(100) NOT NULL,
    last_name_mother VARCHAR(100) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    role_id BIGINT REFERENCES roles(id),
    onboarding_step VARCHAR(30) NOT NULL DEFAULT 'COMPLETED',
    account_status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    version BIGINT DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP WITH TIME ZONE
);

CREATE TABLE IF NOT EXISTS otp_verifications (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    code VARCHAR(6) NOT NULL,
    purpose VARCHAR(50) NOT NULL,
    expires_at TIMESTAMP WITH TIME ZONE NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS identity_documents (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    document_type VARCHAR(50) NOT NULL,
    document_number VARCHAR(100) NOT NULL,
    country_code CHAR(2) NOT NULL,
    is_verified BOOLEAN DEFAULT FALSE,
    verified_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_document_type_number UNIQUE (document_type, document_number)
);

CREATE TABLE IF NOT EXISTS subscription_plans (
    id BIGSERIAL PRIMARY KEY,
    public_id UUID UNIQUE DEFAULT gen_random_uuid(),
    name VARCHAR(100) NOT NULL,
    target_audience VARCHAR(20) CHECK (target_audience IN ('LAWYER', 'LAW_FIRM')),
    monthly_price DECIMAL(10, 2) NOT NULL,
    features JSONB,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS user_subscriptions (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    plan_id BIGINT NOT NULL REFERENCES subscription_plans(id),
    status VARCHAR(20) DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'CANCELLED', 'PAST_DUE')),
    current_period_start TIMESTAMP WITH TIME ZONE NOT NULL,
    current_period_end TIMESTAMP WITH TIME ZONE NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS law_firms (
    id BIGSERIAL PRIMARY KEY,
    public_id UUID UNIQUE DEFAULT gen_random_uuid(),
    name VARCHAR(200) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP WITH TIME ZONE
);

CREATE TABLE IF NOT EXISTS lawyer_profiles (
    id BIGSERIAL PRIMARY KEY,
    public_id UUID UNIQUE DEFAULT gen_random_uuid(),
    user_id BIGINT UNIQUE NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    law_firm_id BIGINT REFERENCES law_firms(id) ON DELETE SET NULL,
    slug VARCHAR(200) UNIQUE NOT NULL,
    bio TEXT,
    city VARCHAR(100) NOT NULL,
    country VARCHAR(100) NOT NULL,
    latitude DECIMAL(10, 8),
    longitude DECIMAL(11, 8),
    hourly_rate DECIMAL(10, 2) NOT NULL,
    currency VARCHAR(3) DEFAULT 'USD',
    rating_avg DECIMAL(3, 2) DEFAULT 0.00,
    review_count INTEGER DEFAULT 0,
    bar_registration_number VARCHAR(50),
    bar_association VARCHAR(150),
    verification_status VARCHAR(20) DEFAULT 'PENDING'
        CHECK (verification_status IN ('PENDING', 'VERIFIED', 'REJECTED')),
    is_verified BOOLEAN DEFAULT FALSE,
    search_vector tsvector GENERATED ALWAYS AS (
        setweight(to_tsvector('spanish', coalesce(city,'')), 'A') ||
        setweight(to_tsvector('spanish', coalesce(bio,'')), 'B')
    ) STORED,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP WITH TIME ZONE
);

CREATE TABLE IF NOT EXISTS client_profiles (
    id BIGSERIAL PRIMARY KEY,
    public_id UUID UNIQUE DEFAULT gen_random_uuid(),
    user_id BIGINT UNIQUE NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    company_name VARCHAR(150),
    billing_address TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP WITH TIME ZONE
);

CREATE TABLE IF NOT EXISTS specialties (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) UNIQUE NOT NULL,
    description TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS lawyer_specialties (
    lawyer_profile_id BIGINT NOT NULL REFERENCES lawyer_profiles(id) ON DELETE CASCADE,
    specialty_id BIGINT NOT NULL REFERENCES specialties(id) ON DELETE CASCADE,
    PRIMARY KEY (lawyer_profile_id, specialty_id)
);

CREATE TABLE IF NOT EXISTS lawyer_schedules (
    id BIGSERIAL PRIMARY KEY,
    lawyer_profile_id BIGINT NOT NULL REFERENCES lawyer_profiles(id) ON DELETE CASCADE,
    day_of_week SMALLINT NOT NULL CHECK (day_of_week BETWEEN 1 AND 7),
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT check_time_logic CHECK (start_time < end_time),
    CONSTRAINT prevent_overlapping_schedules EXCLUDE USING gist (
        lawyer_profile_id WITH =,
        day_of_week WITH =,
        timerange(start_time, end_time) WITH &&
    )
);

CREATE TABLE IF NOT EXISTS appointments (
    id BIGSERIAL PRIMARY KEY,
    public_id UUID UNIQUE DEFAULT gen_random_uuid(),
    client_profile_id BIGINT NOT NULL REFERENCES client_profiles(id),
    lawyer_profile_id BIGINT NOT NULL REFERENCES lawyer_profiles(id),
    scheduled_start TIMESTAMP WITH TIME ZONE NOT NULL,
    scheduled_end TIMESTAMP WITH TIME ZONE NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING'
        CHECK (status IN ('PENDING', 'CONFIRMED', 'CANCELLED', 'COMPLETED', 'NO_SHOW')),
    meeting_link VARCHAR(255),
    notes TEXT,
    version BIGINT DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT check_appointment_time CHECK (scheduled_start < scheduled_end),
    CONSTRAINT prevent_double_booking EXCLUDE USING gist (
        lawyer_profile_id WITH =,
        tstzrange(scheduled_start, scheduled_end) WITH &&
    ) WHERE (status NOT IN ('CANCELLED', 'NO_SHOW'))
);

CREATE TABLE IF NOT EXISTS case_requests (
    id BIGSERIAL PRIMARY KEY,
    public_id UUID UNIQUE DEFAULT gen_random_uuid(),
    client_profile_id BIGINT NOT NULL REFERENCES client_profiles(id) ON DELETE CASCADE,
    specialty_id BIGINT REFERENCES specialties(id) ON DELETE SET NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    budget DECIMAL(10,2),
    status VARCHAR(20) NOT NULL DEFAULT 'OPEN'
        CHECK (status IN ('OPEN', 'IN_PROGRESS', 'CLOSED')),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP WITH TIME ZONE
);

CREATE TABLE IF NOT EXISTS lawyer_proposals (
    id BIGSERIAL PRIMARY KEY,
    case_request_id BIGINT NOT NULL REFERENCES case_requests(id) ON DELETE CASCADE,
    lawyer_profile_id BIGINT NOT NULL REFERENCES lawyer_profiles(id) ON DELETE CASCADE,
    proposal_text TEXT NOT NULL,
    proposed_fee DECIMAL(10,2) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING'
        CHECK (status IN ('PENDING', 'ACCEPTED', 'REJECTED')),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_lawyer_case_proposal UNIQUE (case_request_id, lawyer_profile_id)
);

CREATE TABLE IF NOT EXISTS payments (
    id BIGSERIAL PRIMARY KEY,
    public_id UUID UNIQUE DEFAULT gen_random_uuid(),
    appointment_id BIGINT UNIQUE REFERENCES appointments(id),
    amount DECIMAL(10, 2) NOT NULL,
    currency VARCHAR(3) DEFAULT 'USD',
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING'
        CHECK (status IN ('PENDING', 'PROCESSING', 'SUCCEEDED', 'FAILED', 'REFUNDED')),
    payment_method VARCHAR(50),
    transaction_id VARCHAR(100) UNIQUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS reviews (
    id BIGSERIAL PRIMARY KEY,
    appointment_id BIGINT UNIQUE NOT NULL REFERENCES appointments(id),
    client_profile_id BIGINT NOT NULL REFERENCES client_profiles(id),
    lawyer_profile_id BIGINT NOT NULL REFERENCES lawyer_profiles(id),
    rating SMALLINT NOT NULL CHECK (rating BETWEEN 1 AND 5),
    comment TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP WITH TIME ZONE
);

CREATE TABLE IF NOT EXISTS documents (
    id BIGSERIAL PRIMARY KEY,
    public_id UUID UNIQUE DEFAULT gen_random_uuid(),
    user_id BIGINT NOT NULL REFERENCES users(id),
    appointment_id BIGINT REFERENCES appointments(id) ON DELETE SET NULL,
    file_name VARCHAR(255) NOT NULL,
    file_url VARCHAR(500) NOT NULL,
    file_type VARCHAR(50),
    file_size_bytes BIGINT,
    is_template BOOLEAN DEFAULT FALSE,
    price DECIMAL(10, 2) DEFAULT 0.00,
    signature_status VARCHAR(50) DEFAULT 'NOT_REQUIRED'
        CHECK (signature_status IN ('NOT_REQUIRED', 'PENDING', 'SIGNED', 'FAILED')),
    external_signature_id VARCHAR(255),
    is_archived BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP WITH TIME ZONE
);

CREATE TABLE IF NOT EXISTS document_purchases (
    id BIGSERIAL PRIMARY KEY,
    client_profile_id BIGINT NOT NULL REFERENCES client_profiles(id),
    document_id BIGINT NOT NULL REFERENCES documents(id),
    amount_paid DECIMAL(10, 2) NOT NULL,
    purchased_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS notifications (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    type VARCHAR(50) NOT NULL,
    message TEXT NOT NULL,
    is_read BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS audit_logs (
    id BIGSERIAL PRIMARY KEY,
    entity_name VARCHAR(100) NOT NULL,
    entity_id BIGINT NOT NULL,
    action VARCHAR(20) NOT NULL
        CHECK (action IN ('CREATE', 'UPDATE', 'DELETE', 'SOFT_DELETE')),
    actor_id BIGINT REFERENCES users(id) ON DELETE SET NULL,
    old_values JSONB,
    new_values JSONB,
    ip_address VARCHAR(45),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_lawyer_search_vector ON lawyer_profiles USING GIN (search_vector);
CREATE INDEX IF NOT EXISTS idx_lawyer_profiles_city ON lawyer_profiles(city);
CREATE INDEX IF NOT EXISTS idx_lawyer_profiles_country ON lawyer_profiles(country);
CREATE UNIQUE INDEX IF NOT EXISTS idx_lawyer_slug ON lawyer_profiles(slug) WHERE deleted_at IS NULL;
CREATE INDEX IF NOT EXISTS idx_lawyers_marketplace ON lawyer_profiles(is_verified, rating_avg DESC) WHERE deleted_at IS NULL AND is_verified = TRUE;
CREATE INDEX IF NOT EXISTS idx_lawyers_location_geo ON lawyer_profiles USING gist (ll_to_earth(latitude, longitude));
CREATE INDEX IF NOT EXISTS idx_appointments_calendar ON appointments(lawyer_profile_id, scheduled_start) WHERE status NOT IN ('CANCELLED', 'NO_SHOW');
CREATE INDEX IF NOT EXISTS idx_case_requests_client ON case_requests(client_profile_id);
CREATE INDEX IF NOT EXISTS idx_case_requests_specialty ON case_requests(specialty_id);
CREATE INDEX IF NOT EXISTS idx_case_requests_status ON case_requests(status);
CREATE INDEX IF NOT EXISTS idx_lawyer_proposals_lawyer ON lawyer_proposals(lawyer_profile_id);
CREATE INDEX IF NOT EXISTS idx_lawyer_proposals_case ON lawyer_proposals(case_request_id);
CREATE INDEX IF NOT EXISTS idx_documents_is_template ON documents(is_template);
CREATE INDEX IF NOT EXISTS idx_audit_logs_entity ON audit_logs(entity_name, entity_id);
CREATE INDEX IF NOT EXISTS idx_audit_logs_new_values ON audit_logs USING GIN (new_values);
CREATE INDEX IF NOT EXISTS idx_user_subscriptions_status ON user_subscriptions(status);
CREATE INDEX IF NOT EXISTS idx_fk_lawyer_specialties ON lawyer_specialties(specialty_id);
CREATE INDEX IF NOT EXISTS idx_fk_payments_appointment ON payments(appointment_id);
CREATE INDEX IF NOT EXISTS idx_fk_reviews_lawyer ON reviews(lawyer_profile_id);
CREATE INDEX IF NOT EXISTS idx_fk_document_purchases_doc ON document_purchases(document_id);