# Phân Hệ Currency — Nghiệp Vụ Chi Tiết

## Tổng Quan

Phân hệ **Currency** quản lý danh mục đơn vị tiền tệ (Currency): xem danh sách, xem chi tiết, tạo mới, cập nhật và xóa tiền tệ.

---

## API 1 — Lấy Danh Sách Tất Cả Tiền Tệ

```
GET /api/currencies
```

### Luồng Nghiệp Vụ

```
1. Lấy tất cả loại tiền tệ từ DB (`currencyRepository.findAll()`)
2. Map danh sách Currency → List<CurrencyResponse> bằng MapStruct (gán `code` = `acronym`)
3. Trả về 200 OK kèm danh sách CurrencyResponse
```

---

## API 2 — Xem Chi Tiết Tiền Tệ

```
GET /api/currencies/{id}
```

### Luồng Nghiệp Vụ

```
1. Tìm Currency theo ID (`currencyRepository.findById(id)`)
   → Không tìm thấy → 404 NOT_FOUND
2. Map Currency → CurrencyResponse bằng MapStruct
3. Trả về 200 OK
```

---

## API 3 — Tạo Tiền Tệ Mới

```
POST /api/currencies
Authorization: Bearer <token>
```

### Luồng Nghiệp Vụ

```
1. Validate CreateCurrencyRequest (@Valid): name & acronym không được để trống
2. Kiểm tra mã viết tắt (acronym) đã tồn tại trong DB chưa (`currencyRepository.findByAcronym(acronym)`)
   → Đã tồn tại → 409 CONFLICT ("Currency with this acronym already exists")
3. Map CreateCurrencyModel → Currency entity bằng MapStruct
4. Lưu Currency vào DB
5. Trả về 201 Created kèm CurrencyResponse
```

---

## API 4 — Cập Nhật Tiền Tệ

```
PUT /api/currencies/{id}
Authorization: Bearer <token>
```

### Luồng Nghiệp Vụ

```
1. Tìm Currency theo ID
   → Không tìm thấy → 404 NOT_FOUND
2. Nếu acronym thay đổi: kiểm tra mã mới có bị trùng không
   → Bị trùng → 409 CONFLICT ("Currency with this acronym already exists")
3. Cập nhật các trường gửi lên bằng MapStruct (BeanMapping IGNORE_NULL)
4. Lưu Currency vào DB
5. Trả về 200 OK kèm CurrencyResponse cập nhật
```

---

## API 5 — Xóa Tiền Tệ

```
DELETE /api/currencies/{id}
Authorization: Bearer <token>
```

### Luồng Nghiệp Vụ

```
1. Tìm Currency theo ID
   → Không tìm thấy → 404 NOT_FOUND
2. Xóa Currency khỏi DB (`currencyRepository.delete(currency)`)
3. Trả về 200 OK
```
