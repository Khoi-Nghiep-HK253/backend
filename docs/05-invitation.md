# 📨 Invitation API

[← Về tổng quan](./README.md)

---

## POST `/groups/{groupId}/invitations` — Gửi lời mời

**Auth required**: ✅ Bearer Token | **Phân quyền**: ADMIN của nhóm

### Request Body
```json
{
  "inviteeId": 5,
  "message": "Tham gia chuyến đi Đà Lạt cùng bọn mình nhé!",
  "expiresAt": "2026-08-01T00:00:00"
}
```

| Field | Type | Required | Mô tả |
|---|---|---|---|
| `inviteeId` | integer | ✅ | ID người được mời |
| `message` | string | ❌ | Lời nhắn kèm lời mời |
| `expiresAt` | datetime | ❌ | Thời điểm lời mời hết hạn |

### Response `201 Created`
```json
{
  "status": 201,
  "message": "Invitation sent successfully",
  "data": {
    "id": 3,
    "group": { "id": 10, "name": "Du lịch Đà Lạt 2026" },
    "inviter": { "id": 1, "username": "hungtri" },
    "invitee": { "id": 5, "username": "binhpham" },
    "status": "PENDING",
    "token": "tok_xyz789",
    "message": "Tham gia chuyến đi Đà Lạt cùng bọn mình nhé!",
    "expiresAt": "2026-08-01T00:00:00",
    "createdAt": "2026-07-31T15:00:00"
  }
}
```

### Lỗi thường gặp
| Status | Trường hợp |
|---|---|
| `400` | User đã là thành viên của nhóm |
| `400` | Đã có lời mời PENDING cho user này |
| `403` | Không phải ADMIN của nhóm |
| `404` | User được mời không tồn tại |

---

## GET `/groups/{groupId}/invitations` — Danh sách lời mời của nhóm

**Auth required**: ✅ Bearer Token | **Phân quyền**: ADMIN của nhóm

### Query Parameters
| Param | Type | Mô tả |
|---|---|---|
| `status` | string | Lọc theo trạng thái: `PENDING`, `ACCEPTED`, `DECLINED`, `EXPIRED`, `REVOKED` |

### Response `200 OK`
```json
{
  "status": 200,
  "message": "Invitations retrieved successfully",
  "data": [
    {
      "id": 3,
      "invitee": { "id": 5, "username": "binhpham" },
      "status": "PENDING",
      "expiresAt": "2026-08-01T00:00:00",
      "createdAt": "2026-07-31T15:00:00"
    }
  ]
}
```

---

## GET `/invitations/me` — Lời mời của tôi (inbox)

**Auth required**: ✅ Bearer Token

> Lấy danh sách lời mời mà user hiện tại nhận được.

### Query Parameters
| Param | Type | Mô tả |
|---|---|---|
| `status` | string | Lọc theo trạng thái (mặc định: `PENDING`) |

### Response `200 OK`
```json
{
  "status": 200,
  "message": "My invitations retrieved successfully",
  "data": [
    {
      "id": 3,
      "group": { "id": 10, "name": "Du lịch Đà Lạt 2026" },
      "inviter": { "id": 1, "username": "hungtri" },
      "status": "PENDING",
      "message": "Tham gia chuyến đi Đà Lạt cùng bọn mình nhé!",
      "expiresAt": "2026-08-01T00:00:00"
    }
  ]
}
```

---

## PATCH `/invitations/{invitationId}/accept` — Chấp nhận lời mời

**Auth required**: ✅ Bearer Token | **Phân quyền**: Người được mời (invitee)

> Sau khi chấp nhận, user tự động được thêm vào nhóm với role `MEMBER`.

### Response `200 OK`
```json
{
  "status": 200,
  "message": "Invitation accepted successfully",
  "data": {
    "invitationId": 3,
    "status": "ACCEPTED",
    "joinedGroup": { "id": 10, "name": "Du lịch Đà Lạt 2026" }
  }
}
```

### Lỗi thường gặp
| Status | Trường hợp |
|---|---|
| `400` | Lời mời đã hết hạn hoặc không còn PENDING |
| `403` | Không phải người được mời |

---

## PATCH `/invitations/{invitationId}/decline` — Từ chối lời mời

**Auth required**: ✅ Bearer Token | **Phân quyền**: Người được mời (invitee)

### Response `200 OK`
```json
{
  "status": 200,
  "message": "Invitation declined successfully",
  "data": { "invitationId": 3, "status": "DECLINED" }
}
```

---

## PATCH `/invitations/{invitationId}/revoke` — Thu hồi lời mời

**Auth required**: ✅ Bearer Token | **Phân quyền**: ADMIN của nhóm

### Response `200 OK`
```json
{
  "status": 200,
  "message": "Invitation revoked successfully",
  "data": { "invitationId": 3, "status": "REVOKED" }
}
```
