# Phân Hệ Settlement — Nghiệp Vụ Chi Tiết

## Tổng Quan

Phân hệ **Settlement** quản lý các giao dịch thanh toán nợ. Khi một người ghi nhận đã thanh toán một khoản nợ (Debt), hệ thống tạo Settlement và tự động chuyển trạng thái Debt sang `SETTLED`.

---

## Quan Hệ Giữa Các Thực Thể

```
Expense (chi tiêu)
   └── Debt (công nợ giữa 2 người)
         └── Settlement (giao dịch thanh toán nợ đó)
```

---

## API 1 — Ghi Nhận Thanh Toán Nợ (Tạo Settlement)

```
POST /api/groups/{groupId}/settlements
Authorization: Bearer <token>
```

### Luồng Nghiệp Vụ

```
1. Lấy username từ JWT → tìm caller (User)
2. Tìm Group theo groupId → 404 nếu không có
3. Kiểm tra caller là thành viên group → 403 nếu không phải
4. Tìm Debt theo debtId trong request body → 404 nếu không có
5. Kiểm tra debt thuộc về group:
   debt.expense.group.id == groupId → 400 nếu không khớp
6. Validate settlement:
   a. debt.status == SETTLED → 400 "Debt has already been fully settled."
   b. caller phải là fromUser HOẶC toUser của debt → 403 FORBIDDEN
   c. amount phải > 0 → 400 "Settlement amount must be greater than zero."
   d. amount > debt.amount → 400 "Settlement amount exceeds remaining debt amount."
7. Xác định method (mặc định "CASH" nếu null/blank)
8. Xác định paidAt (mặc định LocalDateTime.now() nếu null)
9. Tạo Settlement {
     debt, group,
     fromUser = debt.fromUser,
     toUser = debt.toUser,
     amount, method, note, paidAt
   }
10. Lưu Settlement vào DB
11. Cập nhật debt.status = SETTLED → lưu DB
12. Trả về SettlementResponse
```

### Các Điều Kiện Kiểm Tra

| Điều kiện | Kết quả |
|---|---|
| Caller không phải thành viên group | `403 Forbidden` |
| Debt không tồn tại | `404 Not Found` |
| Debt không thuộc group này | `400 Bad Request` |
| Debt đã SETTLED | `400 Bad Request` |
| Caller không liên quan đến debt | `403 Forbidden` |
| amount <= 0 | `400 Bad Request` |
| amount > debt.amount | `400 Bad Request` |

### Trường Request Body

| Trường | Bắt buộc | Mặc định | Mô tả |
|---|---|---|---|
| `debtId` | ✅ | — | ID của Debt cần thanh toán |
| `amount` | ✅ | — | Số tiền thanh toán |
| `method` | ❌ | `"CASH"` | Phương thức thanh toán |
| `note` | ❌ | null | Ghi chú |
| `paidAt` | ❌ | `now()` | Thời điểm thanh toán |

### Quy Tắc Về `amount`

Hiện tại hệ thống áp dụng **full settlement only**:
- `amount` phải bằng đúng `debt.amount` hoặc nhỏ hơn
- Khi settlement được tạo → Debt ngay lập tức chuyển sang `SETTLED` dù amount < debt.amount
- *(Trong tương lai có thể mở rộng thành partial settlement)*

---

## API 2 — Lấy Danh Sách Settlement Của Nhóm

```
GET /api/groups/{groupId}/settlements?fromUserId=&toUserId=&fromDate=&toDate=&page=&size=
Authorization: Bearer <token>
```

### Luồng Nghiệp Vụ

```
1. Xác thực caller là thành viên group
2. Xây dựng JPA Specification với filter:
   - group.id = groupId (bắt buộc)
   - fromUser.id = fromUserId nếu có
   - toUser.id = toUserId nếu có
   - paidAt >= fromDate.atStartOfDay() nếu có
   - paidAt <= toDate.atTime(LocalTime.MAX) nếu có
3. Phân trang theo Pageable
4. Map từng Settlement → SettlementSummaryResponse
5. Trả về Page<SettlementSummaryResponse>
```

### Query Params

| Param | Kiểu | Mô tả |
|---|---|---|
| `fromUserId` | Integer | Lọc theo người trả |
| `toUserId` | Integer | Lọc theo người nhận |
| `fromDate` | LocalDate | Từ ngày (theo paidAt) |
| `toDate` | LocalDate | Đến ngày (theo paidAt) |
| `page` | Integer | Số trang (bắt đầu từ 0) |
| `size` | Integer | Số phần tử mỗi trang |

---

## API 3 — Xem Chi Tiết Settlement

```
GET /api/groups/{groupId}/settlements/{settlementId}
Authorization: Bearer <token>
```

### Luồng Nghiệp Vụ

```
1. Xác thực caller là thành viên group
2. Tìm Settlement theo settlementId → 404 nếu không có
3. Kiểm tra settlement.group.id == groupId → 400 nếu không khớp
4. Trả về SettlementDetailResponse {
     id, fromUser, toUser, amount, method, note, paidAt,
     debt: { id, amount, status },
     group: { id, name }
   }
```

### Điều Kiện Kiểm Tra

| Điều kiện | Kết quả |
|---|---|
| Caller không phải thành viên | `403 Forbidden` |
| Settlement không tồn tại | `404 Not Found` |
| Settlement không thuộc group | `400 Bad Request` |

---

## Ví Dụ Luồng Hoàn Chỉnh

```
1. Expense "Ăn tối" tổng 300k
   → A trả 300k
   → B, C mỗi người chia 100k
   → Hệ thống tạo:
      - Debt: B → A: 100k (PENDING)
      - Debt: C → A: 200k (PENDING)

2. B muốn thanh toán nợ với A:
   POST /api/groups/{groupId}/settlements
   { debtId: <B→A debt id>, amount: 100000, method: "MOMO" }
   → Settlement được tạo
   → Debt B→A chuyển sang SETTLED

3. C thanh toán nợ với A:
   POST /api/groups/{groupId}/settlements
   { debtId: <C→A debt id>, amount: 200000, method: "BANKING" }
   → Settlement được tạo
   → Debt C→A chuyển sang SETTLED
```
