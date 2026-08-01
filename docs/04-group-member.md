# 🙋 Group Member API

[← Back to overview](./README.md)

---

## GET `/groups/{groupId}/members` — List group members

**Auth required**: ✅ Bearer Token | **Authorization**: Group member

### Response `200 OK`
```json
{
  "status": 200,
  "message": "Group members retrieved successfully",
  "data": [
    {
      "id": 1,
      "user": {
        "id": 1,
        "username": "hungtri",
        "firstname": "Hung",
        "lastname": "Tri"
      },
      "role": "OWNER",
      "joinedAt": "2026-07-31T15:00:00"
    },
    {
      "id": 2,
      "user": {
        "id": 2,
        "username": "khanhnt",
        "firstname": "Khanh",
        "lastname": "Nguyen"
      },
      "role": "MEMBER",
      "joinedAt": "2026-07-31T16:00:00"
    }
  ]
}
```

---

## POST `/groups/{groupId}/members` — Add a member to the group

**Auth required**: ✅ Bearer Token | **Authorization**: Group OWNER

### Request Body
```json
{
  "userId": 5
}
```

| Field | Type | Required | Description |
|---|---|---|---|
| `userId` | integer | ✅ | ID of the user to add |

### Response `201 Created`
```json
{
  "status": 201,
  "message": "Member added to group successfully",
  "data": {
    "id": 5,
    "user": {
      "id": 5,
      "username": "newuser",
      "firstname": "New",
      "lastname": "User"
    },
    "role": "MEMBER",
    "joinedAt": "2026-07-31T18:00:00"
  }
}
```

### Common Errors
| Status | Cause |
|---|---|
| `400` | User is already a member of the group |
| `403` | Caller is not the group OWNER |
| `404` | User or group does not exist |

---

## PUT `/groups/{groupId}/members/{memberId}/role` — Update member role

**Auth required**: ✅ Bearer Token | **Authorization**: Group OWNER

### Request Body
```json
{
  "role": "OWNER"
}
```

| Field | Type | Values | Description |
|---|---|---|---|
| `role` | string | `OWNER`, `MEMBER` | New role to assign |

### Response `200 OK`
```json
{
  "status": 200,
  "message": "Member role updated successfully",
  "data": {
    "id": 2,
    "user": {
      "id": 2,
      "username": "khanhnt",
      "firstname": "Khanh",
      "lastname": "Nguyen"
    },
    "role": "OWNER",
    "joinedAt": "2026-07-31T16:00:00"
  }
}
```

### Common Errors
| Status | Cause |
|---|---|
| `400` | Invalid role value, or attempting to downgrade the sole OWNER |
| `403` | Caller is not the group OWNER |
| `404` | Member record does not exist in this group |

---

## DELETE `/groups/{groupId}/members/{memberId}` — Remove member / leave group

**Auth required**: ✅ Bearer Token | **Authorization**: Group OWNER, or the member themselves (to leave)

> ⚠️ The sole OWNER of a group cannot be removed.

### Response `200 OK`
```json
{
  "status": 200,
  "message": "Member removed from group successfully",
  "data": null
}
```

### Common Errors
| Status | Cause |
|---|---|
| `400` | Attempting to remove the last OWNER of the group |
| `403` | Caller is not authorized to remove this member |
| `404` | Member record does not exist |
