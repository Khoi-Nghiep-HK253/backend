# 💸 Expense API

[← Back to overview](./README.md)

---

> ## ⚙️ Expense Creation Flow
> 1. **Create expense** (description, total amount, currency, date)
> 2. **Declare payer(s)** — who paid out of pocket
> 3. **Declare share(s)** — who splits the cost and how much
> 4. **System auto-calculates Debts** based on (payer − share)

---

## POST `/groups/{groupId}/expenses` — Create a new expense

**Auth required**: ✅ Bearer Token | **Authorization**: Group member

### Request Body
```json
{
  "description": "Thai hotpot dinner day 1",
  "totalAmount": "1000000.00",
  "currencyId": 1,
  "expenseDate": "2026-08-01",
  "payers": [
    { "userId": 1, "amount": "800000.00" },
    { "userId": 2, "amount": "200000.00" }
  ],
  "shares": [
    { "userId": 1, "amount": "250000.00" },
    { "userId": 2, "amount": "250000.00" },
    { "userId": 3, "amount": "250000.00" },
    { "userId": 4, "amount": "250000.00" }
  ]
}
```

| Field | Type | Required | Description |
|---|---|---|---|
| `description` | string | ✅ | Expense description |
| `totalAmount` | decimal | ✅ | Total expense amount |
| `currencyId` | integer | ✅ | Currency ID |
| `expenseDate` | date | ✅ | Date the expense occurred |
| `splitType` | string | ❌ | Split mode: `EQUAL` (default), `EXACT`, `PERCENTAGE`, `SHARES`, `ADJUSTMENT` |
| `payers` | array | ✅ | List of people who paid |
| `payers[].userId` | integer | ✅ | Payer's user ID |
| `payers[].amount` | decimal | ✅ | Amount paid |
| `shares` | array | ✅ | List of people sharing the cost |
| `shares[].userId` | integer | ✅ | Participant's user ID |
| `shares[].amount` | decimal | ❌ | Exact amount (Required for `EXACT`) |
| `shares[].percentage` | decimal | ❌ | Percentage (Required for `PERCENTAGE`, sum must = 100) |
| `shares[].ratio` | decimal | ❌ | Ratio/shares count (Required for `SHARES`) |
| `shares[].adjustment` | decimal | ❌ | Plus/minus adjustment amount (Used with `ADJUSTMENT`, sum must = 0) |

> **Validation**: `sum(payers.amount)` must equal `totalAmount`  
> **Validation**: `sum(shares.amount)` must equal `totalAmount`

### Response `201 Created`
```json
{
  "status": 201,
  "message": "Expense created successfully",
  "data": {
    "id": 20,
    "group": { "id": 10, "name": "Summer Trip 2026" },
    "description": "Thai hotpot dinner day 1",
    "totalAmount": "1000000.00",
    "currency": { "id": 1, "code": "VND", "symbol": "₫" },
    "expenseDate": "2026-08-01",
    "payers": [
      { "userId": 1, "username": "hungtri", "amount": "800000.00" },
      { "userId": 2, "username": "khanhnt", "amount": "200000.00" }
    ],
    "shares": [
      { "userId": 1, "username": "hungtri", "amount": "250000.00" },
      { "userId": 2, "username": "khanhnt", "amount": "250000.00" },
      { "userId": 3, "username": "anle",    "amount": "250000.00" },
      { "userId": 4, "username": "binhpham","amount": "250000.00" }
    ],
    "debtsCreated": [
      { "fromUserId": 3, "toUserId": 1, "amount": "250000.00" },
      { "fromUserId": 4, "toUserId": 1, "amount": "250000.00" }
    ],
    "createdAt": "2026-07-31T15:00:00"
  }
}
```

---

## GET `/groups/{groupId}/expenses` — List group expenses

**Auth required**: ✅ Bearer Token | **Authorization**: Group member

### Query Parameters
| Param | Type | Description |
|---|---|---|
| `page` | integer | Page number (default: 0) |
| `size` | integer | Items per page (default: 20) |
| `fromDate` | date | Filter from date (`YYYY-MM-DD`) |
| `toDate` | date | Filter to date (`YYYY-MM-DD`) |

### Response `200 OK`
```json
{
  "status": 200,
  "message": "Expenses retrieved successfully",
  "data": {
    "content": [
      {
        "id": 20,
        "description": "Thai hotpot dinner day 1",
        "totalAmount": "1000000.00",
        "currency": { "code": "VND" },
        "expenseDate": "2026-08-01",
        "payerCount": 2,
        "shareCount": 4
      }
    ],
    "totalElements": 5,
    "totalPages": 1,
    "page": 0,
    "size": 20
  }
}
```

---

## GET `/groups/{groupId}/expenses/{expenseId}` — Get expense detail

**Auth required**: ✅ Bearer Token | **Authorization**: Group member

### Response `200 OK`
```json
{
  "status": 200,
  "message": "Expense retrieved successfully",
  "data": {
    "id": 20,
    "description": "Thai hotpot dinner day 1",
    "totalAmount": "1000000.00",
    "currency": { "id": 1, "code": "VND" },
    "expenseDate": "2026-08-01",
    "payers": [ ... ],
    "shares": [ ... ],
    "createdAt": "2026-07-31T15:00:00",
    "updatedAt": "2026-07-31T15:00:00"
  }
}
```

---

## PUT `/groups/{groupId}/expenses/{expenseId}` — Update expense

**Auth required**: ✅ Bearer Token | **Authorization**: Expense creator or Group OWNER

> ⚠️ Updating an expense will **delete and recalculate all associated Debts**.

### Request Body
Same structure as POST request; send the complete updated expense payload.

### Response `200 OK`
```json
{
  "status": 200,
  "message": "Expense updated successfully",
  "data": { ...expense object... }
}
```

---

## DELETE `/groups/{groupId}/expenses/{expenseId}` — Delete expense

**Auth required**: ✅ Bearer Token | **Authorization**: Expense creator or Group OWNER

> ⚠️ Deleting an expense deletes all associated Payers, Shares, and Debts (if no Settlement has been recorded).

### Response `200 OK`
```json
{
  "status": 200,
  "message": "Expense deleted successfully",
  "data": null
}
```

### Common Errors
| Status | Cause |
|---|---|
| `400` | Expense already has associated Settlements and cannot be deleted |
| `403` | Unauthorized to delete this expense |
