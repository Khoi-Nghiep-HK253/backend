# 🔴 Debt API

[← Back to overview](./README.md)

---

> ## 📌 Note
> Debts are **automatically generated** when an Expense is created. Developers **do not create Debts manually**.
> This API is strictly for **reading & tracking** debts.

---

## GET `/groups/{groupId}/debts` — List group debts

**Auth required**: ✅ Bearer Token | **Authorization**: Group member

### Query Parameters
| Param | Type | Description |
|---|---|---|
| `status` | string | Filter by status: `PENDING`, `SETTLED`, `CANCELLED` |
| `userId` | integer | Filter by user (either as debtor or creditor) |

### Response `200 OK`
```json
{
  "status": 200,
  "message": "Debts retrieved successfully",
  "data": [
    {
      "id": 5,
      "expense": {
        "id": 20,
        "description": "Thai hotpot dinner day 1"
      },
      "fromUser": { "id": 3, "username": "anle", "fullname": "An Le" },
      "toUser":   { "id": 1, "username": "hungtri", "fullname": "Tri Hung" },
      "amount": "250000.00",
      "status": "PENDING",
      "createdAt": "2026-07-31T15:00:00"
    }
  ]
}
```

---

## GET `/groups/{groupId}/debts/summary` — Group debt summary

**Auth required**: ✅ Bearer Token | **Authorization**: Group member

> Returns a summary table: "User A owes User B total amount" (aggregated across all expenses).

### Response `200 OK`
```json
{
  "status": 200,
  "message": "Debt summary retrieved successfully",
  "data": {
    "pairs": [
      {
        "fromUser": { "id": 3, "username": "anle" },
        "toUser":   { "id": 1, "username": "hungtri" },
        "totalOwed": "500000.00",
        "currency": { "code": "VND" }
      },
      {
        "fromUser": { "id": 4, "username": "binhpham" },
        "toUser":   { "id": 1, "username": "hungtri" },
        "totalOwed": "250000.00",
        "currency": { "code": "VND" }
      }
    ]
  }
}
```

---

## GET `/groups/{groupId}/debts/me` — My debts in group

**Auth required**: ✅ Bearer Token | **Authorization**: Group member

> Returns only debts involving the current authenticated user (either as debtor or creditor).

### Response `200 OK`
```json
{
  "status": 200,
  "message": "My debts retrieved successfully",
  "data": {
    "iOwe": [
      {
        "toUser": { "id": 1, "username": "hungtri" },
        "totalAmount": "500000.00",
        "debts": [ { "id": 5, "amount": "250000.00", "expenseId": 20 } ]
      }
    ],
    "owedToMe": []
  }
}
```

---

## GET `/groups/{groupId}/debts/{debtId}` — Get debt detail

**Auth required**: ✅ Bearer Token | **Authorization**: Group member

### Response `200 OK`
```json
{
  "status": 200,
  "message": "Debt retrieved successfully",
  "data": {
    "id": 5,
    "expense": {
      "id": 20,
      "description": "Thai hotpot dinner day 1",
      "expenseDate": "2026-08-01"
    },
    "fromUser": { "id": 3, "username": "anle" },
    "toUser":   { "id": 1, "username": "hungtri" },
    "amount": "250000.00",
    "status": "PENDING",
    "settlements": [],
    "createdAt": "2026-07-31T15:00:00"
  }
}
```
