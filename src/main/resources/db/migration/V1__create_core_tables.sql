-- V1: Core tables - users, currency, categories, groups

CREATE TABLE users (
    id             SERIAL PRIMARY KEY,
    username       VARCHAR(50)  NOT NULL UNIQUE,
    firstname      VARCHAR(100),
    lastname       VARCHAR(100),
    phone          VARCHAR(20)  UNIQUE,
    email          VARCHAR(150) NOT NULL UNIQUE,
    hash_password  VARCHAR(255) NOT NULL,
    role           VARCHAR(20)  NOT NULL DEFAULT 'USER'
                   CHECK (role IN ('USER', 'ADMIN')),
    created_at     TIMESTAMP NOT NULL DEFAULT now(),
    updated_at     TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE currency (
    id         SERIAL PRIMARY KEY,
    name       VARCHAR(50) NOT NULL,
    acronym    VARCHAR(10) NOT NULL UNIQUE,
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    updated_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE categories (
    id         SERIAL PRIMARY KEY,
    name       VARCHAR(100) NOT NULL,
    icon       VARCHAR(100),
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    updated_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE groups (
    id                   SERIAL PRIMARY KEY,
    cate_id              INTEGER REFERENCES categories(id) ON DELETE SET NULL,
    default_currency_id  INTEGER REFERENCES currency(id) ON DELETE SET NULL,
    name                 VARCHAR(150) NOT NULL,
    note                 TEXT,
    start_date           DATE,
    end_date             DATE,
    created_at           TIMESTAMP NOT NULL DEFAULT now(),
    updated_at           TIMESTAMP NOT NULL DEFAULT now()
);
