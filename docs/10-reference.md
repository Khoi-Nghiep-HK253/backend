# 🗂️ Reference — Enum Values & Error Codes

[← Back to overview](./README.md)

---

## Enum Values

### `InvitationStatus`
| Value | Description |
|---|---|
| `PENDING` | Invitation pending response |
| `ACCEPTED` | Accepted by invitee |
| `DECLINED` | Declined by invitee |
| `EXPIRED` | Expired before response |
| `REVOKED` | Revoked by inviter/OWNER |

### `DebtStatus`
| Value | Description |
|---|---|
| `PENDING` | Unsettled debt |
| `SETTLED` | Fully paid debt |
| `CANCELLED` | Cancelled (due to expense deletion/update) |

### `GroupMember.role`
| Value | Description |
|---|---|
| `OWNER` | Group Owner — full group management permissions |
| `MEMBER` | Standard member |

### `Settlement.method`
| Value | Description |
|---|---|
| `CASH` | Cash payment (default) |
| `TRANSFER` | Bank transfer |

### `User.role` (System)
| Value | Description |
|---|---|
| `USER` | Standard user |
| `ADMIN` | System administrator |

---

## Complete Endpoints Summary

### 🔐 Auth
| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/auth/register` | Register account |
| POST | `/api/auth/login` | Login |
| GET | `/api/auth/me` | Current user profile |
| POST | `/api/auth/forgot-password` | Request password reset via email |
| GET | `/api/auth/reset-password/verify` | Verify reset token validity |
| POST | `/api/auth/reset-password` | Set new password |

### 👤 User
| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/users/{id}` | View user profile |
| PUT | `/api/users/{id}` | Update user profile |
| PATCH | `/api/users/{id}/password` | Change password |

### 👥 Group
| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/groups` | Create group |
| GET | `/api/groups` | List my joined groups |
| GET | `/api/groups/{groupId}` | Get group details |
| PUT | `/api/groups/{groupId}` | Update group |
| DELETE | `/api/groups/{groupId}` | Delete group |

### 🙋 Group Member
| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/groups/{groupId}/members` | List members |
| PUT | `/api/groups/{groupId}/members/{memberId}/role` | Update member role |
| DELETE | `/api/groups/{groupId}/members/{memberId}` | Remove member / leave group |

### 📨 Invitation
| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/groups/{groupId}/invitations` | Send invitation |
| GET | `/api/groups/{groupId}/invitations` | List group invitations |
| GET | `/api/invitations/me` | My invitations (inbox) |
| PUT | `/api/invitations/{id}/accept` | Accept invitation |
| PUT | `/api/invitations/{id}/decline` | Decline invitation |
| PUT | `/api/invitations/{id}/revoke` | Revoke invitation |

### 💸 Expense
| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/groups/{groupId}/expenses` | Create expense |
| GET | `/api/groups/{groupId}/expenses` | List group expenses |
| GET | `/api/groups/{groupId}/expenses/{expenseId}` | Get expense detail |
| PUT | `/api/groups/{groupId}/expenses/{expenseId}` | Update expense |
| DELETE | `/api/groups/{groupId}/expenses/{expenseId}` | Delete expense |

### 🔴 Debt
| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/groups/{groupId}/debts` | List debts |
| GET | `/api/groups/{groupId}/debts/summary` | Group debt summary |
| GET | `/api/groups/{groupId}/debts/me` | My debts |
| GET | `/api/groups/{groupId}/debts/{debtId}` | Get debt detail |

### ✅ Settlement
| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/groups/{groupId}/settlements` | Record debt payment |
| GET | `/api/groups/{groupId}/settlements` | List settlements |
| GET | `/api/groups/{groupId}/settlements/{settlementId}` | Get settlement detail |

### 📋 Activity
| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/groups/{groupId}/activities` | Group activity history |
| GET | `/api/users/{userId}/activities` | User activity history |

### 🏷️ Category
| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/categories` | List all categories |
| GET | `/api/categories/{id}` | Get category detail |
| POST | `/api/categories` | Create a category |
| PUT | `/api/categories/{id}` | Update a category |
| DELETE | `/api/categories/{id}` | Delete a category |

### 💱 Currency
| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/currencies` | List all currencies |
| GET | `/api/currencies/{id}` | Get currency detail |
| POST | `/api/currencies` | Create a currency |
| PUT | `/api/currencies/{id}` | Update a currency |
| DELETE | `/api/currencies/{id}` | Delete a currency |

---

## Authorization Summary

| Role | Permissions |
|---|---|
| **SYSTEM ADMIN** | Full system permissions |
| **Group OWNER** | Group management: invite, remove member, update group, revoke invitations |
| **Group MEMBER** | View group, create expenses, record settlements, leave group voluntarily |
| **Account Owner** | Edit own profile, change own password |
