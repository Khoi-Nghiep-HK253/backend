-- V7: Reference data bắt buộc phải có để app chạy được (mọi môi trường, kể cả prod)
-- Không insert "id" thủ công -> để SERIAL tự sinh, tránh lệch sequence

INSERT INTO currency (name, acronym) VALUES
('Vietnamese Dong', 'VND'),
('US Dollar', 'USD');

INSERT INTO categories (name, icon) VALUES
('Ăn uống', 'food'),
('Di chuyển', 'transport'),
('Nhà ở / Khách sạn', 'hotel'),
('Giải trí', 'entertainment'),
('Mua sắm', 'shopping'),
('Khác', 'other');
