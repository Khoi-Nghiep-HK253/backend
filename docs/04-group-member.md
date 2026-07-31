# 🙋 Group Member API

[← Về tổng quan](./README.md)

---

## GET `/groups/{groupId}/members` — Danh sách thành viên

**Auth required**: ✅ Bearer Token | **Phân quyền**: Thành viên của nhóm

### Response `200 OK`
```json
{
  "status": 200,
  "message": "Group members retrieved successfully",
  "data": [
    {
      "id": 1,
      "user": {
        "id": 1,
        "username": "hungtri",
        "firstname": "Hùng",
        "lastname": "Trí"
      },
      "role": "OWNER",
      "joinedAt": "2026-07-31T15:00:00"
    },
    {
      "id": 2,
      "user": {
        "id": 2,
        "username": "khanhnt",
        "firstname": "Khánh",
        "lastname": "Nguyễn"
      },
      "role": "MEMBER",
      "joinedAt": "2026-07-31T16:00:00"
    }
  ]
}
```

---

## POST `/groups/{groupId}/members` — Thêm thành viên vào nhóm

**Auth required**: ✅ Bearer Token | **Phân quyền**: OWNER của nhóm

### Request Body
```json
{
  "userId": 5
}
```

| Field | Type | Required | Mô tả |
|---|---|---|---|
| `userId` | integer | ✅ | ID người dùng muốn thêm |

### Response `201 Created`
```json
{
  "status": 201,
  "message": "Member added to group successfully",
  "data": {
    "id": 5,
    "user": {
      "id": 5,
      "username": "newuser",
      "firstname": "New",
      "lastname": "User"
    },
    "role": "MEMBER",
    "joinedAt": "2026-07-31T18:00:00"
  }
}
```

### Lỗi thường gặp
| Status | Trường hợp |
|---|---|
| `400` | User đã là thành viên của nhóm |
| `403` | Không phải OWNER của nhóm |
| `404` | User hoặc Group không tồn tại |

---

## PUT `/groups/{groupId}/members/{memberId}/role` — Đổi vai trò thành viên

**Auth required**: ✅ Bearer Token | **Phân quyền**: OWNER của nhóm

### Request Body
```json
{
  "role": "OWNER"
}
```

| Field | Type | Values | Mô tả |
|---|---|---|---|
| `role` | string | `OWNER`, `MEMBER` | Vai trò mới |

### Response `200 OK`
```json
{
  "status": 200,
  "message": "Member role updated successfully",
  "data": {
    "id": 2,
    "user": {
      "id": 2,
      "username": "khanhnt",
      "firstname": "Khánh",
      "lastname": "Nguyễn"
    },
    "role": "OWNER",
    "joinedAt": "2026-07-31T16:00:00"
  }
}
```

### Lỗi thường gặp
| Status | Trường hợp |
|---|---|
| `400` | `role` không hợp lệ hoặc hạ cấp OWNER duy nhất của nhóm |
| `403` | Không phải OWNER của nhóm |
| `404` | Thành viên không tồn tại trong nhóm |

---

## DELETE `/groups/{groupId}/members/{memberId}` — Xoá thành viên khỏi nhóm

**Auth required**: ✅ Bearer Token | **Phân quyền**: OWNER của nhóm hoặc chính thành viên đó (tự rời nhóm)

> ⚠️ Không thể xoá OWNER duy nhất của nhóm.

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
| `400` | Cố xoá OWNER cuối cùng của nhóm |
| `403` | Không có quyền xoá thành viên này |
| `404` | Thành viên không tồn tại |
