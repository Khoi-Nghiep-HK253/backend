-- V2: Group relation tables - group_members, group_invitations

CREATE TABLE group_members (
    id         SERIAL PRIMARY KEY,
    group_id   INTEGER NOT NULL REFERENCES groups(id) ON DELETE CASCADE,
    user_id    INTEGER NOT NULL REFERENCES users(id)  ON DELETE CASCADE,
    role       VARCHAR(20) NOT NULL DEFAULT 'MEMBER'
               CHECK (role IN ('ADMIN', 'MEMBER')),
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    updated_at TIMESTAMP NOT NULL DEFAULT now(),
    UNIQUE (group_id, user_id)
);

CREATE TABLE group_invitations (
    id          SERIAL PRIMARY KEY,
    group_id    INTEGER NOT NULL REFERENCES groups(id) ON DELETE CASCADE,
    inviter_id  INTEGER NOT NULL REFERENCES users(id)  ON DELETE CASCADE,
    invitee_id  INTEGER NOT NULL REFERENCES users(id)  ON DELETE CASCADE,
    status      VARCHAR(20) NOT NULL DEFAULT 'PENDING'
                CHECK (status IN ('PENDING', 'ACCEPTED', 'DECLINED', 'EXPIRED')),
    token       VARCHAR(255) NOT NULL UNIQUE,
    message     TEXT,
    expires_at  TIMESTAMP,
    created_at  TIMESTAMP NOT NULL DEFAULT now(),
    updated_at  TIMESTAMP NOT NULL DEFAULT now()
);
