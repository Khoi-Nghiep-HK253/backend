# Phân Hệ Category — Nghiệp Vụ Chi Tiết

## Tổng Quan

Phân hệ **Category** quản lý các danh mục chi tiêu (Category): xem danh sách, xem chi tiết, tạo mới, cập nhật và xóa danh mục.

---

## API 1 — Lấy Danh Sách Tất Cả Danh Mục

```
GET /api/categories
```

### Luồng Nghiệp Vụ

```
1. Lấy tất cả danh mục từ DB (`categoryRepository.findAll()`)
2. Map danh sách Category → List<CategoryResponse> bằng MapStruct
3. Trả về 200 OK kèm danh sách CategoryResponse
```

---

## API 2 — Xem Chi Tiết Danh Mục

```
GET /api/categories/{id}
```

### Luồng Nghiệp Vụ

```
1. Tìm Category theo ID (`categoryRepository.findById(id)`)
   → Không tìm thấy → 404 NOT_FOUND
2. Map Category → CategoryResponse bằng MapStruct
3. Trả về 200 OK
```

---

## API 3 — Tạo Danh Mục Mới

```
POST /api/categories
Authorization: Bearer <token>
```

### Luồng Nghiệp Vụ

```
1. Validate CreateCategoryRequest (@Valid): name không được để trống (max 100 chars)
2. Kiểm tra tên danh mục đã tồn tại trong DB chưa (`categoryRepository.findByName(name)`)
   → Đã tồn tại → 409 CONFLICT ("Category with this name already exists")
3. Map CreateCategoryModel → Category entity bằng MapStruct
4. Lưu Category vào DB
5. Trả về 201 Created kèm CategoryResponse
```

---

## API 4 — Cập Nhật Danh Mục

```
PUT /api/categories/{id}
Authorization: Bearer <token>
```

### Luồng Nghiệp Vụ

```
1. Tìm Category theo ID
   → Không tìm thấy → 404 NOT_FOUND
2. Nếu name thay đổi: kiểm tra tên mới có bị trùng với danh mục khác không
   → Bị trùng → 409 CONFLICT ("Category with this name already exists")
3. Cập nhật các trường gửi lên bằng MapStruct (BeanMapping IGNORE_NULL)
4. Lưu Category vào DB
5. Trả về 200 OK kèm CategoryResponse cập nhật
```

---

## API 5 — Xóa Danh Mục

```
DELETE /api/categories/{id}
Authorization: Bearer <token>
```

### Luồng Nghiệp Vụ

```
1. Tìm Category theo ID
   → Không tìm thấy → 404 NOT_FOUND
2. Xóa Category khỏi DB (`categoryRepository.delete(category)`)
3. Trả về 200 OK
```
