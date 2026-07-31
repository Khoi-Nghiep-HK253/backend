# 👥 Group API

[← Về tổng quan](./README.md)

---

## POST `/groups` — Tạo nhóm mới

**Auth required**: ✅ Bearer Token

> Người tạo nhóm tự động trở thành **ADMIN** của nhóm.

### Request Body
```json
{
  "name": "Du lịch Đà Lạt 2026",
  "categoryId": 1,
  "defaultCurrencyId": 1,
  "note": "Chuyến đi hè của team",
  "startDate": "2026-08-01",
  "endDate": "2026-08-05"
}
```

| Field | Type | Required | Mô tả |
|---|---|---|---|
| `name` | string | ✅ | Tên nhóm, tối đa 150 ký tự |
| `categoryId` | integer | ❌ | ID danh mục nhóm |
| `defaultCurrencyId` | integer | ❌ | ID đơn vị tiền tệ mặc định |
| `note` | string | ❌ | Ghi chú về nhóm |
| `startDate` | date | ❌ | Ngày bắt đầu (`YYYY-MM-DD`) |
| `endDate` | date | ❌ | Ngày kết thúc (`YYYY-MM-DD`) |

### Response `201 Created`
```json
{
  "status": 201,
  "message": "Group created successfully",
  "data": {
    "id": 10,
    "name": "Du lịch Đà Lạt 2026",
    "category": { "id": 1, "name": "Du lịch" },
    "defaultCurrency": { "id": 1, "code": "VND", "symbol": "₫" },
    "note": "Chuyến đi hè của team",
    "startDate": "2026-08-01",
    "endDate": "2026-08-05",
    "createdAt": "2026-07-31T15:00:00"
  }
}
```

---

## GET `/groups` — Lấy danh sách nhóm của tôi

**Auth required**: ✅ Bearer Token

> Chỉ trả về các nhóm mà user hiện tại là thành viên.

### Query Parameters
| Param | Type | Mô tả |
|---|---|---|
| `page` | integer | Trang hiện tại (mặc định: 0) |
| `size` | integer | Số item mỗi trang (mặc định: 20) |

### Response `200 OK`
```json
{
  "status": 200,
  "message": "Groups retrieved successfully",
  "data": {
    "content": [
      {
        "id": 10,
        "name": "Du lịch Đà Lạt 2026",
        "category": { "id": 1, "name": "Du lịch" },
        "defaultCurrency": { "id": 1, "code": "VND", "symbol": "₫" },
        "memberCount": 4,
        "startDate": "2026-08-01",
        "endDate": "2026-08-05"
      }
    ],
    "totalElements": 1,
    "totalPages": 1,
    "page": 0,
    "size": 20
  }
}
```

---

## GET `/groups/{groupId}` — Lấy chi tiết nhóm

**Auth required**: ✅ Bearer Token | **Phân quyền**: Thành viên của nhóm

### Response `200 OK`
```json
{
  "status": 200,
  "message": "Group retrieved successfully",
  "data": {
    "id": 10,
    "name": "Du lịch Đà Lạt 2026",
    "category": { "id": 1, "name": "Du lịch" },
    "defaultCurrency": { "id": 1, "code": "VND", "symbol": "₫" },
    "note": "Chuyến đi hè của team",
    "startDate": "2026-08-01",
    "endDate": "2026-08-05",
    "memberCount": 4,
    "totalExpense": "4500000.00",
    "createdAt": "2026-07-31T15:00:00",
    "updatedAt": "2026-07-31T15:00:00"
  }
}
```

---

## PUT `/groups/{groupId}` — Cập nhật nhóm

**Auth required**: ✅ Bearer Token | **Phân quyền**: ADMIN của nhóm

### Request Body
```json
{
  "name": "Du lịch Đà Lạt (Update)",
  "note": "Cập nhật thêm ghi chú",
  "endDate": "2026-08-07"
}
```

> Chỉ cần gửi các field muốn thay đổi.

### Response `200 OK`
```json
{
  "status": 200,
  "message": "Group updated successfully",
  "data": { ...group object... }
}
```

---

## DELETE `/groups/{groupId}` — Xoá nhóm

**Auth required**: ✅ Bearer Token | **Phân quyền**: ADMIN của nhóm

> ⚠️ Xoá nhóm sẽ xoá toàn bộ dữ liệu liên quan (chi tiêu, công nợ...). Cân nhắc dùng soft-delete.

### Response `200 OK`
```json
{
  "status": 200,
  "message": "Group deleted successfully",
  "data": null
}
```

### Lỗi thường gặp
| Status | Trường hợp |
|---|---|
| `403` | Không phải ADMIN của nhóm |
| `404` | Nhóm không tồn tại |
