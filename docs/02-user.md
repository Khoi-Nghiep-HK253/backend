# 👤 User API

[← Back to overview](./README.md)

---

## GET `/users/{id}` — Get user details

**Auth required**: ✅ Bearer Token

### Path Parameters
| Param | Type | Description |
|---|---|---|
| `id` | integer | The user's ID |

### Response `200 OK`
```json
{
  "status": 200,
  "message": "User retrieved successfully",
  "data": {
    "id": 1,
    "username": "hungtri",
    "email": "hung@example.com",
    "firstname": "Hung",
    "lastname": "Tri",
    "phone": "0912345678",
    "avatar": "https://..."
  }
}
```

---

## PUT `/users/{id}` — Update user profile

**Auth required**: ✅ Bearer Token | **Authorization**: Account owner only

### Path Parameters
| Param | Type | Description |
|---|---|---|
| `id` | integer | The user's ID |

### Request Body
```json
{
  "firstname": "Hung",
  "lastname": "Tri",
  "phone": "0912345678",
  "avatar": "https://cdn.example.com/avatar.jpg"
}
```

| Field | Type | Required | Description |
|---|---|---|---|
| `firstname` | string | ❌ | First name |
| `lastname` | string | ❌ | Last name |
| `phone` | string | ❌ | Phone number |
| `avatar` | string | ❌ | Profile picture URL |

> Only send the fields you want to update (partial update).

### Response `200 OK`
```json
{
  "status": 200,
  "message": "User updated successfully",
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
| `403` | Attempting to edit another user's profile |
| `404` | User does not exist |

---

## PATCH `/users/{id}/password` — Change password

**Auth required**: ✅ Bearer Token | **Authorization**: Account owner only

### Request Body
```json
{
  "currentPassword": "123456",
  "newPassword": "newpass123"
}
```

### Response `200 OK`
```json
{
  "status": 200,
  "message": "Password changed successfully",
  "data": null
}
```

### Common Errors
| Status | Cause |
|---|---|
| `400` | `currentPassword` is incorrect |
| `403` | Attempting to change another user's password |
