-- V4: Debt & settlement tables

CREATE TABLE debts (
    id           SERIAL PRIMARY KEY,
    expense_id   INTEGER NOT NULL REFERENCES expenses(id) ON DELETE CASCADE,
    from_user_id INTEGER NOT NULL REFERENCES users(id)    ON DELETE CASCADE,
    to_user_id   INTEGER NOT NULL REFERENCES users(id)    ON DELETE CASCADE,
    amount       NUMERIC(12,2) NOT NULL CHECK (amount >= 0),
    status       VARCHAR(20) NOT NULL DEFAULT 'PENDING'
                 CHECK (status IN ('PENDING', 'PARTIALLY_PAID', 'SETTLED')),
    created_at   TIMESTAMP NOT NULL DEFAULT now(),
    updated_at   TIMESTAMP NOT NULL DEFAULT now(),
    CHECK (from_user_id <> to_user_id)
);

CREATE TABLE settlements (
    id           SERIAL PRIMARY KEY,
    debt_id      INTEGER REFERENCES debts(id) ON DELETE SET NULL,
    group_id     INTEGER NOT NULL REFERENCES groups(id) ON DELETE CASCADE,
    from_user_id INTEGER NOT NULL REFERENCES users(id)  ON DELETE CASCADE,
    to_user_id   INTEGER NOT NULL REFERENCES users(id)  ON DELETE CASCADE,
    amount       NUMERIC(12,2) NOT NULL CHECK (amount > 0),
    method       VARCHAR(50) DEFAULT 'CASH',
    note         TEXT,
    paid_at      TIMESTAMP NOT NULL DEFAULT now(),
    created_at   TIMESTAMP NOT NULL DEFAULT now()
);
