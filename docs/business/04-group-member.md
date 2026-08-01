# Phân Hệ Group Member — Nghiệp Vụ Chi Tiết

## Tổng Quan

Phân hệ **Group Member** quản lý thành viên trong nhóm: xem danh sách, thêm thủ công, đổi vai trò, và rời/xóa thành viên.

> **Lưu ý phân biệt**:
> - Thêm thành viên **thủ công** (API này) — OWNER thêm trực tiếp bằng userId.
> - Thêm thành viên qua **lời mời** (Invitation) — xem phân hệ 05-invitation.

---

## API 1 — Lấy Danh Sách Thành Viên

```
GET /api/groups/{groupId}/members
Authorization: Bearer <token>
```

### Luồng Nghiệp Vụ

```
1. Lấy username từ JWT → tìm User (caller)
2. Tìm Group theo groupId → 404 nếu không có
3. Tìm GroupMember của caller trong Group
4. Kiểm tra caller có phải thành viên không
   → Không phải → 403 FORBIDDEN "You are not a member of this group."
5. Lấy toàn bộ GroupMember của Group
6. Map từng GroupMember → GroupMemberResponse
7. Trả về List<GroupMemberResponse>
```

---

## API 2 — Thêm Thành Viên Thủ Công

```
POST /api/groups/{groupId}/members
Authorization: Bearer <token>
```

### Luồng Nghiệp Vụ

```
1. Lấy username từ JWT → tìm User (caller)
2. Tìm Group theo groupId → 404 nếu không có
3. Tìm GroupMember của caller trong Group
4. Kiểm tra caller có phải OWNER không
   → Không phải → 403 FORBIDDEN "Only group owners can perform this action."
5. Tìm target User theo userId trong request body
   → Không tìm thấy → 404 NOT_FOUND
6. Kiểm tra target User đã là thành viên chưa
   → Đã là thành viên → 400 BAD_REQUEST "User is already a member of this group."
7. Tạo GroupMember { group, user=targetUser, role=MEMBER } (mặc định)
8. Lưu vào DB
9. Trả về GroupMemberResponse
```

### Các Điều Kiện Kiểm Tra

| Điều kiện | Kết quả |
|---|---|
| Caller không phải thành viên | `403 Forbidden` |
| Caller không phải OWNER | `403 Forbidden` |
| Target user không tồn tại | `404 Not Found` |
| Target user đã là thành viên | `400 Bad Request` |

---

## API 3 — Cập Nhật Vai Trò Thành Viên

```
PUT /api/groups/{groupId}/members/{memberId}/role
Authorization: Bearer <token>
```

### Luồng Nghiệp Vụ

```
1. Lấy username từ JWT → tìm User (caller)
2. Tìm Group theo groupId → 404 nếu không có
3. Tìm GroupMember của caller → kiểm tra OWNER
   → Không phải OWNER → 403 FORBIDDEN
4. Tìm GroupMember theo memberId (target member)
   → Không tìm thấy → 404 NOT_FOUND
5. Đếm số OWNER hiện tại trong group
6. Validate downgrade:
   → Nếu target đang là OWNER và newRole là MEMBER:
      → Số OWNER <= 1 → 400 BAD_REQUEST "Cannot downgrade the last owner of the group."
7. Cập nhật role của GroupMember
8. Lưu vào DB
9. Trả về GroupMemberResponse
```

### Quy Tắc Đặc Biệt

- **Bảo vệ OWNER cuối cùng**: Không thể downgrade OWNER duy nhất xuống MEMBER. Nhóm phải luôn có ít nhất 1 OWNER.
- Caller (OWNER) có thể tự downgrade chính mình nếu còn ít nhất 1 OWNER khác.

| Điều kiện | Kết quả |
|---|---|
| Caller không phải OWNER | `403 Forbidden` |
| target member không tồn tại | `404 Not Found` |
| Downgrade OWNER duy nhất | `400 Bad Request` |

---

## API 4 — Xóa / Rời Nhóm

```
DELETE /api/groups/{groupId}/members/{memberId}
Authorization: Bearer <token>
```

### Luồng Nghiệp Vụ

```
1. Lấy username từ JWT → tìm User (caller)
2. Tìm Group theo groupId → 404 nếu không có
3. Tìm GroupMember target theo memberId
   → Không tìm thấy → 404 NOT_FOUND
4. Tìm GroupMember của caller
5. Xác định quyền:
   - isSelf = (target.user.id == caller.id)
   - isCallerAdmin = (callerMember != null && callerMember.role == OWNER)
6. Kiểm tra ủy quyền:
   → Nếu KHÔNG phải self VÀ KHÔNG phải admin → 403 FORBIDDEN "You are not authorized to remove this member."
7. Kiểm tra bảo vệ OWNER cuối:
   → Nếu target là OWNER và đây là OWNER duy nhất → 400 BAD_REQUEST "Cannot remove the last owner of the group."
8. Xóa GroupMember khỏi DB
9. Trả về 204 No Content
```

### Quy Tắc Ủy Quyền

| Hành động | Điều kiện cho phép |
|---|---|
| Rời nhóm (tự xóa mình) | Bất kỳ thành viên nào |
| Xóa thành viên khác | Chỉ OWNER |
| Xóa OWNER duy nhất | ❌ Không được phép |
