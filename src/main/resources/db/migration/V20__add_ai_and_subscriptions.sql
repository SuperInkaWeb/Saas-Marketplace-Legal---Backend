-- Table for user AI sessions
CREATE TABLE IF NOT EXISTS ai_chat_sessions (
    id BIGSERIAL PRIMARY KEY,
    public_id UUID UNIQUE DEFAULT gen_random_uuid(),
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    title VARCHAR(255),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP WITH TIME ZONE
);

-- Table for individual chat messages in a session
CREATE TABLE IF NOT EXISTS ai_chat_messages (
    id BIGSERIAL PRIMARY KEY,
    session_id BIGINT NOT NULL REFERENCES ai_chat_sessions(id) ON DELETE CASCADE,
    role VARCHAR(20) NOT NULL CHECK (role IN ('USER', 'ASSISTANT', 'SYSTEM')),
    content TEXT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Insert initial seed data for Subscription Plans if not exists
INSERT INTO subscription_plans (name, target_audience, monthly_price, features)
VALUES 
    ('FREE', 'LAWYER', 0.00, '{"max_appointments": 5, "includes_ai": false}'),
    ('PREMIUM', 'LAWYER', 49.99, '{"max_appointments": -1, "includes_ai": true, "ai_document_analysis": true}'),
    ('CORPORATE', 'LAW_FIRM', 199.99, '{"max_appointments": -1, "includes_ai": true, "ai_document_analysis": true, "multiple_users": true}')
ON CONFLICT DO NOTHING;
