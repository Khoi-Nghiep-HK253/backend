# 👥 Group API

[← Back to overview](./README.md)

---

## POST `/groups` — Create a new group

**Auth required**: ✅ Bearer Token

> The creator is automatically assigned the **OWNER** role in the new group.

### Request Body
```json
{
  "name": "Summer Trip 2026",
  "categoryId": 1,
  "defaultCurrencyId": 1,
  "note": "Team summer outing",
  "startDate": "2026-08-01",
  "endDate": "2026-08-05"
}
```

| Field | Type | Required | Description |
|---|---|---|---|
| `name` | string | ✅ | Group name, max 150 characters |
| `categoryId` | integer | ❌ | Group category ID |
| `defaultCurrencyId` | integer | ❌ | Default currency ID |
| `note` | string | ❌ | Group note |
| `startDate` | date | ❌ | Start date (`YYYY-MM-DD`) |
| `endDate` | date | ❌ | End date (`YYYY-MM-DD`) |

### Response `201 Created`
```json
{
  "status": 201,
  "message": "Group created successfully",
  "data": {
    "id": 10,
    "name": "Summer Trip 2026",
    "category": { "id": 1, "name": "Travel" },
    "defaultCurrency": { "id": 1, "code": "VND", "symbol": "₫" },
    "note": "Team summer outing",
    "startDate": "2026-08-01",
    "endDate": "2026-08-05",
    "createdAt": "2026-07-31T15:00:00"
  }
}
```

---

## GET `/groups` — List my groups

**Auth required**: ✅ Bearer Token

> Returns only groups where the current user is a member.

### Query Parameters
| Param | Type | Description |
|---|---|---|
| `page` | integer | Page number (default: 0) |
| `size` | integer | Items per page (default: 20) |

### Response `200 OK`
```json
{
  "status": 200,
  "message": "Groups retrieved successfully",
  "data": {
    "content": [
      {
        "id": 10,
        "name": "Summer Trip 2026",
        "category": { "id": 1, "name": "Travel" },
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

## GET `/groups/{groupId}` — Get group details

**Auth required**: ✅ Bearer Token | **Authorization**: Group member

### Response `200 OK`
```json
{
  "status": 200,
  "message": "Group retrieved successfully",
  "data": {
    "id": 10,
    "name": "Summer Trip 2026",
    "category": { "id": 1, "name": "Travel" },
    "defaultCurrency": { "id": 1, "code": "VND", "symbol": "₫" },
    "note": "Team summer outing",
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

## PUT `/groups/{groupId}` — Update group

**Auth required**: ✅ Bearer Token | **Authorization**: Group OWNER

### Request Body
```json
{
  "name": "Summer Trip 2026 (Updated)",
  "note": "Added extra notes",
  "endDate": "2026-08-07"
}
```

> Only include the fields you want to change (partial update).

### Response `200 OK`
```json
{
  "status": 200,
  "message": "Group updated successfully",
  "data": { ...group object... }
}
```

---

## DELETE `/groups/{groupId}` — Delete group

**Auth required**: ✅ Bearer Token | **Authorization**: Group OWNER

> ⚠️ Deleting a group permanently removes all associated data (expenses, debts, members, invitations). This action is irreversible.

### Response `200 OK`
```json
{
  "status": 200,
  "message": "Group deleted successfully",
  "data": null
}
```

### Common Errors
| Status | Cause |
|---|---|
| `403` | Caller is not the group OWNER |
| `404` | Group does not exist |
