# 🗂️ Reference — Enum Values & Error Codes

[← Về tổng quan](./README.md)

---

## Enum Values

### `InvitationStatus`
| Value | Mô tả |
|---|---|
| `PENDING` | Lời mời đang chờ phản hồi |
| `ACCEPTED` | Đã chấp nhận |
| `DECLINED` | Đã từ chối |
| `EXPIRED` | Hết hạn trước khi phản hồi |
| `REVOKED` | Bị thu hồi bởi người mời |

### `DebtStatus`
| Value | Mô tả |
|---|---|
| `PENDING` | Chưa được thanh toán |
| `SETTLED` | Đã thanh toán xong |
| `CANCELLED` | Bị huỷ (do expense bị xoá/chỉnh sửa) |

### `GroupMember.role`
| Value | Mô tả |
|---|---|
| `ADMIN` | Quản trị viên — toàn quyền quản lý nhóm |
| `MEMBER` | Thành viên thông thường |

### `Settlement.method`
| Value | Mô tả |
|---|---|
| `CASH` | Thanh toán tiền mặt (mặc định) |
| `TRANSFER` | Chuyển khoản ngân hàng |

### `User.role` (hệ thống)
| Value | Mô tả |
|---|---|
| `USER` | Người dùng thông thường |
| `ADMIN` | Quản trị viên hệ thống |

---

## Tổng hợp tất cả Endpoints

### 🔐 Auth
| Method | Endpoint | Mô tả |
|---|---|---|
| POST | `/api/auth/register` | Đăng ký |
| POST | `/api/auth/login` | Đăng nhập |
| GET | `/api/auth/me` | Thông tin user hiện tại |
| POST | `/api/auth/forgot-password` | Yêu cầu reset mật khẩu qua email |
| GET | `/api/auth/reset-password/verify` | Kiểm tra token reset còn hiệu lực |
| POST | `/api/auth/reset-password` | Đặt lại mật khẩu mới |

### 👤 User
| Method | Endpoint | Mô tả |
|---|---|---|
| GET | `/api/users/{id}` | Xem hồ sơ user |
| PUT | `/api/users/{id}` | Cập nhật hồ sơ |
| PATCH | `/api/users/{id}/password` | Đổi mật khẩu |

### 👥 Group
| Method | Endpoint | Mô tả |
|---|---|---|
| POST | `/api/groups` | Tạo nhóm |
| GET | `/api/groups` | Danh sách nhóm của tôi |
| GET | `/api/groups/{groupId}` | Chi tiết nhóm |
| PUT | `/api/groups/{groupId}` | Cập nhật nhóm |
| DELETE | `/api/groups/{groupId}` | Xoá nhóm |

### 🙋 Group Member
| Method | Endpoint | Mô tả |
|---|---|---|
| GET | `/api/groups/{groupId}/members` | Danh sách thành viên |
| PATCH | `/api/groups/{groupId}/members/{memberId}/role` | Đổi vai trò |
| DELETE | `/api/groups/{groupId}/members/{memberId}` | Xoá / rời nhóm |

### 📨 Invitation
| Method | Endpoint | Mô tả |
|---|---|---|
| POST | `/api/groups/{groupId}/invitations` | Gửi lời mời |
| GET | `/api/groups/{groupId}/invitations` | Danh sách lời mời nhóm |
| GET | `/api/invitations/me` | Lời mời của tôi (inbox) |
| PATCH | `/api/invitations/{id}/accept` | Chấp nhận |
| PATCH | `/api/invitations/{id}/decline` | Từ chối |
| PATCH | `/api/invitations/{id}/revoke` | Thu hồi |

### 💸 Expense
| Method | Endpoint | Mô tả |
|---|---|---|
| POST | `/api/groups/{groupId}/expenses` | Tạo khoản chi |
| GET | `/api/groups/{groupId}/expenses` | Danh sách khoản chi |
| GET | `/api/groups/{groupId}/expenses/{expenseId}` | Chi tiết khoản chi |
| PUT | `/api/groups/{groupId}/expenses/{expenseId}` | Cập nhật khoản chi |
| DELETE | `/api/groups/{groupId}/expenses/{expenseId}` | Xoá khoản chi |

### 🔴 Debt
| Method | Endpoint | Mô tả |
|---|---|---|
| GET | `/api/groups/{groupId}/debts` | Danh sách công nợ |
| GET | `/api/groups/{groupId}/debts/summary` | Tổng hợp công nợ |
| GET | `/api/groups/{groupId}/debts/me` | Công nợ của tôi |
| GET | `/api/groups/{groupId}/debts/{debtId}` | Chi tiết khoản nợ |

### ✅ Settlement
| Method | Endpoint | Mô tả |
|---|---|---|
| POST | `/api/groups/{groupId}/settlements` | Ghi nhận thanh toán |
| GET | `/api/groups/{groupId}/settlements` | Lịch sử thanh toán |
| GET | `/api/groups/{groupId}/settlements/{settlementId}` | Chi tiết giao dịch |

### 📋 Activity
| Method | Endpoint | Mô tả |
|---|---|---|
| GET | `/api/groups/{groupId}/activities` | Lịch sử hoạt động nhóm |
| GET | `/api/users/{userId}/activities` | Lịch sử cá nhân |

---

## Phân quyền tổng hợp

| Role | Quyền hạn |
|---|---|
| **SYSTEM ADMIN** | Toàn quyền hệ thống |
| **Group ADMIN** | Quản lý nhóm: mời, xoá member, chỉnh sửa nhóm, thu hồi lời mời |
| **Group MEMBER** | Xem nhóm, tạo expense, ghi nhận thanh toán, tự rời nhóm |
| **Chủ tài khoản** | Sửa hồ sơ cá nhân, đổi mật khẩu của chính mình |
