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
2. Xác định category — có 2 chế độ, gửi cả hai cùng lúc → 400 BAD_REQUEST:
   a. categoryId != null → tìm Category theo id → Không tìm thấy → 404 NOT_FOUND
   b. categoryName != null/blank → tìm Category theo tên (không phân biệt hoa/thường)
      → Không tìm thấy → tự động tạo Category mới với tên đó (không báo lỗi)
   c. Cả hai đều null/blank → group không có category
3. Map CreateGroupRequest + creator + category → Group entity (MapStruct)
4. Lưu Group vào DB
5. Tạo GroupMember { group, user=creator, role=OWNER } → lưu vào DB
6. Trả về GroupResponse
```

### Quy Tắc

- Người tạo nhóm tự động trở thành **OWNER** duy nhất ban đầu.
- Category là tùy chọn (có thể bỏ trống). Chọn một trong hai chế độ:
  - `categoryId` — chọn category có sẵn, không tồn tại thì báo lỗi 404 (giống các API khác trong hệ thống).
  - `categoryName` — chế độ tự động: có tên rồi thì dùng, chưa có thì tự tạo mới, không bao giờ báo lỗi 404.
  - Gửi cả hai field cùng lúc → 400 BAD_REQUEST ("Provide either categoryId or categoryName, not both.").

---

## API 1.5 — Gợi Ý Category Bằng AI (Preview, Không Lưu)

```
POST /api/groups/suggest-category
Authorization: Bearer <token>
```

### Luồng Nghiệp Vụ

```
1. Lấy toàn bộ Category hiện có trong DB
2. Gửi tên các category đó + name/note của group cho Gemini (ChatClient, structured output)
   → AI chọn 1 tên category có sẵn phù hợp nhất, hoặc đề xuất tên mới nếu không cái nào hợp
3. Đối chiếu tên AI trả về với danh sách Category (không phân biệt hoa/thường):
   a. Khớp → trả về { categoryId, categoryName: null }
   b. Không khớp → trả về { categoryId: null, categoryName: "<tên đề xuất>" }
4. Không lưu gì vào DB ở bước này
```

### Quy Tắc

- Đây chỉ là gợi ý — client tự quyết định dùng hay sửa, rồi gửi `categoryId`/`categoryName` (tương ứng) sang `POST /api/groups` như bình thường.
- Response được thiết kế đúng hình dạng field của `CreateGroupRequest` để client gắn thẳng vào form, không cần map lại.
- Gọi AI thất bại (lỗi mạng, quota, timeout...) → 503 SERVICE_UNAVAILABLE.

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
5. Xác định category mới (cùng quy tắc 2 chế độ như lúc tạo — xem API 1)
6. Cập nhật Group bằng MapStruct (BeanMapping IGNORE_NULL):
   → Chỉ các trường được gửi mới được cập nhật
7. Lưu Group, trả về GroupResponse cập nhật
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
