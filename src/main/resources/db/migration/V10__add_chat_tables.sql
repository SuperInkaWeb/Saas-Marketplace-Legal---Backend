-- =============================================
-- V10: Chat System Tables
-- =============================================

CREATE TABLE chat_rooms (
    id          BIGSERIAL    PRIMARY KEY,
    public_id   UUID         NOT NULL UNIQUE DEFAULT gen_random_uuid(),
    case_request_id BIGINT   NOT NULL REFERENCES case_requests(id),
    client_user_id  BIGINT   NOT NULL REFERENCES users(id),
    lawyer_user_id  BIGINT   NOT NULL REFERENCES users(id),
    status      VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE',
    closed_at   TIMESTAMPTZ,
    created_at  TIMESTAMPTZ  DEFAULT now(),
    UNIQUE(case_request_id)
);

CREATE TABLE chat_messages (
    id              BIGSERIAL    PRIMARY KEY,
    public_id       UUID         NOT NULL UNIQUE DEFAULT gen_random_uuid(),
    chat_room_id    BIGINT       NOT NULL REFERENCES chat_rooms(id),
    sender_user_id  BIGINT       NOT NULL REFERENCES users(id),
    text            TEXT         NOT NULL,
    is_read         BOOLEAN      DEFAULT false,
    created_at      TIMESTAMPTZ  DEFAULT now()
);

-- Performance indexes
CREATE INDEX idx_chat_rooms_client  ON chat_rooms(client_user_id);
CREATE INDEX idx_chat_rooms_lawyer  ON chat_rooms(lawyer_user_id);
CREATE INDEX idx_chat_messages_room ON chat_messages(chat_room_id);
CREATE INDEX idx_chat_messages_read ON chat_messages(chat_room_id, is_read) WHERE is_read = false;
