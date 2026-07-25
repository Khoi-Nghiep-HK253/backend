-- V8: Sample/fake data - CHỈ chạy ở môi trường dev (không nằm trong db/migration
-- chính, xem application-dev.yml để biết cách bật riêng cho profile "dev")

INSERT INTO users (username, firstname, lastname, phone, email, hash_password, role) VALUES
('hungtri',  'Hung',  'Tri',    '0901111111', 'hung@example.com',  'hash_1', 'USER'),
('khanhnt',  'Khanh', 'Nguyen', '0902222222', 'khanh@example.com', 'hash_2', 'USER'),
('anle',     'An',    'Le',     '0903333333', 'an@example.com',    'hash_3', 'USER'),
('binhpham', 'Binh',  'Pham',   '0904444444', 'binh@example.com',  'hash_4', 'USER'),
('adminuser','Admin', 'System', '0900000000', 'admin@example.com', 'hash_admin', 'ADMIN');

INSERT INTO groups (cate_id, default_currency_id, name, note, start_date, end_date) VALUES
(3, 1, 'Chuyến đi Đà Lạt', 'Nhóm bạn đi Đà Lạt 3 ngày 2 đêm', '2026-08-01', '2026-08-03'),
(1, 1, 'Tiền nhà chung cư', 'Chi phí sinh hoạt hàng tháng', NULL, NULL);

INSERT INTO group_members (group_id, user_id, role) VALUES
(1, 1, 'ADMIN'),
(1, 2, 'MEMBER'),
(1, 3, 'MEMBER'),
(1, 4, 'MEMBER'),
(2, 1, 'ADMIN'),
(2, 2, 'MEMBER');

INSERT INTO group_invitations (group_id, inviter_id, invitee_id, status, token, message, expires_at) VALUES
(1, 1, 4, 'ACCEPTED', 'tok_abc123', 'Đi Đà Lạt cùng bọn mình nhé!', '2026-07-30 00:00:00'),
(2, 1, 2, 'ACCEPTED', 'tok_def456', 'Mời tham gia nhóm tiền nhà', '2026-07-15 00:00:00');

INSERT INTO expenses (group_id, currency_id, cate_id, description, total_amount, expense_date) VALUES
(1, 1, 3, 'Đặt phòng khách sạn 2 đêm', 2400000, '2026-08-01'),
(1, 1, 1, 'Ăn tối ngày 1', 800000, '2026-08-01'),
(1, 1, 2, 'Thuê xe máy 3 ngày', 450000, '2026-08-01'),
(1, 1, 4, 'Vé tham quan thác Datanla', 400000, '2026-08-02'),
(2, 1, 1, 'Tiền điện nước tháng 7', 600000, '2026-07-28');

INSERT INTO expense_payers (user_id, expense_id, amount) VALUES
(1, 1, 2400000),
(3, 2, 800000),
(2, 3, 450000),
(4, 4, 400000),
(1, 5, 600000);

INSERT INTO expense_shares (user_id, expense_id, amount) VALUES
(1, 1, 600000), (2, 1, 600000), (3, 1, 600000), (4, 1, 600000),
(1, 2, 200000), (2, 2, 200000), (3, 2, 200000), (4, 2, 200000),
(1, 3, 112500), (2, 3, 112500), (3, 3, 112500), (4, 3, 112500),
(1, 4, 100000), (2, 4, 100000), (3, 4, 100000), (4, 4, 100000),
(1, 5, 300000), (2, 5, 300000);

INSERT INTO debts (expense_id, from_user_id, to_user_id, amount, status) VALUES
(1, 2, 1, 600000, 'PENDING'),
(1, 3, 1, 600000, 'SETTLED'),
(1, 4, 1, 600000, 'PENDING'),
(2, 1, 3, 200000, 'PENDING'),
(2, 2, 3, 200000, 'PENDING'),
(2, 4, 3, 200000, 'PENDING'),
(3, 1, 2, 112500, 'PENDING'),
(3, 3, 2, 112500, 'PENDING'),
(3, 4, 2, 112500, 'PENDING'),
(4, 1, 4, 100000, 'PENDING'),
(4, 2, 4, 100000, 'PENDING'),
(4, 3, 4, 100000, 'PENDING'),
(5, 2, 1, 300000, 'PENDING');

INSERT INTO settlements (debt_id, group_id, from_user_id, to_user_id, amount, method, note, paid_at) VALUES
(2, 1, 3, 1, 600000, 'BANK_TRANSFER', 'Chuyển khoản trả tiền khách sạn Đà Lạt', '2026-08-05 10:00:00');

INSERT INTO activities (user_id, entity_type, entity_id, topic, description) VALUES
(1, 'GROUP', 1, 'Tạo nhóm', 'hungtri đã tạo nhóm "Chuyến đi Đà Lạt"'),
(1, 'EXPENSE', 1, 'Thêm chi phí', 'hungtri đã thêm chi phí "Đặt phòng khách sạn 2 đêm" - 2,400,000 VND'),
(3, 'SETTLEMENT', 1, 'Thanh toán', 'anle đã chuyển khoản 600,000 VND cho hungtri');
