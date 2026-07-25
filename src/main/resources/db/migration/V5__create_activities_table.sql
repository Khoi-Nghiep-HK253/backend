-- V5: Activities (audit log)

CREATE TABLE activities (
    id          SERIAL PRIMARY KEY,
    user_id     INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    entity_type VARCHAR(30) CHECK (entity_type IN ('GROUP', 'EXPENSE', 'DEBT', 'SETTLEMENT', 'MEMBER')),
    entity_id   INTEGER,
    topic       VARCHAR(150),
    description TEXT,
    created_at  TIMESTAMP NOT NULL DEFAULT now()
);
