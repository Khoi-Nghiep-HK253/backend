# Tài Liệu Nghiệp Vụ API — Divvy

Thư mục này mô tả chi tiết logic nghiệp vụ của từng API trong hệ thống, tổ chức theo phân hệ.

## Danh Sách Phân Hệ

| File | Phân hệ | Mô tả |
|---|---|---|
| [01-auth.md](./01-auth.md) | **Auth** | Đăng ký, đăng nhập, quên/đặt lại mật khẩu |
| [02-user.md](./02-user.md) | **User** | Quản lý hồ sơ người dùng, đổi mật khẩu |
| [03-group.md](./03-group.md) | **Group** | Tạo/sửa/xóa nhóm chi tiêu |
| [04-group-member.md](./04-group-member.md) | **Group Member** | Thêm/xóa/đổi vai trò thành viên |
| [05-invitation.md](./05-invitation.md) | **Invitation** | Gửi/chấp nhận/từ chối lời mời tham gia nhóm |
| [06-expense.md](./06-expense.md) | **Expense** | Tạo/sửa/xóa chi tiêu, tính toán chia tiền |
| [07-debt.md](./07-debt.md) | **Debt** | Xem công nợ sinh ra từ chi tiêu |
| [08-settlement.md](./08-settlement.md) | **Settlement** | Ghi nhận thanh toán nợ |

---

## Quy Ước Chung

### Xác Thực

Tất cả API (trừ `/api/auth/register`, `/api/auth/login`, `/api/auth/forgot-password`, `/api/auth/verify-reset-token`, `/api/auth/reset-password`) đều yêu cầu:

```
Authorization: Bearer <JWT_TOKEN>
```

### Quy Tắc Phân Quyền

| Vai trò | Ký hiệu | Mô tả |
|---|---|---|
| Bất kỳ thành viên | `MEMBER+` | User đã join nhóm |
| Chủ nhóm | `OWNER` | Có toàn quyền trong nhóm |
| Chủ tài khoản | `SELF` | Chỉ được thao tác trên tài khoản của mình |

### Mã Lỗi HTTP Phổ Biến

| Code | Ý nghĩa |
|---|---|
| `200 OK` | Thành công, có body |
| `201 Created` | Tạo mới thành công |
| `204 No Content` | Thành công, không có body |
| `400 Bad Request` | Dữ liệu đầu vào không hợp lệ |
| `401 Unauthorized` | Chưa xác thực hoặc token hết hạn |
| `403 Forbidden` | Đã xác thực nhưng không đủ quyền |
| `404 Not Found` | Tài nguyên không tồn tại |
| `409 Conflict` | Dữ liệu đã tồn tại (username/email trùng) |

### Luồng Chung Của Mọi API (Có Bảo Vệ)

```
Request → JWT Filter → Controller → Service
                                      │
                                      ├─ 1. Xác thực người dùng (User exists?)
                                      ├─ 2. Xác thực tài nguyên (Group/Expense/... exists?)
                                      ├─ 3. Kiểm tra quyền (isMember? isOwner? isSelf?)
                                      ├─ 4. Kiểm tra ràng buộc nghiệp vụ
                                      ├─ 5. Thực thi nghiệp vụ
                                      └─ 6. Trả về Response
```
