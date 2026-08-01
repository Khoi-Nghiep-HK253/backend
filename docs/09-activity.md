# 📋 Activity API

[← Back to overview](./README.md)

---

> ## 📌 Note
> Activities are **automatically recorded** by the system after key actions.
> This API is strictly for **read** operations — there are no endpoints for creating, updating, or deleting activities.

---

## `entityType` and `topic` Reference

| entityType | topic | Trigger condition |
|---|---|---|
| `GROUP` | `GROUP_CREATED` | New group created |
| `GROUP` | `GROUP_UPDATED` | Group details updated |
| `GROUP_MEMBER` | `MEMBER_JOINED` | Member joined group |
| `GROUP_MEMBER` | `MEMBER_LEFT` | Member left group |
| `GROUP_MEMBER` | `MEMBER_ROLE_CHANGED` | Member role updated |
| `INVITATION` | `INVITATION_SENT` | Invitation sent |
| `INVITATION` | `INVITATION_ACCEPTED` | Invitation accepted |
| `INVITATION` | `INVITATION_DECLINED` | Invitation declined |
| `INVITATION` | `INVITATION_REVOKED` | Invitation revoked |
| `EXPENSE` | `EXPENSE_CREATED` | New expense created |
| `EXPENSE` | `EXPENSE_UPDATED` | Expense updated |
| `EXPENSE` | `EXPENSE_DELETED` | Expense deleted |
| `SETTLEMENT` | `SETTLEMENT_CREATED` | Debt payment recorded |

---

## GET `/groups/{groupId}/activities` — Group activity history

**Auth required**: ✅ Bearer Token | **Authorization**: Group member

### Query Parameters
| Param | Type | Description |
|---|---|---|
| `page` | integer | Page number (default: 0) |
| `size` | integer | Items per page (default: 30) |
| `entityType` | string | Filter by entity type (`EXPENSE`, `SETTLEMENT`, etc.) |
| `userId` | integer | Filter by performing user ID |
| `fromDate` | date | Filter from date (`YYYY-MM-DD`) |
| `toDate` | date | Filter to date (`YYYY-MM-DD`) |

### Response `200 OK`
```json
{
  "status": 200,
  "message": "Activities retrieved successfully",
  "data": {
    "content": [
      {
        "id": 101,
        "user": { "id": 1, "username": "hungtri", "fullname": "Tri Hung" },
        "entityType": "EXPENSE",
        "entityId": 20,
        "topic": "EXPENSE_CREATED",
        "description": "hungtri created expense \"Thai hotpot dinner\" — 1,000,000 ₫",
        "createdAt": "2026-08-01T20:30:00"
      },
      {
        "id": 100,
        "user": { "id": 3, "username": "anle", "fullname": "An Le" },
        "entityType": "SETTLEMENT",
        "entityId": 8,
        "topic": "SETTLEMENT_CREATED",
        "description": "anle paid 250,000 ₫ to hungtri",
        "createdAt": "2026-08-02T10:05:00"
      }
    ],
    "totalElements": 20,
    "totalPages": 1,
    "page": 0,
    "size": 30
  }
}
```

---

## GET `/users/{userId}/activities` — User activity history

**Auth required**: ✅ Bearer Token

> Activity history for a specific user.

### Query Parameters
| Param | Type | Description |
|---|---|---|
| `page` | integer | Page number (default: 0) |
| `size` | integer | Items per page (default: 20) |

### Response `200 OK`
```json
{
  "status": 200,
  "message": "User activities retrieved successfully",
  "data": {
    "content": [
      {
        "id": 101,
        "entityType": "EXPENSE",
        "entityId": 20,
        "topic": "EXPENSE_CREATED",
        "description": "hungtri created expense \"Thai hotpot dinner\" — 1,000,000 ₫",
        "createdAt": "2026-08-01T20:30:00"
      }
    ],
    "totalElements": 5,
    "totalPages": 1,
    "page": 0,
    "size": 20
  }
}
```
