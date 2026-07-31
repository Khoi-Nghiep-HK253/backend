# ✅ Settlement API

[← Về tổng quan](./README.md)

---

> ## 📌 Ghi chú
> Settlement là giao dịch **xác nhận đã trả nợ**. Sau khi tạo Settlement thành công,
> khoản Debt tương ứng sẽ chuyển sang trạng thái `SETTLED`.

---

## POST `/groups/{groupId}/settlements` — Ghi nhận thanh toán nợ

**Auth required**: ✅ Bearer Token | **Phân quyền**: Thành viên của nhóm (người nợ)

### Request Body
```json
{
  "debtId": 5,
  "amount": "250000.00",
  "method": "TRANSFER",
  "note": "Chuyển khoản qua MB Bank lúc 10h sáng",
  "paidAt": "2026-08-02T10:00:00"
}
```

| Field | Type | Required | Mô tả |
|---|---|---|---|
| `debtId` | integer | ✅ | ID khoản nợ cần thanh toán |
| `amount` | decimal | ✅ | Số tiền thanh toán |
| `method` | string | ❌ | Phương thức: `CASH`, `TRANSFER` (mặc định: `CASH`) |
| `note` | string | ❌ | Ghi chú thêm |
| `paidAt` | datetime | ❌ | Thời điểm thanh toán (mặc định: now) |

> **Validation**: `amount` phải <= số tiền còn nợ trong `debtId`

### Response `201 Created`
```json
{
  "status": 201,
  "message": "Settlement recorded successfully",
  "data": {
    "id": 8,
    "debt": {
      "id": 5,
      "newStatus": "SETTLED"
    },
    "fromUser": { "id": 3, "username": "anle" },
    "toUser":   { "id": 1, "username": "hungtri" },
    "amount": "250000.00",
    "method": "TRANSFER",
    "note": "Chuyển khoản qua MB Bank lúc 10h sáng",
    "paidAt": "2026-08-02T10:00:00",
    "createdAt": "2026-08-02T10:05:00"
  }
}
```

### Lỗi thường gặp
| Status | Trường hợp |
|---|---|
| `400` | Khoản nợ đã được thanh toán (`SETTLED`) |
| `400` | `amount` vượt quá số tiền còn nợ |
| `403` | Không phải người trong khoản nợ này |
| `404` | `debtId` không tồn tại |

---

## GET `/groups/{groupId}/settlements` — Lịch sử thanh toán của nhóm

**Auth required**: ✅ Bearer Token | **Phân quyền**: Thành viên của nhóm

### Query Parameters
| Param | Type | Mô tả |
|---|---|---|
| `fromUserId` | integer | Lọc theo người đã trả |
| `toUserId` | integer | Lọc theo người nhận tiền |
| `fromDate` | date | Lọc từ ngày |
| `toDate` | date | Lọc đến ngày |
| `page` | integer | Trang (mặc định: 0) |
| `size` | integer | Số item (mặc định: 20) |

### Response `200 OK`
```json
{
  "status": 200,
  "message": "Settlements retrieved successfully",
  "data": {
    "content": [
      {
        "id": 8,
        "fromUser": { "id": 3, "username": "anle" },
        "toUser":   { "id": 1, "username": "hungtri" },
        "amount": "250000.00",
        "method": "TRANSFER",
        "paidAt": "2026-08-02T10:00:00"
      }
    ],
    "totalElements": 1,
    "totalPages": 1,
    "page": 0,
    "size": 20
  }
}
```

---

## GET `/groups/{groupId}/settlements/{settlementId}` — Chi tiết giao dịch thanh toán

**Auth required**: ✅ Bearer Token | **Phân quyền**: Thành viên của nhóm

### Response `200 OK`
```json
{
  "status": 200,
  "message": "Settlement retrieved successfully",
  "data": {
    "id": 8,
    "debt": { "id": 5, "amount": "250000.00" },
    "group": { "id": 10, "name": "Du lịch Đà Lạt 2026" },
    "fromUser": { "id": 3, "username": "anle" },
    "toUser":   { "id": 1, "username": "hungtri" },
    "amount": "250000.00",
    "method": "TRANSFER",
    "note": "Chuyển khoản qua MB Bank lúc 10h sáng",
    "paidAt": "2026-08-02T10:00:00",
    "createdAt": "2026-08-02T10:05:00"
  }
}
```
