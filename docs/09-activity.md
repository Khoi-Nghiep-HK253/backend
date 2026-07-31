# 📋 Activity API

[← Về tổng quan](./README.md)

---

> ## 📌 Ghi chú
> Activity được **tự động ghi** bởi hệ thống sau mỗi thao tác quan trọng.
> API này chỉ phục vụ **đọc** — không có endpoint tạo/sửa/xoá.

---

## Danh sách `entityType` và `topic`

| entityType | topic | Trigger khi nào |
|---|---|---|
| `GROUP` | `GROUP_CREATED` | Tạo nhóm mới |
| `GROUP` | `GROUP_UPDATED` | Cập nhật thông tin nhóm |
| `GROUP_MEMBER` | `MEMBER_JOINED` | Thành viên tham gia nhóm |
| `GROUP_MEMBER` | `MEMBER_LEFT` | Thành viên rời nhóm |
| `GROUP_MEMBER` | `MEMBER_ROLE_CHANGED` | Đổi vai trò thành viên |
| `INVITATION` | `INVITATION_SENT` | Gửi lời mời |
| `INVITATION` | `INVITATION_ACCEPTED` | Chấp nhận lời mời |
| `INVITATION` | `INVITATION_DECLINED` | Từ chối lời mời |
| `INVITATION` | `INVITATION_REVOKED` | Thu hồi lời mời |
| `EXPENSE` | `EXPENSE_CREATED` | Tạo khoản chi mới |
| `EXPENSE` | `EXPENSE_UPDATED` | Cập nhật khoản chi |
| `EXPENSE` | `EXPENSE_DELETED` | Xoá khoản chi |
| `SETTLEMENT` | `SETTLEMENT_CREATED` | Ghi nhận thanh toán |

---

## GET `/groups/{groupId}/activities` — Lịch sử hoạt động nhóm

**Auth required**: ✅ Bearer Token | **Phân quyền**: Thành viên của nhóm

### Query Parameters
| Param | Type | Mô tả |
|---|---|---|
| `page` | integer | Trang (mặc định: 0) |
| `size` | integer | Số item (mặc định: 30) |
| `entityType` | string | Lọc theo loại entity (`EXPENSE`, `SETTLEMENT`...) |
| `userId` | integer | Lọc theo người thực hiện |
| `fromDate` | date | Lọc từ ngày |
| `toDate` | date | Lọc đến ngày |

### Response `200 OK`
```json
{
  "status": 200,
  "message": "Activities retrieved successfully",
  "data": {
    "content": [
      {
        "id": 101,
        "user": { "id": 1, "username": "hungtri", "fullname": "Trí Hùng" },
        "entityType": "EXPENSE",
        "entityId": 20,
        "topic": "EXPENSE_CREATED",
        "description": "hungtri đã tạo khoản chi \"Ăn lẩu thái\" — 1,000,000 ₫",
        "createdAt": "2026-08-01T20:30:00"
      },
      {
        "id": 100,
        "user": { "id": 3, "username": "anle", "fullname": "Lê An" },
        "entityType": "SETTLEMENT",
        "entityId": 8,
        "topic": "SETTLEMENT_CREATED",
        "description": "anle đã thanh toán 250,000 ₫ cho hungtri",
        "createdAt": "2026-08-02T10:05:00"
      }
    ],
    "totalElements": 20,
    "totalPages": 1,
    "page": 0,
    "size": 30
  }
}
```

---

## GET `/users/{userId}/activities` — Lịch sử hoạt động cá nhân

**Auth required**: ✅ Bearer Token

> Lịch sử các thao tác của một user cụ thể (có thể xem của chính mình).

### Query Parameters
| Param | Type | Mô tả |
|---|---|---|
| `page` | integer | Trang (mặc định: 0) |
| `size` | integer | Số item (mặc định: 20) |

### Response `200 OK`
```json
{
  "status": 200,
  "message": "User activities retrieved successfully",
  "data": {
    "content": [
      {
        "id": 101,
        "entityType": "EXPENSE",
        "entityId": 20,
        "topic": "EXPENSE_CREATED",
        "description": "hungtri đã tạo khoản chi \"Ăn lẩu thái\" — 1,000,000 ₫",
        "createdAt": "2026-08-01T20:30:00"
      }
    ],
    "totalElements": 5,
    "totalPages": 1,
    "page": 0,
    "size": 20
  }
}
```
