# 👥 Group API

[← Back to overview](./README.md)

---

## POST `/groups` — Create a new group

**Auth required**: ✅ Bearer Token

> The creator is automatically assigned the **OWNER** role in the new group.

Category is picked one of two ways — send at most one of `categoryId` / `categoryName`, never both:

```json
{
  "name": "Summer Trip 2026",
  "categoryId": 1,
  "note": "Team summer outing",
  "startDate": "2026-08-01",
  "endDate": "2026-08-05"
}
```
```json
{
  "name": "Summer Trip 2026",
  "categoryName": "Travel",
  "note": "Team summer outing",
  "startDate": "2026-08-01",
  "endDate": "2026-08-05"
}
```

| Field | Type | Required | Description |
|---|---|---|---|
| `name` | string | ✅ | Group name, max 150 characters |
| `categoryId` | integer | ❌ | Pick an existing category by ID. 404 if it doesn't exist. Mutually exclusive with `categoryName` |
| `categoryName` | string | ❌ | Pick a category by name — case-insensitive match against an existing category, or **auto-created** if none matches. Mutually exclusive with `categoryId` |
| `note` | string | ❌ | Group note |
| `startDate` | date | ❌ | Start date (`YYYY-MM-DD`) |
| `endDate` | date | ❌ | End date (`YYYY-MM-DD`) |

> Sending both `categoryId` and `categoryName` in the same request is a `400 Bad Request`.
>
> Not sure what category to pick? Call `POST /groups/suggest-category` below first, then drop its response straight into this request.

### Response `201 Created`
```json
{
  "status": 201,
  "message": "Group created successfully",
  "data": {
    "id": 10,
    "name": "Summer Trip 2026",
    "category": { "id": 1, "name": "Travel" },
    "note": "Team summer outing",
    "startDate": "2026-08-01",
    "endDate": "2026-08-05",
    "createdAt": "2026-07-31T15:00:00"
  }
}
```

---

## POST `/groups/suggest-category` — Suggest a category for a new group (AI)

**Auth required**: ✅ Bearer Token

> Sends the group's name/note to Google Gemini and suggests a category. **Nothing is persisted** — the response is shaped like the category fields of `POST /groups` above, so the client pre-fills its "create group" form and the user reviews/edits before actually creating the group.

### Request Body
```json
{
  "name": "Summer Trip 2026",
  "note": "Team summer outing, hotel + flights"
}
```

| Field | Type | Required | Description |
|---|---|---|---|
| `name` | string | ✅ | Group name, max 150 characters |
| `note` | string | ❌ | Group note — extra context for the suggestion |

### Response `200 OK`

Either an existing category matched:
```json
{
  "status": 200,
  "message": "Category suggestion generated successfully",
  "data": { "categoryId": 3, "categoryName": null }
}
```
...or none fit well, so a new name is proposed (not yet created — it's created only if you go on to send this `categoryName` to `POST /groups`):
```json
{
  "status": 200,
  "message": "Category suggestion generated successfully",
  "data": { "categoryId": null, "categoryName": "Người thân" }
}
```

> `503 Service Unavailable` if the AI call fails.

---

## GET `/groups` — List my groups

**Auth required**: ✅ Bearer Token

> Returns only groups where the current user is a member.

### Query Parameters
| Param | Type | Description |
|---|---|---|
| `page` | integer | Page number (default: 0) |
| `size` | integer | Items per page (default: 20) |

### Response `200 OK`
```json
{
  "status": 200,
  "message": "Groups retrieved successfully",
  "data": {
    "content": [
      {
        "id": 10,
        "name": "Summer Trip 2026",
        "category": { "id": 1, "name": "Travel" },
        "memberCount": 4,
        "startDate": "2026-08-01",
        "endDate": "2026-08-05"
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

## GET `/groups/{groupId}` — Get group details

**Auth required**: ✅ Bearer Token | **Authorization**: Group member

### Response `200 OK`
```json
{
  "status": 200,
  "message": "Group retrieved successfully",
  "data": {
    "id": 10,
    "name": "Summer Trip 2026",
    "category": { "id": 1, "name": "Travel" },
    "note": "Team summer outing",
    "startDate": "2026-08-01",
    "endDate": "2026-08-05",
    "memberCount": 4,
    "totalExpense": "4500000.00",
    "createdAt": "2026-07-31T15:00:00",
    "updatedAt": "2026-07-31T15:00:00"
  }
}
```

---

## PUT `/groups/{groupId}` — Update group

**Auth required**: ✅ Bearer Token | **Authorization**: Group OWNER

### Request Body
```json
{
  "name": "Summer Trip 2026 (Updated)",
  "note": "Added extra notes",
  "endDate": "2026-08-07"
}
```

> Only include the fields you want to change (partial update). To change the category, send `categoryId` or `categoryName` — same rules as create (see above), mutually exclusive.

### Response `200 OK`
```json
{
  "status": 200,
  "message": "Group updated successfully",
  "data": { ...group object... }
}
```

---

## DELETE `/groups/{groupId}` — Delete group

**Auth required**: ✅ Bearer Token | **Authorization**: Group OWNER

> ⚠️ Deleting a group permanently removes all associated data (expenses, debts, members, invitations). This action is irreversible.

### Response `200 OK`
```json
{
  "status": 200,
  "message": "Group deleted successfully",
  "data": null
}
```

### Common Errors
| Status | Cause |
|---|---|
| `403` | Caller is not the group OWNER |
| `404` | Group does not exist |
