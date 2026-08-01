# Phân Hệ Invitation — Nghiệp Vụ Chi Tiết

## Tổng Quan

Phân hệ **Invitation** quản lý lời mời tham gia nhóm. Đây là luồng chính để thêm thành viên một cách có kiểm soát: OWNER gửi lời mời → người được mời có thể chấp nhận hoặc từ chối.

---

## Vòng Đời Của Lời Mời (InvitationStatus)

```
                    ┌─────────────────────────────┐
                    │         PENDING              │
                    └──────────┬──────────────────┘
             ┌─────────────────┼───────────────────────────┐
             ▼                 ▼                           ▼
        ACCEPTED          DECLINED                     REVOKED
    (auto: thêm vào    (bởi invitee)              (bởi OWNER)
      group MEMBER)
             
     Nếu quá hạn (expiresAt < now) → hệ thống tự đổi → EXPIRED
```

---

## API 1 — Gửi Lời Mời

```
POST /api/groups/{groupId}/invitations
Authorization: Bearer <token>
```

### Luồng Nghiệp Vụ

```
1. Lấy username từ JWT → tìm inviter (User)
2. Tìm invitee theo inviteeId trong request body
   → Không tìm thấy → 404 NOT_FOUND
3. Tìm Group theo groupId → 404 nếu không có
4. Tìm GroupMember của inviter trong Group
5. Validate lời mời:
   a. inviter phải là OWNER → 403 FORBIDDEN nếu không phải
   b. invitee chưa là thành viên → 400 BAD_REQUEST nếu đã là thành viên
   c. invitee chưa có lời mời PENDING → 400 BAD_REQUEST nếu đã có
6. Tạo token duy nhất: "tok_" + 16 ký tự alphanumeric ngẫu nhiên
7. Tạo GroupInvitation { group, inviter, invitee, status=PENDING, token, message, expiresAt }
8. Lưu vào DB
9. Trả về InvitationResponse
```

### Các Điều Kiện Kiểm Tra

| Điều kiện | Kết quả |
|---|---|
| inviter không phải OWNER | `403 Forbidden` |
| invitee đã là thành viên | `400 Bad Request` |
| invitee đã có lời mời PENDING | `400 Bad Request` |
| invitee không tồn tại | `404 Not Found` |

---

## API 2 — Xem Danh Sách Lời Mời Của Nhóm

```
GET /api/groups/{groupId}/invitations?status=PENDING
Authorization: Bearer <token>
```

### Luồng Nghiệp Vụ

```
1. Lấy username từ JWT → tìm caller
2. Tìm GroupMember của caller trong Group
3. Kiểm tra caller phải là OWNER
   → Không phải OWNER → 403 FORBIDDEN
4. Nếu có query param `status`:
   → Lấy invitations theo groupId + status
   Nếu không có:
   → Lấy tất cả invitations theo groupId
5. Map → List<InvitationResponse>
6. Trả về List<InvitationResponse>
```

> Chỉ OWNER mới xem được danh sách lời mời của nhóm.

---

## API 3 — Xem Lời Mời Của Tôi (Invitee)

```
GET /api/invitations/me?status=PENDING
Authorization: Bearer <token>
```

### Luồng Nghiệp Vụ

```
1. Lấy username từ JWT → tìm caller (invitee)
2. Nếu có query param `status` → dùng status đó
   Nếu không → mặc định lấy PENDING
3. Truy vấn: tất cả GroupInvitation của invitee có status tương ứng
4. Map → List<InvitationResponse>
5. Trả về
```

> API này cho phép user xem các lời mời mình nhận được, mặc định chỉ lấy PENDING.

---

## API 4 — Chấp Nhận Lời Mời

```
POST /api/invitations/{invitationId}/accept
Authorization: Bearer <token>
```

### Luồng Nghiệp Vụ

```
1. Lấy username từ JWT → tìm caller
2. Tìm GroupInvitation theo invitationId → 404 nếu không có
3. Kiểm tra hết hạn TRƯỚC khi validate:
   → expiresAt != null && expiresAt < now:
      → Cập nhật status = EXPIRED, lưu DB
      → Throw 400 "Invitation has expired."
4. Validate:
   a. caller phải là invitee của lời mời → 403 nếu không phải
   b. status phải là PENDING → 400 "Invitation is no longer pending."
   c. Kiểm tra lại hạn một lần nữa → 400 "Invitation has expired."
5. Cập nhật invitation.status = ACCEPTED, lưu DB
6. Tạo GroupMember { group, user=caller, role=MEMBER } → lưu DB (auto join nhóm)
7. Trả về AcceptInvitationResponse { invitation, membership }
```

### Tác Dụng Phụ Quan Trọng

Khi chấp nhận, hệ thống **tự động thêm user vào nhóm** với vai trò **MEMBER** — không cần bước tay riêng.

---

## API 5 — Từ Chối Lời Mời

```
POST /api/invitations/{invitationId}/decline
Authorization: Bearer <token>
```

### Luồng Nghiệp Vụ

```
1. Lấy username từ JWT → tìm caller
2. Tìm GroupInvitation theo invitationId → 404 nếu không có
3. Validate:
   a. caller phải là invitee → 403 FORBIDDEN
   b. status phải là PENDING → 400 BAD_REQUEST
4. Cập nhật invitation.status = DECLINED, lưu DB
5. Trả về InvitationStatusResponse { status="DECLINED" }
```

---

## API 6 — Thu Hồi Lời Mời (OWNER)

```
DELETE /api/groups/{groupId}/invitations/{invitationId}
Authorization: Bearer <token>
```

### Luồng Nghiệp Vụ

```
1. Lấy username từ JWT → tìm caller
2. Tìm GroupInvitation theo invitationId → 404 nếu không có
3. Tìm GroupMember của caller trong group của invitation
4. Validate:
   a. caller phải là OWNER của group → 403 FORBIDDEN nếu không phải
   b. status của invitation phải là PENDING → 400 "Only pending invitations can be revoked."
5. Cập nhật invitation.status = REVOKED, lưu DB
6. Trả về InvitationStatusResponse { status="REVOKED" }
```

### Bảng Tổng Hợp Quyền Hạn

| Hành động | Ai được phép |
|---|---|
| Gửi lời mời | OWNER |
| Xem danh sách lời mời của nhóm | OWNER |
| Xem lời mời của mình | Bất kỳ user |
| Chấp nhận lời mời | Invitee |
| Từ chối lời mời | Invitee |
| Thu hồi lời mời | OWNER |
