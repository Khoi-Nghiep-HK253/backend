# Phân Hệ User — Nghiệp Vụ Chi Tiết

## Tổng Quan

Phân hệ **User** quản lý thông tin cá nhân của người dùng: xem danh sách, xem chi tiết, tạo mới (admin), cập nhật hồ sơ và đổi mật khẩu.

Tất cả API đều yêu cầu xác thực JWT (`Authorization: Bearer <token>`).

---

## API 1 — Lấy Danh Sách Tất Cả Users

```
GET /api/users
Authorization: Bearer <token>
```

### Luồng Nghiệp Vụ

```
1. Xác thực JWT (Spring Security filter)
2. Lấy toàn bộ danh sách User từ DB (không phân trang)
3. Map từng User → UserResponse
4. Trả về List<UserResponse>
```

> **Lưu ý**: API này không có phân quyền chặt chẽ. Cân nhắc giới hạn cho ADMIN trong tương lai.

---

## API 2 — Xem Chi Tiết User Theo ID

```
GET /api/users/{id}
Authorization: Bearer <token>
```

### Luồng Nghiệp Vụ

```
1. Xác thực JWT
2. Tìm User theo ID
   → Không tìm thấy → 404 NOT_FOUND "User not found with id: {id}"
3. Map User → UserResponse
4. Trả về UserResponse
```

---

## API 3 — Tạo User Mới (Admin)

```
POST /api/users
Authorization: Bearer <token>
```

### Luồng Nghiệp Vụ

```
1. Nhận CreateUserRequest (username, email, password, firstname, lastname, phone)
2. Kiểm tra username đã tồn tại chưa → 409 CONFLICT nếu có
3. Kiểm tra email đã tồn tại chưa    → 409 CONFLICT nếu có
4. Map request → User entity
5. Encode password bằng BCrypt
6. Lưu User vào DB
7. Trả về UserResponse
```

### Các Điều Kiện Kiểm Tra

| Điều kiện | Kết quả |
|---|---|
| `username` đã tồn tại | `409 Conflict` |
| `email` đã tồn tại | `409 Conflict` |

---

## API 4 — Cập Nhật Hồ Sơ

```
PUT /api/users/{id}
Authorization: Bearer <token>
```

### Luồng Nghiệp Vụ

```
1. Lấy username từ JWT (người đang đăng nhập)
2. Tìm User theo ID trong path
   → Không tìm thấy → 404 NOT_FOUND
3. Kiểm tra quyền sở hữu: user.username phải == currentUsername
   → Không khớp → 403 FORBIDDEN "You are not authorized to modify this user's data."
4. Cập nhật các trường (firstname, lastname, phone) bằng MapStruct partial update
   → Trường nào null trong request thì giữ nguyên giá trị cũ (IGNORE nulls)
5. Lưu User vào DB
6. Trả về UserResponse cập nhật
```

### Các Điều Kiện Kiểm Tra

| Điều kiện | Kết quả |
|---|---|
| User không tồn tại | `404 Not Found` |
| Caller không phải chủ sở hữu | `403 Forbidden` |

---

## API 5 — Đổi Mật Khẩu

```
PUT /api/users/{id}/change-password
Authorization: Bearer <token>
```

### Luồng Nghiệp Vụ

```
1. Lấy username từ JWT
2. Tìm User theo ID
   → Không tìm thấy → 404 NOT_FOUND
3. Kiểm tra quyền sở hữu: user.username phải == currentUsername
   → Không khớp → 403 FORBIDDEN
4. Validate mật khẩu:
   a. oldPassword phải khớp với hashPassword trong DB (BCrypt.matches)
      → Không khớp → 400 "Current password is incorrect."
   b. newPassword phải khác oldPassword
      → Giống nhau → 400 "New password must be different from the current password."
5. Encode newPassword bằng BCrypt, cập nhật hashPassword
6. Lưu User vào DB
7. Trả về 200 OK (no body)
```

### Các Điều Kiện Kiểm Tra

| Điều kiện | Kết quả |
|---|---|
| User không tồn tại | `404 Not Found` |
| Caller không phải chủ sở hữu | `403 Forbidden` |
| `oldPassword` sai | `400 Bad Request` |
| `newPassword == oldPassword` | `400 Bad Request` |
