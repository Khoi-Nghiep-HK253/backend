# 💱 Currency API

[← Back to overview](./README.md)

---

## GET `/currencies` — List all currencies

**Auth required**: ❌ No / Optional

### Response `200 OK`
```json
{
  "status": 200,
  "message": "Currencies retrieved successfully",
  "data": [
    {
      "id": 1,
      "name": "Vietnamese Dong",
      "code": "VND"
    },
    {
      "id": 2,
      "name": "US Dollar",
      "code": "USD"
    }
  ]
}
```

---

## GET `/currencies/{id}` — Get currency details

**Auth required**: ❌ No / Optional

### Path Parameters
| Param | Type | Description |
|---|---|---|
| `id` | integer | Currency ID |

### Response `200 OK`
```json
{
  "status": 200,
  "message": "Currency retrieved successfully",
  "data": {
    "id": 1,
    "name": "Vietnamese Dong",
    "code": "VND"
  }
}
```

### Common Errors
| Status | Cause |
|---|---|
| `404` | Currency does not exist |

---

## POST `/currencies` — Create a currency

**Auth required**: ✅ Bearer Token

### Request Body
```json
{
  "name": "Euro",
  "acronym": "EUR"
}
```

| Field | Type | Required | Description |
|---|---|---|---|
| `name` | string | ✅ | Currency name (max 50 chars) |
| `acronym` | string | ✅ | Currency code/acronym (max 10 chars, unique) |

### Response `201 Created`
```json
{
  "status": 201,
  "message": "Currency created successfully",
  "data": {
    "id": 3,
    "name": "Euro",
    "code": "EUR"
  }
}
```

### Common Errors
| Status | Cause |
|---|---|
| `400` | Missing required field |
| `409` | Currency with this acronym already exists |

---

## PUT `/currencies/{id}` — Update a currency

**Auth required**: ✅ Bearer Token

### Request Body
```json
{
  "name": "Euro (EUR)",
  "acronym": "EUR"
}
```

### Response `200 OK`
```json
{
  "status": 200,
  "message": "Currency updated successfully",
  "data": {
    "id": 3,
    "name": "Euro (EUR)",
    "code": "EUR"
  }
}
```

### Common Errors
| Status | Cause |
|---|---|
| `404` | Currency does not exist |
| `409` | Currency acronym already taken |

---

## DELETE `/currencies/{id}` — Delete a currency

**Auth required**: ✅ Bearer Token

### Response `200 OK`
```json
{
  "status": 200,
  "message": "Currency deleted successfully",
  "data": null
}
```

### Common Errors
| Status | Cause |
|---|---|
| `404` | Currency does not exist |
