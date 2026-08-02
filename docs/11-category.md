# 🏷️ Category API

[← Back to overview](./README.md)

---

## GET `/categories` — List all categories

**Auth required**: ❌ No / Optional

### Response `200 OK`
```json
{
  "status": 200,
  "message": "Categories retrieved successfully",
  "data": [
    {
      "id": 1,
      "name": "Food & Dining",
      "icon": "food"
    },
    {
      "id": 2,
      "name": "Transportation",
      "icon": "transport"
    }
  ]
}
```

---

## GET `/categories/{id}` — Get category details

**Auth required**: ❌ No / Optional

### Path Parameters
| Param | Type | Description |
|---|---|---|
| `id` | integer | Category ID |

### Response `200 OK`
```json
{
  "status": 200,
  "message": "Category retrieved successfully",
  "data": {
    "id": 1,
    "name": "Food & Dining",
    "icon": "food"
  }
}
```

### Common Errors
| Status | Cause |
|---|---|
| `404` | Category does not exist |

---

## POST `/categories` — Create a category

**Auth required**: ✅ Bearer Token

### Request Body
```json
{
  "name": "Shopping",
  "icon": "shopping"
}
```

| Field | Type | Required | Description |
|---|---|---|---|
| `name` | string | ✅ | Category name (max 100 chars, unique) |
| `icon` | string | ❌ | Icon identifier (max 100 chars) |

### Response `201 Created`
```json
{
  "status": 201,
  "message": "Category created successfully",
  "data": {
    "id": 5,
    "name": "Shopping",
    "icon": "shopping"
  }
}
```

### Common Errors
| Status | Cause |
|---|---|
| `400` | Missing required field or length exceeded |
| `409` | Category name already exists |

---

## PUT `/categories/{id}` — Update a category

**Auth required**: ✅ Bearer Token

### Request Body
```json
{
  "name": "Shopping & Gifts",
  "icon": "gift"
}
```

### Response `200 OK`
```json
{
  "status": 200,
  "message": "Category updated successfully",
  "data": {
    "id": 5,
    "name": "Shopping & Gifts",
    "icon": "gift"
  }
}
```

### Common Errors
| Status | Cause |
|---|---|
| `404` | Category does not exist |
| `409` | Category name already taken |

---

## DELETE `/categories/{id}` — Delete a category

**Auth required**: ✅ Bearer Token

### Response `200 OK`
```json
{
  "status": 200,
  "message": "Category deleted successfully",
  "data": null
}
```

### Common Errors
| Status | Cause |
|---|---|
| `404` | Category does not exist |
