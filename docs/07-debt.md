# 🔴 Debt API

[← Về tổng quan](./README.md)

---

> ## 📌 Ghi chú
> Debt được **tự động tạo** khi có Expense mới. Dev **không tạo Debt thủ công**.
> API này chỉ phục vụ **đọc & theo dõi** công nợ.

---

## GET `/groups/{groupId}/debts` — Danh sách công nợ của nhóm

**Auth required**: ✅ Bearer Token | **Phân quyền**: Thành viên của nhóm

### Query Parameters
| Param | Type | Mô tả |
|---|---|---|
| `status` | string | Lọc theo trạng thái: `PENDING`, `SETTLED`, `CANCELLED` |
| `userId` | integer | Lọc theo user (là người nợ hoặc chủ nợ) |

### Response `200 OK`
```json
{
  "status": 200,
  "message": "Debts retrieved successfully",
  "data": [
    {
      "id": 5,
      "expense": {
        "id": 20,
        "description": "Ăn lẩu thái tối ngày 1"
      },
      "fromUser": { "id": 3, "username": "anle", "fullname": "Lê An" },
      "toUser":   { "id": 1, "username": "hungtri", "fullname": "Trí Hùng" },
      "amount": "250000.00",
      "status": "PENDING",
      "createdAt": "2026-07-31T15:00:00"
    }
  ]
}
```

---

## GET `/groups/{groupId}/debts/summary` — Tổng hợp công nợ nhóm

**Auth required**: ✅ Bearer Token | **Phân quyền**: Thành viên của nhóm

> Trả về bảng tóm tắt: "User A nợ User B tổng bao nhiêu" (đã gộp từ nhiều expense).

### Response `200 OK`
```json
{
  "status": 200,
  "message": "Debt summary retrieved successfully",
  "data": {
    "pairs": [
      {
        "fromUser": { "id": 3, "username": "anle" },
        "toUser":   { "id": 1, "username": "hungtri" },
        "totalOwed": "500000.00",
        "currency": { "code": "VND" }
      },
      {
        "fromUser": { "id": 4, "username": "binhpham" },
        "toUser":   { "id": 1, "username": "hungtri" },
        "totalOwed": "250000.00",
        "currency": { "code": "VND" }
      }
    ]
  }
}
```

---

## GET `/groups/{groupId}/debts/me` — Công nợ của tôi trong nhóm

**Auth required**: ✅ Bearer Token | **Phân quyền**: Thành viên của nhóm

> Chỉ trả về các debt mà user hiện tại liên quan (là từ hoặc tới).

### Response `200 OK`
```json
{
  "status": 200,
  "message": "My debts retrieved successfully",
  "data": {
    "iOwe": [
      {
        "toUser": { "id": 1, "username": "hungtri" },
        "totalAmount": "500000.00",
        "debts": [ { "id": 5, "amount": "250000.00", "expenseId": 20 } ]
      }
    ],
    "owedToMe": []
  }
}
```

---

## GET `/groups/{groupId}/debts/{debtId}` — Chi tiết một khoản nợ

**Auth required**: ✅ Bearer Token | **Phân quyền**: Thành viên của nhóm

### Response `200 OK`
```json
{
  "status": 200,
  "message": "Debt retrieved successfully",
  "data": {
    "id": 5,
    "expense": {
      "id": 20,
      "description": "Ăn lẩu thái tối ngày 1",
      "expenseDate": "2026-08-01"
    },
    "fromUser": { "id": 3, "username": "anle" },
    "toUser":   { "id": 1, "username": "hungtri" },
    "amount": "250000.00",
    "status": "PENDING",
    "settlements": [],
    "createdAt": "2026-07-31T15:00:00"
  }
}
```
