-- =============================================================================
-- DIVVY DEV DATA SEED SCRIPT (Generated from DevDataSeeder.java)
-- All users default password: 123456
-- BCrypt Hash: $2a$10$8.UnVuG9HHgffUDAlk8qfOUVGkqRzgVymGe07xd00DMxs.AQvq4aO
-- =============================================================================

BEGIN;

-- 1. Seed Reference currency
INSERT INTO currency (id, acronym, name, created_at, updated_at) VALUES
(1, 'VND', 'Vietnamese Dong', NOW(), NOW()),
(2, 'USD', 'US Dollar', NOW(), NOW())
ON CONFLICT (id) DO NOTHING;

-- 2. Seed Expense Categories
INSERT INTO categories (id, name, icon, created_at, updated_at) VALUES
(1, 'Ăn uống', 'food', NOW(), NOW()),
(2, 'Di chuyển', 'transport', NOW(), NOW()),
(3, 'Nhà ở / Khách sạn', 'hotel', NOW(), NOW()),
(4, 'Giải trí', 'entertainment', NOW(), NOW()),
(5, 'Mua sắm', 'shopping', NOW(), NOW()),
(6, 'Khác', 'other', NOW(), NOW())
ON CONFLICT (id) DO NOTHING;

-- 3. Seed Sample Users (Password: 123456)
INSERT INTO users (id, username, firstname, lastname, phone, email, hash_password, role, created_at, updated_at) VALUES
(1, 'trihung', 'Hung', 'Doan', '0901111111', 'koikoidth12@gmail.com', '$2a$10$8.UnVuG9HHgffUDAlk8qfOUVGkqRzgVymGe07xd00DMxs.AQvq4aO', 'USER', NOW(), NOW()),
(2, 'vietanh', 'Anh', 'Pham', '0902222222', 'anh.phamviet241103@hcmut.edu.vn', '$2a$10$8.UnVuG9HHgffUDAlk8qfOUVGkqRzgVymGe07xd00DMxs.AQvq4aO', 'USER', NOW(), NOW()),
(3, 'duyhung', 'Hung', 'Pham', '0903333333', 'hung.phamdh@hcmut.edu.vn', '$2a$10$8.UnVuG9HHgffUDAlk8qfOUVGkqRzgVymGe07xd00DMxs.AQvq4aO', 'USER', NOW(), NOW()),
(4, 'thunguyen', 'Thu', 'Nguyen', '0904444444', 'thu.nguyen231@hcmut.edu.vn', '$2a$10$8.UnVuG9HHgffUDAlk8qfOUVGkqRzgVymGe07xd00DMxs.AQvq4aO', 'USER', NOW(), NOW()),
(5, 'adminuser', 'Admin', 'System', '0900000000', 'admin@example.com', '$2a$10$8.UnVuG9HHgffUDAlk8qfOUVGkqRzgVymGe07xd00DMxs.AQvq4aO', 'ADMIN', NOW(), NOW())
ON CONFLICT (id) DO NOTHING;

-- 4. Seed Sample Groups
INSERT INTO groups (id, name, note, cate_id, created_by, start_date, end_date, created_at, updated_at) VALUES
(1, 'Chuyến đi Đà Lạt', 'Nhóm bạn đi Đà Lạt 3 ngày 2 đêm', 3, 1, '2026-08-01', '2026-08-03', NOW(), NOW()),
(2, 'Tiền nhà chung cư', 'Chi phí sinh hoạt hàng tháng', 1, 1, NULL, NULL, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;

-- 5. Seed Group Members
INSERT INTO group_members (id, group_id, user_id, role, created_at, updated_at) VALUES
(1, 1, 1, 'OWNER', NOW(), NOW()),
(2, 1, 2, 'MEMBER', NOW(), NOW()),
(3, 1, 3, 'MEMBER', NOW(), NOW()),
(4, 1, 4, 'MEMBER', NOW(), NOW()),
(5, 2, 1, 'OWNER', NOW(), NOW()),
(6, 2, 2, 'MEMBER', NOW(), NOW())
ON CONFLICT (id) DO NOTHING;

-- 6. Seed Group Invitations
INSERT INTO group_invitations (id, group_id, inviter_id, invitee_id, status, token, message, expires_at, created_at, updated_at) VALUES
(1, 1, 1, 4, 'ACCEPTED', 'tok_abc123', 'Đi Đà Lạt cùng bọn mình nhé!', '2026-07-30 00:00:00', NOW(), NOW())
ON CONFLICT (id) DO NOTHING;

-- 7. Seed Sample Expense
INSERT INTO expenses (id, group_id, currency_id, created_by, description, total_amount, split_type, expense_date, created_at, updated_at) VALUES
(1, 1, 1, 1, 'Đặt phòng khách sạn 2 đêm', 2400000.00, 'EQUAL', '2026-08-01', NOW(), NOW())
ON CONFLICT (id) DO NOTHING;

-- 8. Seed Expense Payers
INSERT INTO expense_payers (id, expense_id, user_id, amount, created_at, updated_at) VALUES
(1, 1, 1, 2400000.00, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;

-- 9. Seed Expense Shares
INSERT INTO expense_shares (id, expense_id, user_id, amount, created_at, updated_at) VALUES
(1, 1, 1, 600000.00, NOW(), NOW()),
(2, 1, 2, 600000.00, NOW(), NOW()),
(3, 1, 3, 600000.00, NOW(), NOW()),
(4, 1, 4, 600000.00, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;

-- 10. Seed Activity Log
INSERT INTO activities (id, user_id, entity_type, entity_id, topic, description, created_at) VALUES
(1, 1, 'GROUP', 1, 'Tạo nhóm', 'trihung đã tạo nhóm ''Chuyến đi Đà Lạt''', NOW())
ON CONFLICT (id) DO NOTHING;

-- Update PostgreSQL ID Sequence values to prevent Primary Key collisions on new inserts
SELECT setval('currency_id_seq', (SELECT MAX(id) FROM currency));
SELECT setval('categories_id_seq', (SELECT MAX(id) FROM categories));
SELECT setval('users_id_seq', (SELECT MAX(id) FROM users));
SELECT setval('groups_id_seq', (SELECT MAX(id) FROM groups));
SELECT setval('group_members_id_seq', (SELECT MAX(id) FROM group_members));
SELECT setval('group_invitations_id_seq', (SELECT MAX(id) FROM group_invitations));
SELECT setval('expenses_id_seq', (SELECT MAX(id) FROM expenses));
SELECT setval('expense_payers_id_seq', (SELECT MAX(id) FROM expense_payers));
SELECT setval('expense_shares_id_seq', (SELECT MAX(id) FROM expense_shares));
SELECT setval('activities_id_seq', (SELECT MAX(id) FROM activities));

COMMIT;