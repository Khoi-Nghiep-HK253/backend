# Divvy API Documentation

> **Base URL**: `http://localhost:8080/api`
> **Auth**: Bearer JWT Token (`Authorization: Bearer <token>`)
> **Content-Type**: `application/json`

---

## Tổng quan tài liệu

Bộ tài liệu này mô tả thiết kế REST API cho ứng dụng **Divvy** — sổ quỹ thông minh dành cho nhóm.

---

## Danh sách module

| Module | File | Mô tả |
|---|---|---|
| 🔐 Authentication | [01-auth.md](./01-auth.md) | Đăng ký, đăng nhập, thông tin user |
| 👤 User | [02-user.md](./02-user.md) | Quản lý hồ sơ cá nhân |
| 👥 Group | [03-group.md](./03-group.md) | Tạo, cập nhật, xoá nhóm |
| 🙋 Group Member | [04-group-member.md](./04-group-member.md) | Quản lý thành viên trong nhóm |
| 📨 Invitation | [05-invitation.md](./05-invitation.md) | Gửi & xử lý lời mời tham gia nhóm |
| 💸 Expense | [06-expense.md](./06-expense.md) | Ghi nhận và quản lý khoản chi |
| 🔴 Debt | [07-debt.md](./07-debt.md) | Xem và theo dõi công nợ |
| ✅ Settlement | [08-settlement.md](./08-settlement.md) | Ghi nhận thanh toán nợ |
| 📋 Activity | [09-activity.md](./09-activity.md) | Lịch sử hoạt động nhóm |
| 🗂️ Reference | [10-reference.md](./10-reference.md) | Enum values, error codes, cấu trúc chung |

---

## Cấu trúc Response chuẩn

### Success
```json
{
  "status": 200,
  "message": "Success message",
  "data": { ... }
}
```

### Error
```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Mô tả lỗi cụ thể",
  "timestamp": "2026-07-31T15:00:00"
}
```

---

## Luồng nghiệp vụ tổng quan

```
[Đăng ký / Đăng nhập]
        ↓
[Tạo nhóm] → [Mời thành viên] → [Thành viên chấp nhận]
        ↓
[Ghi nhận khoản chi]
  ├── Payer: ai đã bỏ tiền ra?
  └── Share: ai phải chia tiền?
        ↓
[Hệ thống tự tính Debt]
        ↓
[Thành viên trả nợ → Settlement]
        ↓
[Xem Activity log để đối soát]
```

---

## HTTP Status Codes

| Code | Ý nghĩa |
|---|---|
| `200 OK` | Thành công |
| `201 Created` | Tạo mới thành công |
| `400 Bad Request` | Dữ liệu đầu vào không hợp lệ |
| `401 Unauthorized` | Chưa xác thực hoặc token không hợp lệ |
| `403 Forbidden` | Không có quyền thực hiện thao tác |
| `404 Not Found` | Tài nguyên không tồn tại |
| `409 Conflict` | Dữ liệu bị trùng (email, username...) |
| `500 Internal Server Error` | Lỗi hệ thống |
