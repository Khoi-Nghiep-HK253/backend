# 👤 User API

[← Về tổng quan](./README.md)

---

## GET `/users/{id}` — Lấy thông tin người dùng

**Auth required**: ✅ Bearer Token

### Path Parameters
| Param | Type | Mô tả |
|---|---|---|
| `id` | integer | ID của user |

### Response `200 OK`
```json
{
  "status": 200,
  "message": "User retrieved successfully",
  "data": {
    "id": 1,
    "username": "hungtri",
    "email": "hung@example.com",
    "firstname": "Hùng",
    "lastname": "Trí",
    "phone": "0912345678",
    "avatar": "https://..."
  }
}
```

---

## PUT `/users/{id}` — Cập nhật thông tin cá nhân

**Auth required**: ✅ Bearer Token | **Phân quyền**: Chỉ chủ tài khoản

### Path Parameters
| Param | Type | Mô tả |
|---|---|---|
| `id` | integer | ID của user |

### Request Body
```json
{
  "firstname": "Hùng",
  "lastname": "Trí",
  "phone": "0912345678",
  "avatar": "https://cdn.example.com/avatar.jpg"
}
```

| Field | Type | Required | Mô tả |
|---|---|---|---|
| `firstname` | string | ❌ | Tên |
| `lastname` | string | ❌ | Họ |
| `phone` | string | ❌ | Số điện thoại |
| `avatar` | string | ❌ | URL ảnh đại diện |

### Response `200 OK`
```json
{
  "status": 200,
  "message": "User updated successfully",
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
| `403` | Cố sửa thông tin user khác |
| `404` | User không tồn tại |

---

## PATCH `/users/{id}/password` — Đổi mật khẩu

**Auth required**: ✅ Bearer Token | **Phân quyền**: Chỉ chủ tài khoản

### Request Body
```json
{
  "currentPassword": "123456",
  "newPassword": "newpass123"
}
```

### Response `200 OK`
```json
{
  "status": 200,
  "message": "Password changed successfully",
  "data": null
}
```

### Lỗi thường gặp
| Status | Trường hợp |
|---|---|
| `400` | `currentPassword` không khớp |
| `403` | Cố đổi mật khẩu user khác |
