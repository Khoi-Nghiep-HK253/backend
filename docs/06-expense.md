# 💸 Expense API

[← Về tổng quan](./README.md)

---

> ## ⚙️ Luồng tạo khoản chi
> 1. **Tạo expense** (mô tả, số tiền, danh mục)
> 2. **Khai báo payer(s)** — ai đã bỏ tiền ra
> 3. **Khai báo share(s)** — ai phải chia tiền, bao nhiêu
> 4. **Hệ thống tự tính Debt** dựa trên (payer − share)

---

## POST `/groups/{groupId}/expenses` — Tạo khoản chi mới

**Auth required**: ✅ Bearer Token | **Phân quyền**: Thành viên của nhóm

### Request Body
```json
{
  "description": "Ăn lẩu thái tối ngày 1",
  "totalAmount": "1000000.00",
  "currencyId": 1,
  "categoryId": 2,
  "expenseDate": "2026-08-01",
  "payers": [
    { "userId": 1, "amount": "800000.00" },
    { "userId": 2, "amount": "200000.00" }
  ],
  "shares": [
    { "userId": 1, "amount": "250000.00" },
    { "userId": 2, "amount": "250000.00" },
    { "userId": 3, "amount": "250000.00" },
    { "userId": 4, "amount": "250000.00" }
  ]
}
```

| Field | Type | Required | Mô tả |
|---|---|---|---|
| `description` | string | ✅ | Mô tả khoản chi |
| `totalAmount` | decimal | ✅ | Tổng số tiền |
| `currencyId` | integer | ✅ | Đơn vị tiền tệ |
| `categoryId` | integer | ❌ | Danh mục chi tiêu |
| `expenseDate` | date | ✅ | Ngày phát sinh chi tiêu |
| `payers` | array | ✅ | Danh sách người bỏ tiền ra |
| `payers[].userId` | integer | ✅ | ID người thanh toán |
| `payers[].amount` | decimal | ✅ | Số tiền họ đã trả |
| `shares` | array | ✅ | Danh sách người chia chi phí |
| `shares[].userId` | integer | ✅ | ID người phải chịu chi phí |
| `shares[].amount` | decimal | ✅ | Số tiền họ phải chịu |

> **Validation**: `sum(payers.amount)` phải bằng `totalAmount`  
> **Validation**: `sum(shares.amount)` phải bằng `totalAmount`

### Response `201 Created`
```json
{
  "status": 201,
  "message": "Expense created successfully",
  "data": {
    "id": 20,
    "group": { "id": 10, "name": "Du lịch Đà Lạt 2026" },
    "description": "Ăn lẩu thái tối ngày 1",
    "totalAmount": "1000000.00",
    "currency": { "id": 1, "code": "VND", "symbol": "₫" },
    "category": { "id": 2, "name": "Ăn uống" },
    "expenseDate": "2026-08-01",
    "payers": [
      { "userId": 1, "username": "hungtri", "amount": "800000.00" },
      { "userId": 2, "username": "khanhnt", "amount": "200000.00" }
    ],
    "shares": [
      { "userId": 1, "username": "hungtri", "amount": "250000.00" },
      { "userId": 2, "username": "khanhnt", "amount": "250000.00" },
      { "userId": 3, "username": "anle",    "amount": "250000.00" },
      { "userId": 4, "username": "binhpham","amount": "250000.00" }
    ],
    "debtsCreated": [
      { "fromUserId": 3, "toUserId": 1, "amount": "250000.00" },
      { "fromUserId": 4, "toUserId": 1, "amount": "250000.00" }
    ],
    "createdAt": "2026-07-31T15:00:00"
  }
}
```

---

## GET `/groups/{groupId}/expenses` — Danh sách khoản chi của nhóm

**Auth required**: ✅ Bearer Token | **Phân quyền**: Thành viên của nhóm

### Query Parameters
| Param | Type | Mô tả |
|---|---|---|
| `page` | integer | Trang (mặc định: 0) |
| `size` | integer | Số item (mặc định: 20) |
| `categoryId` | integer | Lọc theo danh mục |
| `fromDate` | date | Lọc từ ngày (`YYYY-MM-DD`) |
| `toDate` | date | Lọc đến ngày (`YYYY-MM-DD`) |

### Response `200 OK`
```json
{
  "status": 200,
  "message": "Expenses retrieved successfully",
  "data": {
    "content": [
      {
        "id": 20,
        "description": "Ăn lẩu thái tối ngày 1",
        "totalAmount": "1000000.00",
        "currency": { "code": "VND" },
        "category": { "name": "Ăn uống" },
        "expenseDate": "2026-08-01",
        "payerCount": 2,
        "shareCount": 4
      }
    ],
    "totalElements": 5,
    "totalPages": 1,
    "page": 0,
    "size": 20
  }
}
```

---

## GET `/groups/{groupId}/expenses/{expenseId}` — Chi tiết khoản chi

**Auth required**: ✅ Bearer Token | **Phân quyền**: Thành viên của nhóm

### Response `200 OK`
```json
{
  "status": 200,
  "message": "Expense retrieved successfully",
  "data": {
    "id": 20,
    "description": "Ăn lẩu thái tối ngày 1",
    "totalAmount": "1000000.00",
    "currency": { "id": 1, "code": "VND" },
    "category": { "id": 2, "name": "Ăn uống" },
    "expenseDate": "2026-08-01",
    "payers": [ ... ],
    "shares": [ ... ],
    "createdAt": "2026-07-31T15:00:00",
    "updatedAt": "2026-07-31T15:00:00"
  }
}
```

---

## PUT `/groups/{groupId}/expenses/{expenseId}` — Cập nhật khoản chi

**Auth required**: ✅ Bearer Token | **Phân quyền**: Người tạo khoản chi hoặc ADMIN

> ⚠️ Cập nhật sẽ **xoá và tính lại toàn bộ Debt** liên quan đến khoản chi này.

### Request Body
Giống với POST, gửi toàn bộ dữ liệu mới.

### Response `200 OK`
```json
{
  "status": 200,
  "message": "Expense updated successfully",
  "data": { ...expense object... }
}
```

---

## DELETE `/groups/{groupId}/expenses/{expenseId}` — Xoá khoản chi

**Auth required**: ✅ Bearer Token | **Phân quyền**: Người tạo hoặc ADMIN

> ⚠️ Xoá khoản chi sẽ xoá tất cả Payer, Share và Debt liên quan (nếu chưa có Settlement).

### Response `200 OK`
```json
{
  "status": 200,
  "message": "Expense deleted successfully",
  "data": null
}
```

### Lỗi thường gặp
| Status | Trường hợp |
|---|---|
| `400` | Khoản chi đã có Settlement — không thể xoá |
| `403` | Không có quyền xoá |
