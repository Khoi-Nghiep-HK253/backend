# 🙋 Group Member API

[← Về tổng quan](./README.md)

---

## GET `/groups/{groupId}/members` — Danh sách thành viên

**Auth required**: ✅ Bearer Token | **Phân quyền**: Thành viên của nhóm

### Response `200 OK`
```json
{
  "status": 200,
  "message": "Members retrieved successfully",
  "data": [
    {
      "id": 1,
      "user": {
        "id": 1,
        "username": "hungtri",
        "firstname": "Hùng",
        "lastname": "Trí",
        "avatar": "https://..."
      },
      "role": "ADMIN",
      "joinedAt": "2026-07-31T15:00:00"
    },
    {
      "id": 2,
      "user": {
        "id": 2,
        "username": "khanhnt",
        "firstname": "Khánh",
        "lastname": "Nguyễn",
        "avatar": null
      },
      "role": "MEMBER",
      "joinedAt": "2026-07-31T16:00:00"
    }
  ]
}
```

---

## PATCH `/groups/{groupId}/members/{memberId}/role` — Đổi vai trò thành viên

**Auth required**: ✅ Bearer Token | **Phân quyền**: ADMIN của nhóm

### Request Body
```json
{
  "role": "ADMIN"
}
```

| Field | Type | Values | Mô tả |
|---|---|---|---|
| `role` | string | `ADMIN`, `MEMBER` | Vai trò mới |

### Response `200 OK`
```json
{
  "status": 200,
  "message": "Member role updated successfully",
  "data": {
    "id": 2,
    "userId": 2,
    "role": "ADMIN"
  }
}
```

### Lỗi thường gặp
| Status | Trường hợp |
|---|---|
| `400` | `role` không hợp lệ |
| `403` | Không phải ADMIN |
| `404` | Thành viên không tồn tại trong nhóm |

---

## DELETE `/groups/{groupId}/members/{memberId}` — Xoá thành viên khỏi nhóm

**Auth required**: ✅ Bearer Token | **Phân quyền**: ADMIN của nhóm hoặc chính thành viên đó (tự rời nhóm)

> ⚠️ Không thể xoá ADMIN duy nhất của nhóm.

### Response `200 OK`
```json
{
  "status": 200,
  "message": "Member removed from group successfully",
  "data": null
}
```

### Lỗi thường gặp
| Status | Trường hợp |
|---|---|
| `400` | Cố xoá ADMIN cuối cùng của nhóm |
| `403` | Không có quyền xoá thành viên này |
| `404` | Thành viên không tồn tại |
