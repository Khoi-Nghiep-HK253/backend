# Phân Hệ Debt — Nghiệp Vụ Chi Tiết

## Tổng Quan

Phân hệ **Debt** quản lý công nợ sinh ra từ chi tiêu. Debt **không được tạo thủ công** — chúng được hệ thống tự động tạo khi tạo/sửa chi tiêu (Expense). API này chỉ cung cấp các chức năng đọc và tổng hợp.

---

## Vòng Đời Của Debt (DebtStatus)

```
  [Tạo từ Expense]
        ↓
    PENDING  ──── (Tạo Settlement) ──→  SETTLED
```

- `PENDING`: Công nợ đang chờ thanh toán.
- `SETTLED`: Đã được thanh toán thông qua Settlement.

---

## API 1 — Lấy Danh Sách Debt Của Nhóm

```
GET /api/groups/{groupId}/debts?status=PENDING&userId=
Authorization: Bearer <token>
```

### Luồng Nghiệp Vụ

```
1. Lấy username từ JWT → tìm caller
2. Tìm Group theo groupId → 404 nếu không có
3. Kiểm tra caller là thành viên → 403 nếu không phải
4. Xây dựng JPA Specification với filter:
   - expense.group.id = groupId (bắt buộc)
   - status nếu có query param
   - userId: filter các debt mà userId là fromUser HOẶC toUser
5. Lấy danh sách Debt theo spec
6. Map từng Debt → DebtItemResponse
7. Trả về List<DebtItemResponse>
```

### Query Params

| Param | Kiểu | Mô tả |
|---|---|---|
| `status` | `PENDING \| SETTLED` | Lọc theo trạng thái |
| `userId` | Integer | Chỉ lấy debt liên quan đến userId này |

---

## API 2 — Xem Tổng Hợp Nợ Theo Cặp Người Dùng

```
GET /api/groups/{groupId}/debts/summary
Authorization: Bearer <token>
```

### Luồng Nghiệp Vụ

```
1. Xác thực caller là thành viên group
2. Lấy tất cả Debt có status = PENDING của group
3. Gom nhóm theo cặp (fromUser, toUser):
   - Key: "{fromUserId}_{toUserId}"
   - Cộng dồn amount của tất cả Debt trong cặp
4. Lấy currency code từ group.defaultCurrency (mặc định "VND")
5. Build DebtPairSummaryResponse cho từng cặp:
   { fromUser, toUser, totalOwed, currency }
6. Trả về DebtGroupSummaryResponse { pairs: [...] }
```

### Ý Nghĩa Nghiệp Vụ

Thay vì hiển thị từng debt riêng lẻ, API này **gom tất cả PENDING debt** giữa hai người lại thành một con số tổng. Ví dụ:

```
Debt 1: A → B: 50k (từ expense "Ăn trưa")
Debt 2: A → B: 30k (từ expense "Cà phê")
→ Summary: A nợ B tổng 80k
```

---

## API 3 — Xem Nợ Của Tôi

```
GET /api/groups/{groupId}/debts/me
Authorization: Bearer <token>
```

### Luồng Nghiệp Vụ

```
1. Xác thực caller là thành viên group
2. Lấy tất cả Debt PENDING mà caller là fromUser HOẶC toUser
3. Phân loại:
   - iOwe: caller là fromUser (tôi nợ người khác)
   - owedToMe: caller là toUser (người khác nợ tôi)
4. Gom nhóm iOwe theo toUser:
   { toUser, totalAmount, debts: [{ id, amount, expenseId }] }
5. Gom nhóm owedToMe theo fromUser:
   { fromUser, totalAmount, debts: [{ id, amount, expenseId }] }
6. Trả về MyDebtsResponse { iOwe: [...], owedToMe: [...] }
```

### Cấu Trúc Response

```json
{
  "iOwe": [
    {
      "toUser": { "id": 2, "username": "bob" },
      "totalAmount": 150000,
      "debts": [
        { "id": 5, "amount": 100000, "expenseId": 3 },
        { "id": 6, "amount": 50000, "expenseId": 4 }
      ]
    }
  ],
  "owedToMe": [
    {
      "fromUser": { "id": 3, "username": "carol" },
      "totalAmount": 80000,
      "debts": [...]
    }
  ]
}
```

---

## API 4 — Xem Chi Tiết Một Debt

```
GET /api/groups/{groupId}/debts/{debtId}
Authorization: Bearer <token>
```

### Luồng Nghiệp Vụ

```
1. Xác thực caller là thành viên group
2. Tìm Debt theo debtId → 404 nếu không có
3. Kiểm tra debt thuộc về group này:
   debt.expense.group.id phải == groupId → 400 nếu không khớp
4. Lấy danh sách Settlement liên quan đến debt này
5. Trả về DebtDetailResponse {
     id, fromUser, toUser, amount, status,
     expense: { id, description },
     settlements: [...]
   }
```

### Điều Kiện Kiểm Tra

| Điều kiện | Kết quả |
|---|---|
| Caller không phải thành viên | `403 Forbidden` |
| Debt không tồn tại | `404 Not Found` |
| Debt không thuộc group | `400 Bad Request` |
