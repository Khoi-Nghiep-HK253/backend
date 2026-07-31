# 🔐 Authentication API

[← Về tổng quan](./README.md)

---

## POST `/auth/register` — Đăng ký tài khoản

**Auth required**: ❌ Không

### Request Body
```json
{
  "username": "hungtri",
  "email": "hung@example.com",
  "password": "123456",
  "firstname": "Hùng",
  "lastname": "Trí",
  "phone": "0912345678"
}
```

| Field | Type | Required | Mô tả |
|---|---|---|---|
| `username` | string | ✅ | 3–50 ký tự, không dấu cách |
| `email` | string | ✅ | Định dạng email hợp lệ |
| `password` | string | ✅ | Tối thiểu 6 ký tự |
| `firstname` | string | ❌ | Tên |
| `lastname` | string | ❌ | Họ |
| `phone` | string | ❌ | Số điện thoại |

### Response `201 Created`
```json
{
  "status": 201,
  "message": "User registered successfully",
  "data": {
    "accessToken": "eyJhbGci...",
    "tokenType": "Bearer",
    "user": {
      "id": 1,
      "username": "hungtri",
      "email": "hung@example.com",
      "firstname": "Hùng",
      "lastname": "Trí",
      "phone": "0912345678"
    }
  }
}
```

### Lỗi thường gặp
| Status | Trường hợp |
|---|---|
| `400` | Thiếu field bắt buộc, sai định dạng email |
| `409` | Username hoặc email đã tồn tại |

---

## POST `/auth/login` — Đăng nhập

**Auth required**: ❌ Không

### Request Body
```json
{
  "usernameOrEmail": "hungtri",
  "password": "123456"
}
```

| Field | Type | Required | Mô tả |
|---|---|---|---|
| `usernameOrEmail` | string | ✅ | Username hoặc email |
| `password` | string | ✅ | Mật khẩu |

### Response `200 OK`
```json
{
  "status": 200,
  "message": "User logged in successfully",
  "data": {
    "accessToken": "eyJhbGci...",
    "tokenType": "Bearer",
    "user": {
      "id": 1,
      "username": "hungtri",
      "email": "hung@example.com",
      "firstname": "Hùng",
      "lastname": "Trí"
    }
  }
}
```

### Lỗi thường gặp
| Status | Trường hợp |
|---|---|
| `401` | Sai username/email hoặc mật khẩu |

---

## GET `/auth/me` — Lấy thông tin user hiện tại

**Auth required**: ✅ Bearer Token

### Response `200 OK`
```json
{
  "status": 200,
  "message": "Current user retrieved successfully",
  "data": {
    "id": 1,
    "username": "hungtri",
    "email": "hung@example.com",
    "firstname": "Hùng",
    "lastname": "Trí",
    "phone": "0912345678"
  }
}
```

### Lỗi thường gặp
| Status | Trường hợp |
|---|---|
| `401` | Token không hợp lệ hoặc hết hạn |

---

## Luồng quên mật khẩu

```
[User nhập email]
       ↓
POST /auth/forgot-password
       ↓
Hệ thống gửi email chứa link reset (có token)
       ↓
[User nhấn link → nhập mật khẩu mới]
       ↓
POST /auth/reset-password  (token + newPassword)
       ↓
Mật khẩu được đổi, token bị vô hiệu hoá
```

---

## POST `/auth/forgot-password` — Yêu cầu đặt lại mật khẩu

**Auth required**: ❌ Không

> Hệ thống sẽ gửi email chứa đường dẫn reset mật khẩu đến địa chỉ email được cung cấp.
> **Để tránh lộ thông tin**, response luôn trả `200 OK` dù email có tồn tại hay không.

### Request Body
```json
{
  "email": "hung@example.com"
}
```

| Field | Type | Required | Mô tả |
|---|---|---|---|
| `email` | string | ✅ | Địa chỉ email đã đăng ký |

### Response `200 OK`
```json
{
  "status": 200,
  "message": "If this email is registered, a password reset link has been sent.",
  "data": null
}
```

### Lỗi thường gặp
| Status | Trường hợp |
|---|---|
| `400` | Sai định dạng email |
| `429` | Gửi quá nhiều yêu cầu trong thời gian ngắn (rate limit) |

---

## GET `/auth/reset-password/verify` — Kiểm tra token hợp lệ

**Auth required**: ❌ Không

> Dùng để frontend kiểm tra token còn hiệu lực trước khi hiển thị form nhập mật khẩu mới.

### Query Parameters
| Param | Type | Required | Mô tả |
|---|---|---|---|
| `token` | string | ✅ | Token từ link trong email |

### Response `200 OK` — Token hợp lệ
```json
{
  "status": 200,
  "message": "Token is valid",
  "data": {
    "email": "h***@example.com",
    "expiresAt": "2026-07-31T16:00:00"
  }
}
```

### Lỗi thường gặp
| Status | Trường hợp |
|---|---|
| `400` | Token không hợp lệ hoặc đã được sử dụng |
| `410` | Token đã hết hạn |

---

## POST `/auth/reset-password` — Đặt lại mật khẩu mới

**Auth required**: ❌ Không

> Sau khi đặt lại thành công, token bị **vô hiệu hoá ngay lập tức**.
> Tất cả JWT đang active của user cũng nên bị **invalidate** (nếu có blacklist).

### Request Body
```json
{
  "token": "reset_tok_abc123xyz",
  "newPassword": "newSecurePass456",
  "confirmPassword": "newSecurePass456"
}
```

| Field | Type | Required | Mô tả |
|---|---|---|---|
| `token` | string | ✅ | Token từ link trong email |
| `newPassword` | string | ✅ | Mật khẩu mới (tối thiểu 6 ký tự) |
| `confirmPassword` | string | ✅ | Nhập lại mật khẩu mới để xác nhận |

### Response `200 OK`
```json
{
  "status": 200,
  "message": "Password has been reset successfully. Please log in with your new password.",
  "data": null
}
```

### Lỗi thường gặp
| Status | Trường hợp |
|---|---|
| `400` | `newPassword` và `confirmPassword` không khớp |
| `400` | Token không hợp lệ hoặc đã được sử dụng |
| `400` | Mật khẩu mới trùng với mật khẩu cũ |
| `410` | Token đã hết hạn — yêu cầu gửi lại email |
