# 📨 Invitation API

[← Back to overview](./README.md)

---

## POST `/groups/{groupId}/invitations` — Send an invitation

**Auth required**: ✅ Bearer Token | **Authorization**: Group OWNER

### Request Body
```json
{
  "inviteeId": 5,
  "message": "Join our summer trip group!",
  "expiresAt": "2026-08-01T00:00:00"
}
```

| Field | Type | Required | Description |
|---|---|---|---|
| `inviteeId` | integer | ✅ | ID of the user to invite |
| `message` | string | ❌ | Optional message to include |
| `expiresAt` | datetime | ❌ | When the invitation expires |

### Response `201 Created`
```json
{
  "status": 201,
  "message": "Invitation sent successfully",
  "data": {
    "id": 3,
    "group": { "id": 10, "name": "Summer Trip 2026" },
    "inviter": { "id": 1, "username": "hungtri" },
    "invitee": { "id": 5, "username": "binhpham" },
    "status": "PENDING",
    "token": "tok_xyz789",
    "message": "Join our summer trip group!",
    "expiresAt": "2026-08-01T00:00:00",
    "createdAt": "2026-07-31T15:00:00"
  }
}
```

### Common Errors
| Status | Cause |
|---|---|
| `400` | User is already a member of the group |
| `400` | A pending invitation already exists for this user |
| `403` | Caller is not the group OWNER |
| `404` | Invitee user does not exist |

---

## GET `/groups/{groupId}/invitations` — List group invitations (outbox)

**Auth required**: ✅ Bearer Token | **Authorization**: Group OWNER

### Query Parameters
| Param | Type | Description |
|---|---|---|
| `status` | string | Filter by status: `PENDING`, `ACCEPTED`, `DECLINED`, `EXPIRED`, `REVOKED` |

### Response `200 OK`
```json
{
  "status": 200,
  "message": "Invitations retrieved successfully",
  "data": [
    {
      "id": 3,
      "invitee": { "id": 5, "username": "binhpham" },
      "status": "PENDING",
      "expiresAt": "2026-08-01T00:00:00",
      "createdAt": "2026-07-31T15:00:00"
    }
  ]
}
```

---

## GET `/invitations/me` — My invitations (inbox)

**Auth required**: ✅ Bearer Token

> Returns all invitations received by the current user. Defaults to `PENDING` if no status is provided.

### Query Parameters
| Param | Type | Description |
|---|---|---|
| `status` | string | Filter by status (default: `PENDING`) |

### Response `200 OK`
```json
{
  "status": 200,
  "message": "My invitations retrieved successfully",
  "data": [
    {
      "id": 3,
      "group": { "id": 10, "name": "Summer Trip 2026" },
      "inviter": { "id": 1, "username": "hungtri" },
      "status": "PENDING",
      "message": "Join our summer trip group!",
      "expiresAt": "2026-08-01T00:00:00"
    }
  ]
}
```

---

## PUT `/invitations/{invitationId}/accept` — Accept an invitation

**Auth required**: ✅ Bearer Token | **Authorization**: Invitee only

> After accepting, the user is automatically added to the group with the `MEMBER` role.

### Response `200 OK`
```json
{
  "status": 200,
  "message": "Invitation accepted successfully",
  "data": {
    "invitationId": 3,
    "status": "ACCEPTED",
    "joinedGroup": { "id": 10, "name": "Summer Trip 2026" }
  }
}
```

### Common Errors
| Status | Cause |
|---|---|
| `400` | Invitation has expired or is no longer pending |
| `403` | Caller is not the invitee |

---

## PUT `/invitations/{invitationId}/decline` — Decline an invitation

**Auth required**: ✅ Bearer Token | **Authorization**: Invitee only

### Response `200 OK`
```json
{
  "status": 200,
  "message": "Invitation declined successfully",
  "data": { "invitationId": 3, "status": "DECLINED" }
}
```

---

## PUT `/invitations/{invitationId}/revoke` — Revoke an invitation

**Auth required**: ✅ Bearer Token | **Authorization**: Group OWNER

### Response `200 OK`
```json
{
  "status": 200,
  "message": "Invitation revoked successfully",
  "data": { "invitationId": 3, "status": "REVOKED" }
}
```

### Common Errors
| Status | Cause |
|---|---|
| `400` | Invitation is no longer pending |
| `403` | Caller is not the group OWNER |
