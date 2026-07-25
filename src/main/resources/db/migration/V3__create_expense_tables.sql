-- V3: Expense tables - expenses, expense_payers, expense_shares

CREATE TABLE expenses (
    id           SERIAL PRIMARY KEY,
    group_id     INTEGER NOT NULL REFERENCES groups(id)     ON DELETE CASCADE,
    currency_id  INTEGER NOT NULL REFERENCES currency(id),
    cate_id      INTEGER REFERENCES categories(id) ON DELETE SET NULL,
    description  VARCHAR(255) NOT NULL,
    total_amount NUMERIC(12,2) NOT NULL DEFAULT 0 CHECK (total_amount >= 0),
    expense_date DATE NOT NULL DEFAULT CURRENT_DATE,
    created_at   TIMESTAMP NOT NULL DEFAULT now(),
    updated_at   TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE expense_payers (
    id         SERIAL PRIMARY KEY,
    user_id    INTEGER NOT NULL REFERENCES users(id)    ON DELETE CASCADE,
    expense_id INTEGER NOT NULL REFERENCES expenses(id) ON DELETE CASCADE,
    amount     NUMERIC(12,2) NOT NULL DEFAULT 0 CHECK (amount >= 0),
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    updated_at TIMESTAMP NOT NULL DEFAULT now(),
    UNIQUE (user_id, expense_id)
);

CREATE TABLE expense_shares (
    id         SERIAL PRIMARY KEY,
    user_id    INTEGER NOT NULL REFERENCES users(id)    ON DELETE CASCADE,
    expense_id INTEGER NOT NULL REFERENCES expenses(id) ON DELETE CASCADE,
    amount     NUMERIC(12,2) NOT NULL DEFAULT 0 CHECK (amount >= 0),
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    updated_at TIMESTAMP NOT NULL DEFAULT now(),
    UNIQUE (user_id, expense_id)
);
