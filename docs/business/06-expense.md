# Phân Hệ Expense — Nghiệp Vụ Chi Tiết

## Tổng Quan

Phân hệ **Expense** là nghiệp vụ cốt lõi của hệ thống. Khi tạo chi tiêu, hệ thống tự động:
1. Lưu danh sách người chi trả (ExpensePayer)
2. Tính toán và lưu phần chia (ExpenseShare) theo loại chia tiền
3. Tính toán và lưu công nợ (Debt) thực tế giữa các thành viên

---

## Các Loại Chia Tiền (SplitType)

| SplitType | Mô tả | Input cần thiết |
|---|---|---|
| `EQUAL` | Chia đều cho tất cả | Danh sách userId |
| `EXACT` | Mỗi người trả đúng số tiền xác định | userId + amount |
| `PERCENTAGE` | Chia theo phần trăm | userId + percentage |
| `SHARES` | Chia theo tỷ lệ (phần) | userId + ratio |
| `ADJUSTMENT` | Chia đều, sau đó điều chỉnh ± | userId + adjustment |

---

## API 1 — Tạo Chi Tiêu

```
POST /api/groups/{groupId}/expenses
Authorization: Bearer <token>
```

### Luồng Nghiệp Vụ

```
1. Lấy username từ JWT → tìm caller
2. Tìm Group theo groupId → 404 nếu không có
3. Kiểm tra caller là thành viên group → 403 nếu không phải
4. Tìm Currency theo currencyId → 404 nếu không có
5. Load tất cả GroupMember → tạo userMap (userId → User)

── Validate dữ liệu đầu vào:
6. validatePayers: tổng tiền payers phải == totalAmount, userId phải là thành viên group
7. validateShares: userId phải là thành viên group, ràng buộc riêng theo SplitType

── Tạo & lưu Expense:
8. Tạo Expense entity (MapStruct: group, currency, splitType, ...)
9. Lưu Expense vào DB

── Tạo & lưu ExpensePayer:
10. Với mỗi payer trong request → tạo ExpensePayer { expense, user, amount }
11. Lưu tất cả vào DB

── Tính toán phần chia (normalizedSharesMap):
12. Theo SplitType → calculateNormalizedShares() → Map<userId, amount>
13. Lưu ExpenseShare cho từng user

── Tính toán & lưu Debt:
14. calculateAndSaveDebts():
    - Tính net = paid - share cho từng participant
    - net < 0 → debtor (người nợ)
    - net > 0 → creditor (người được nợ)
    - Dùng thuật toán Two-Pointer để tối thiểu hóa số lượng debt
    - Tạo Debt { expense, fromUser, toUser, amount, status=PENDING }
15. Lưu tất cả Debt vào DB

16. Trả về ExpenseResponse (expense + payers + shares + debts)
```

### Thuật Toán Tính Debt (Two-Pointer Greedy)

```
Ví dụ: A trả 300k, B chi tiêu 100k, C chi tiêu 200k

paidMap: { A: 300k }
shareMap: { B: 100k, C: 200k }
net:
  A: +300k (creditor)
  B: -100k (debtor)
  C: -200k (debtor)

Giải:
  B → A: 100k   (Debt 1)
  C → A: 200k   (Debt 2)
```

### Cách Tính Theo Từng SplitType

**EQUAL** — Chia đều:
```
totalAmount = 300k, 3 người
equalShare = 300/3 = 100k
Người đầu nhận phần dư làm tròn (nếu có)
```

**PERCENTAGE** — Theo phần trăm:
```
totalAmount = 100k
A: 50% → 50k
B: 30% → 30k
C: 20% → 20k
Phần làm tròn dư ra → cộng vào người đầu tiên
```

**SHARES** — Theo tỷ lệ:
```
totalAmount = 120k
A: ratio=3, B: ratio=1, C: ratio=2 → total ratio=6
A: 3/6 * 120 = 60k, B: 1/6 * 20k, C: 2/6 * 40k
```

**ADJUSTMENT** — Điều chỉnh:
```
totalAmount = 120k, 3 người
equalBase = 40k
A: +10k → 50k, B: -5k → 35k, C: 0 → 40k
```

---

## API 2 — Lấy Danh Sách Chi Tiêu Của Nhóm

```
GET /api/groups/{groupId}/expenses?fromDate=&toDate=&page=&size=
Authorization: Bearer <token>
```

### Luồng Nghiệp Vụ

```
1. Lấy username từ JWT → tìm caller
2. Tìm Group → 404 nếu không có
3. Kiểm tra caller là thành viên → 403 nếu không phải
4. Xây dựng JPA Specification với các filter:
   - group.id = groupId (bắt buộc)
   - fromDate (expenseDate >= fromDate) nếu có
   - toDate (expenseDate <= toDate) nếu có
5. Phân trang theo Pageable
6. Map từng Expense → ExpenseSummaryResponse (kèm payerCount, shareCount)
7. Trả về Page<ExpenseSummaryResponse>
```

---

## API 3 — Xem Chi Tiết Chi Tiêu

```
GET /api/groups/{groupId}/expenses/{expenseId}
Authorization: Bearer <token>
```

### Luồng Nghiệp Vụ

```
1. Lấy username từ JWT → tìm caller
2. Tìm Group → 404 nếu không có
3. Kiểm tra caller là thành viên → 403 nếu không phải
4. Tìm Expense theo expenseId → 404 nếu không có
5. Load ExpensePayer, ExpenseShare, Debt theo expenseId
6. Trả về ExpenseResponse đầy đủ
```

---

## API 4 — Cập Nhật Chi Tiêu

```
PUT /api/groups/{groupId}/expenses/{expenseId}
Authorization: Bearer <token>
```

### Luồng Nghiệp Vụ

```
1-3. Giống API tạo (xác thực caller, group, membership)
4. Tìm Expense → 404 nếu không có
5. Validate quyền chỉnh sửa:
   - caller phải là người tạo expense HOẶC là OWNER của group
   → Không hợp lệ → 403 FORBIDDEN
6. Kiểm tra trạng thái:
   - Nếu bất kỳ Debt nào của expense đã SETTLED → 400 BAD_REQUEST
     "Cannot edit expense with settled debts."
7. Validate currency, payers, shares (giống khi tạo)
8. Cập nhật Expense entity (MapStruct)
9. Xóa toàn bộ dữ liệu cũ:
   - Xóa tất cả Debt cũ
   - Xóa tất cả ExpensePayer cũ
   - Xóa tất cả ExpenseShare cũ
10. Tạo lại Payers, Shares, Debts mới từ đầu
11. Trả về ExpenseResponse
```

### Điều Kiện Chặn Chỉnh Sửa

| Điều kiện | Kết quả |
|---|---|
| Caller không phải creator và không phải OWNER | `403 Forbidden` |
| Có Debt đã SETTLED | `400 Bad Request` |

---

## API 5 — Xóa Chi Tiêu

```
DELETE /api/groups/{groupId}/expenses/{expenseId}
Authorization: Bearer <token>
```

### Luồng Nghiệp Vụ

```
1-3. Xác thực caller, group, membership
4. Tìm Expense → 404 nếu không có
5. Validate quyền (giống update): creator hoặc OWNER
6. Kiểm tra: nếu có Debt đã SETTLED → 400 BAD_REQUEST
7. Xóa theo thứ tự (tránh constraint violation):
   a. Xóa tất cả Debt của expense
   b. Xóa tất cả ExpensePayer của expense
   c. Xóa tất cả ExpenseShare của expense
   d. Xóa Expense
8. Trả về 204 No Content
```

> **Nguyên tắc bảo toàn dữ liệu**: Expense đã có Debt được thanh toán (SETTLED) không thể xóa hoặc sửa. Đây là điều kiện bất biến quan trọng của hệ thống.
