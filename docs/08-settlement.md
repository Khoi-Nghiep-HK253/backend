# ✅ Settlement API

[← Back to overview](./README.md)

---

> ## 📌 Note
> A Settlement is a transaction **confirming debt payment**. After a Settlement is successfully created,
> the corresponding Debt status will automatically update to `SETTLED`.

---

## POST `/groups/{groupId}/settlements` — Record debt payment

**Auth required**: ✅ Bearer Token | **Authorization**: Group member (debtor or creditor)

### Request Body
```json
{
  "debtId": 5,
  "amount": "250000.00",
  "method": "TRANSFER",
  "note": "Bank transfer via MB Bank at 10 AM",
  "paidAt": "2026-08-02T10:00:00"
}
```

| Field | Type | Required | Description |
|---|---|---|---|
| `debtId` | integer | ✅ | ID of the debt being settled |
| `amount` | decimal | ✅ | Payment amount |
| `method` | string | ❌ | Payment method: `CASH`, `TRANSFER` (default: `CASH`) |
| `note` | string | ❌ | Additional note |
| `paidAt` | datetime | ❌ | Payment timestamp (default: current time) |

> **Validation**: `amount` must be <= remaining debt amount in `debtId`

### Response `201 Created`
```json
{
  "status": 201,
  "message": "Settlement recorded successfully",
  "data": {
    "id": 8,
    "debt": {
      "id": 5,
      "newStatus": "SETTLED"
    },
    "fromUser": { "id": 3, "username": "anle" },
    "toUser":   { "id": 1, "username": "hungtri" },
    "amount": "250000.00",
    "method": "TRANSFER",
    "note": "Bank transfer via MB Bank at 10 AM",
    "paidAt": "2026-08-02T10:00:00",
    "createdAt": "2026-08-02T10:05:00"
  }
}
```

### Common Errors
| Status | Cause |
|---|---|
| `400` | Debt is already settled (`SETTLED`) |
| `400` | `amount` exceeds remaining debt amount |
| `403` | User is not involved in this debt |
| `404` | `debtId` does not exist |

---

## GET `/groups/{groupId}/settlements` — Group settlement history

**Auth required**: ✅ Bearer Token | **Authorization**: Group member

### Query Parameters
| Param | Type | Description |
|---|---|---|
| `fromUserId` | integer | Filter by payer user ID |
| `toUserId` | integer | Filter by payee user ID |
| `fromDate` | date | Filter from date (`YYYY-MM-DD`) |
| `toDate` | date | Filter to date (`YYYY-MM-DD`) |
| `page` | integer | Page number (default: 0) |
| `size` | integer | Items per page (default: 20) |

### Response `200 OK`
```json
{
  "status": 200,
  "message": "Settlements retrieved successfully",
  "data": {
    "content": [
      {
        "id": 8,
        "fromUser": { "id": 3, "username": "anle" },
        "toUser":   { "id": 1, "username": "hungtri" },
        "amount": "250000.00",
        "method": "TRANSFER",
        "paidAt": "2026-08-02T10:00:00"
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

## GET `/groups/{groupId}/settlements/{settlementId}` — Get settlement detail

**Auth required**: ✅ Bearer Token | **Authorization**: Group member

### Response `200 OK`
```json
{
  "status": 200,
  "message": "Settlement retrieved successfully",
  "data": {
    "id": 8,
    "debt": { "id": 5, "amount": "250000.00" },
    "group": { "id": 10, "name": "Summer Trip 2026" },
    "fromUser": { "id": 3, "username": "anle" },
    "toUser":   { "id": 1, "username": "hungtri" },
    "amount": "250000.00",
    "method": "TRANSFER",
    "note": "Bank transfer via MB Bank at 10 AM",
    "paidAt": "2026-08-02T10:00:00",
    "createdAt": "2026-08-02T10:05:00"
  }
}
```
