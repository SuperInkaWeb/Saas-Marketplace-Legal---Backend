-- V7__add_reply_to_reviews.sql
ALTER TABLE reviews ADD COLUMN IF NOT EXISTS reply_text TEXT;
ALTER TABLE reviews ADD COLUMN IF NOT EXISTS replied_at TIMESTAMP WITH TIME ZONE;
