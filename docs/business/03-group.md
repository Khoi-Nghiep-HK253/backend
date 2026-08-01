# Phân Hệ Group — Nghiệp Vụ Chi Tiết

## Tổng Quan

Phân hệ **Group** quản lý các nhóm chi tiêu: tạo, xem danh sách, xem chi tiết, cập nhật và xóa. Khi tạo nhóm, người tạo tự động được thêm vào nhóm với vai trò **OWNER**.

---

## Vai Trò Trong Nhóm (GroupRole)

| Vai trò | Quyền hạn |
|---|---|
| `OWNER` | Toàn quyền: sửa, xóa nhóm, quản lý thành viên, gửi lời mời |
| `MEMBER` | Chỉ xem, tạo chi tiêu |

---

## API 1 — Tạo Nhóm

```
POST /api/groups
Authorization: Bearer <token>
```

### Luồng Nghiệp Vụ

```
1. Lấy username từ JWT → tìm User (creator)
   → Không tìm thấy → 404 NOT_FOUND
2. Nếu categoryId != null → tìm Category
   → Không tìm thấy → 404 NOT_FOUND
3. Nếu defaultCurrencyId != null → tìm Currency
   → Không tìm thấy → 404 NOT_FOUND
4. Map CreateGroupRequest + creator + category + currency → Group entity (MapStruct)
5. Lưu Group vào DB
6. Tạo GroupMember { group, user=creator, role=OWNER } → lưu vào DB
7. Trả về GroupResponse
```

### Quy Tắc

- Người tạo nhóm tự động trở thành **OWNER** duy nhất ban đầu.
- `categoryId` và `defaultCurrencyId` là tùy chọn (có thể null).

---

## API 2 — Lấy Danh Sách Nhóm Của Tôi

```
GET /api/groups/me?page=0&size=10
Authorization: Bearer <token>
```

### Luồng Nghiệp Vụ

```
1. Lấy username từ JWT → tìm User
   → Không tìm thấy → 404
2. Truy vấn DB: tìm tất cả Group mà User là thành viên (qua bảng group_members)
   → Hỗ trợ phân trang (Pageable)
3. Map từng Group → GroupResponse
4. Trả về Page<GroupResponse>
```

---

## API 3 — Xem Chi Tiết Nhóm

```
GET /api/groups/{groupId}
Authorization: Bearer <token>
```

### Luồng Nghiệp Vụ

```
1. Lấy username từ JWT → tìm User
2. Tìm Group theo groupId
   → Không tìm thấy → 404 NOT_FOUND
3. Tìm GroupMember của User trong Group này
4. Kiểm tra User có phải thành viên không
   → Không phải → 403 FORBIDDEN "You are not a member of this group."
5. Trả về GroupResponse
```

---

## API 4 — Cập Nhật Thông Tin Nhóm

```
PUT /api/groups/{groupId}
Authorization: Bearer <token>
```

### Luồng Nghiệp Vụ

```
1. Lấy username từ JWT → tìm User
2. Tìm Group theo groupId
   → Không tìm thấy → 404
3. Tìm GroupMember của User trong Group
4. Kiểm tra quyền OWNER:
   → Không phải OWNER → 403 FORBIDDEN "Only group owners can perform this action."
5. Nếu categoryId mới != null → tìm Category mới
6. Nếu defaultCurrencyId mới != null → tìm Currency mới
7. Cập nhật Group bằng MapStruct (BeanMapping IGNORE_NULL):
   → Chỉ các trường được gửi mới được cập nhật
8. Lưu Group, trả về GroupResponse cập nhật
```

### Các Điều Kiện Kiểm Tra

| Điều kiện | Kết quả |
|---|---|
| Group không tồn tại | `404 Not Found` |
| Caller không phải thành viên | `403 Forbidden` |
| Caller không phải OWNER | `403 Forbidden` |

---

## API 5 — Xóa Nhóm

```
DELETE /api/groups/{groupId}
Authorization: Bearer <token>
```

### Luồng Nghiệp Vụ

```
1. Lấy username từ JWT → tìm User
2. Tìm Group theo groupId
   → Không tìm thấy → 404
3. Tìm GroupMember của User trong Group
4. Kiểm tra quyền OWNER
   → Không phải OWNER → 403 FORBIDDEN
5. Xóa Group (cascade xóa theo cấu hình JPA: GroupMember, Invitation, Expense, Debt...)
6. Trả về 204 No Content
```

> **Cảnh báo**: Xóa nhóm sẽ xóa toàn bộ dữ liệu liên quan (thành viên, chi tiêu, công nợ) không thể phục hồi.
