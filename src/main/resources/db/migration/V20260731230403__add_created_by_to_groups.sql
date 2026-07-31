-- Flyway Migration: add_created_by_to_groups
-- Auto-generated diff by MigrationGenerator (Prisma-like)
-- Timestamp: 20260731230403

alter table if exists groups add column created_by integer not null;
create index idx_groups_created_by on groups (created_by);
alter table if exists groups add constraint FKkhpvhy2p2c1un4krvhwnau23b foreign key (created_by) references users;
