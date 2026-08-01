# Phân Hệ Auth — Nghiệp Vụ Chi Tiết

## Tổng Quan

Phân hệ **Auth** xử lý toàn bộ luồng xác thực và quản lý mật khẩu: đăng ký tài khoản, đăng nhập, lấy thông tin người dùng hiện tại, quên mật khẩu, xác minh token và đặt lại mật khẩu.

Tất cả API (ngoại trừ `GET /me`) không yêu cầu JWT, còn `GET /me` yêu cầu Bearer Token.

---

## API 1 — Đăng Ký Tài Khoản

```
POST /api/auth/register
```

### Luồng Nghiệp Vụ

```
1. Nhận RegisterRequest (username, email, password, firstname, lastname, phone)
2. Kiểm tra username đã tồn tại chưa → nếu có → 409 CONFLICT "Username already exists"
3. Kiểm tra email đã tồn tại chưa    → nếu có → 409 CONFLICT "Email already exists"
4. Map request → User entity (MapStruct)
5. Encode password bằng BCrypt, gán vào hashPassword
6. Gán role = USER (mặc định)
7. Lưu User vào DB
8. Tạo JWT token từ username vừa lưu
9. Trả về AuthResponse { accessToken, tokenType="Bearer", user: UserResponse }
```

### Các Điều Kiện Kiểm Tra

| Điều kiện | Kết quả |
|---|---|
| `username` đã tồn tại | `409 Conflict` |
| `email` đã tồn tại | `409 Conflict` |
| Hợp lệ | `201 Created` + JWT |

---

## API 2 — Đăng Nhập

```
POST /api/auth/login
```

### Luồng Nghiệp Vụ

```
1. Nhận LoginRequest (usernameOrEmail, password)
2. Gọi AuthenticationManager.authenticate() với UsernamePasswordAuthenticationToken
   → Spring Security tự xác minh password qua UserDetailsService
   → Nếu sai → Spring ném AuthenticationException → 401 Unauthorized
3. Tìm User theo username, nếu không thấy thì tìm theo email
   → Không tìm thấy → 404 NOT_FOUND
4. Tạo JWT token từ username
5. Trả về AuthResponse { accessToken, tokenType="Bearer", user: UserResponse }
```

### Lưu Ý

- Trường `usernameOrEmail` có thể nhận cả username lẫn địa chỉ email.
- Spring Security tự xử lý so khớp password bằng `PasswordEncoder` (BCrypt).

---

## API 3 — Lấy Thông Tin Người Dùng Hiện Tại

```
GET /api/auth/me
Authorization: Bearer <token>
```

### Luồng Nghiệp Vụ

```
1. Lấy username từ JWT (Spring Security SecurityContextHolder)
2. Truy vấn User theo username
   → Không tìm thấy → 404 NOT_FOUND
3. Map User → UserResponse
4. Trả về UserResponse
```

---

## API 4 — Quên Mật Khẩu

```
POST /api/auth/forgot-password
```

### Luồng Nghiệp Vụ

```
1. Nhận ForgotPasswordRequest (email)
2. Tìm User theo email
   → KHÔNG tìm thấy → log cảnh báo rồi RETURN 200 (không lộ thông tin user tồn tại hay không)
3. Vô hiệu hóa tất cả token reset cũ của user (invalidateAllByUserId)
4. Tạo raw token ngẫu nhiên (UUID / alphanumeric)
5. Lưu PasswordResetToken {user, token, expiresAt = now + resetTokenExpiryMinutes, used = false}
6. Gửi email chứa link: {resetPasswordBaseUrl}?token={rawToken}
7. Trả về 200 OK (không tiết lộ email có tồn tại không)
```

### Bảo Mật

- **Silent success**: API luôn trả `200 OK` dù email không tồn tại → tránh user enumeration attack.
- Token có thời hạn cấu hình qua `app.reset-password.expiry-minutes` (mặc định: 30 phút).

---

## API 5 — Xác Minh Token Reset Mật Khẩu

```
GET /api/auth/verify-reset-token?token={token}
```

### Luồng Nghiệp Vụ

```
1. Nhận token từ query param
2. Tìm PasswordResetToken theo token
3. Validate token:
   a. Không tìm thấy → lỗi
   b. Đã được dùng (used=true) → lỗi
   c. Đã hết hạn (expiresAt < now) → lỗi
4. Mask email: "example@gmail.com" → "ex****@gmail.com"
5. Trả về VerifyTokenResponse { maskedEmail, expiresAt }
```

---

## API 6 — Đặt Lại Mật Khẩu

```
POST /api/auth/reset-password
```

### Luồng Nghiệp Vụ

```
1. Nhận ResetPasswordRequest (token, newPassword, confirmPassword)
2. Tìm PasswordResetToken theo token
3. Lấy User từ token
4. Validate toàn diện:
   a. Token không tồn tại → lỗi
   b. Token đã dùng (used=true) → lỗi
   c. Token hết hạn → lỗi
   d. newPassword != confirmPassword → lỗi
5. Encode newPassword bằng BCrypt, cập nhật hashPassword của User
6. Lưu User
7. Đánh dấu token.used = true, lưu lại
8. Trả về 200 OK
```

### Các Điều Kiện Kiểm Tra

| Điều kiện | Kết quả |
|---|---|
| Token không tồn tại | `400 Bad Request` |
| Token đã được sử dụng | `400 Bad Request` |
| Token đã hết hạn | `400 Bad Request` |
| `newPassword != confirmPassword` | `400 Bad Request` |
| Hợp lệ | `200 OK` |
