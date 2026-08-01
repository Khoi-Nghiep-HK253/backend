# 🔐 Authentication API

[← Back to overview](./README.md)

---

## POST `/auth/register` — Register a new account

**Auth required**: ❌ No

### Request Body
```json
{
  "username": "hungtri",
  "email": "hung@example.com",
  "password": "123456",
  "firstname": "Hung",
  "lastname": "Tri",
  "phone": "0912345678"
}
```

| Field | Type | Required | Description |
|---|---|---|---|
| `username` | string | ✅ | 3–50 characters, no spaces |
| `email` | string | ✅ | Valid email format |
| `password` | string | ✅ | Minimum 6 characters |
| `firstname` | string | ❌ | First name |
| `lastname` | string | ❌ | Last name |
| `phone` | string | ❌ | Phone number |

### Response `201 Created`
```json
{
  "status": 201,
  "message": "User registered successfully",
  "data": {
    "accessToken": "eyJhbGci...",
    "tokenType": "Bearer",
    "user": {
      "id": 1,
      "username": "hungtri",
      "email": "hung@example.com",
      "firstname": "Hung",
      "lastname": "Tri",
      "phone": "0912345678"
    }
  }
}
```

### Common Errors
| Status | Cause |
|---|---|
| `400` | Missing required field or invalid email format |
| `409` | Username or email already exists |

---

## POST `/auth/login` — Login

**Auth required**: ❌ No

### Request Body
```json
{
  "usernameOrEmail": "hungtri",
  "password": "123456"
}
```

| Field | Type | Required | Description |
|---|---|---|---|
| `usernameOrEmail` | string | ✅ | Username or email address |
| `password` | string | ✅ | Account password |

### Response `200 OK`
```json
{
  "status": 200,
  "message": "User logged in successfully",
  "data": {
    "accessToken": "eyJhbGci...",
    "tokenType": "Bearer",
    "user": {
      "id": 1,
      "username": "hungtri",
      "email": "hung@example.com",
      "firstname": "Hung",
      "lastname": "Tri"
    }
  }
}
```

### Common Errors
| Status | Cause |
|---|---|
| `401` | Incorrect username/email or password |

---

## GET `/auth/me` — Get current user profile

**Auth required**: ✅ Bearer Token

### Response `200 OK`
```json
{
  "status": 200,
  "message": "Current user retrieved successfully",
  "data": {
    "id": 1,
    "username": "hungtri",
    "email": "hung@example.com",
    "firstname": "Hung",
    "lastname": "Tri",
    "phone": "0912345678"
  }
}
```

### Common Errors
| Status | Cause |
|---|---|
| `401` | Token invalid or expired |

---

## Forgot Password Flow

```
[User enters email]
       ↓
POST /auth/forgot-password
       ↓
System sends email with reset link (containing token)
       ↓
[User clicks link → enters new password]
       ↓
POST /auth/reset-password  (token + newPassword)
       ↓
Password changed, token invalidated
```

---

## POST `/auth/forgot-password` — Request password reset

**Auth required**: ❌ No

> The system will send a password reset link to the provided email address.
> **To prevent user enumeration**, the response is always `200 OK` regardless of whether the email exists.

### Request Body
```json
{
  "email": "hung@example.com"
}
```

| Field | Type | Required | Description |
|---|---|---|---|
| `email` | string | ✅ | Registered email address |

### Response `200 OK`
```json
{
  "status": 200,
  "message": "If this email is registered, a password reset link has been sent.",
  "data": null
}
```

### Common Errors
| Status | Cause |
|---|---|
| `400` | Invalid email format |

---

## GET `/auth/reset-password/verify` — Verify reset token

**Auth required**: ❌ No

> Used by the frontend to validate a token before displaying the new password form.

### Query Parameters
| Param | Type | Required | Description |
|---|---|---|---|
| `token` | string | ✅ | Token from the reset email link |

### Response `200 OK` — Token is valid
```json
{
  "status": 200,
  "message": "Token is valid",
  "data": {
    "email": "h***@example.com",
    "expiresAt": "2026-07-31T16:00:00"
  }
}
```

### Common Errors
| Status | Cause |
|---|---|
| `400` | Token invalid or already used |
| `410` | Token has expired |

---

## POST `/auth/reset-password` — Set new password

**Auth required**: ❌ No

> After a successful reset, the token is **immediately invalidated** and cannot be reused.

### Request Body
```json
{
  "token": "reset_tok_abc123xyz",
  "newPassword": "newSecurePass456",
  "confirmPassword": "newSecurePass456"
}
```

| Field | Type | Required | Description |
|---|---|---|---|
| `token` | string | ✅ | Token from the reset email link |
| `newPassword` | string | ✅ | New password (minimum 6 characters) |
| `confirmPassword` | string | ✅ | Confirmation of the new password |

### Response `200 OK`
```json
{
  "status": 200,
  "message": "Password has been reset successfully. Please log in with your new password.",
  "data": null
}
```

### Common Errors
| Status | Cause |
|---|---|
| `400` | `newPassword` and `confirmPassword` do not match |
| `400` | Token invalid or already used |
| `400` | New password is the same as the old password |
| `410` | Token has expired — request a new reset email |
