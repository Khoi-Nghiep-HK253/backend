-- Flyway Migration: add_split_type_to_expenses
-- Auto-generated diff by MigrationGenerator (Prisma-like)
-- Timestamp: 20260801230421

alter table if exists expenses add column split_type varchar(20) not null check ((split_type in ('EQUAL','EXACT','PERCENTAGE','SHARES','ADJUSTMENT')));
