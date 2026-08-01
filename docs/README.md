# Divvy API Documentation

> **Base URL**: `http://localhost:8080/api`
> **Auth**: Bearer JWT Token (`Authorization: Bearer <token>`)
> **Content-Type**: `application/json`

---

## Documentation Index

| Module | File | Description |
|---|---|---|
| 🔐 Authentication | [01-auth.md](./01-auth.md) | Register, login, current user |
| 👤 User | [02-user.md](./02-user.md) | User profile management |
| 👥 Group | [03-group.md](./03-group.md) | Create, update, delete groups |
| 🙋 Group Member | [04-group-member.md](./04-group-member.md) | Manage group members |
| 📨 Invitation | [05-invitation.md](./05-invitation.md) | Send & handle group invitations |
| 💸 Expense | [06-expense.md](./06-expense.md) | Record and manage expenses |
| 🔴 Debt | [07-debt.md](./07-debt.md) | View and track debts |
| ✅ Settlement | [08-settlement.md](./08-settlement.md) | Record debt payments |
| 📋 Activity | [09-activity.md](./09-activity.md) | Group activity history |
| 🗂️ Reference | [10-reference.md](./10-reference.md) | Enum values, error codes, endpoint index |

---

## Standard Response Format

### Success
```json
{
  "status": 200,
  "message": "Success message",
  "data": { ... }
}
```

### Error
```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Specific error description",
  "timestamp": "2026-07-31T15:00:00"
}
```

---

## Business Flow Overview

```
[Register / Login]
        ↓
[Create Group] → [Invite Members] → [Members Accept]
        ↓
[Record an Expense]
  ├── Payers: who put up the money?
  └── Shares: who splits the cost?
        ↓
[System auto-calculates Debts]
        ↓
[Members repay → Settlement]
        ↓
[Review Activity log for reconciliation]
```

---

## HTTP Status Codes

| Code | Meaning |
|---|---|
| `200 OK` | Success |
| `201 Created` | Resource created successfully |
| `400 Bad Request` | Invalid input data |
| `401 Unauthorized` | Not authenticated or token invalid/expired |
| `403 Forbidden` | Authenticated but not authorized for this action |
| `404 Not Found` | Resource does not exist |
| `409 Conflict` | Duplicate data (email, username, etc.) |
| `500 Internal Server Error` | Unexpected server error |
