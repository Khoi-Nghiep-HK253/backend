#!/usr/bin/env bash
# Quick manual test for the AI receipt-scan feature.
#
# Usage:
#   ./scripts/test-receipt-scan.sh <path-to-receipt-image> [--create]
#
#   --create   also POST the returned draft to /expenses to persist a real expense
#              (default: only scans and prints the draft, nothing is saved)
#
# Overridable via env vars: BASE_URL, USERNAME, PASSWORD, GROUP_NAME
# Requires: curl, jq

set -euo pipefail

BASE_URL="${BASE_URL:-http://localhost:8080}"
USERNAME="${USERNAME:-trihung}"
PASSWORD="${PASSWORD:-123456}"
GROUP_NAME="${GROUP_NAME:-Chuyến đi Đà Lạt}"

IMAGE_PATH="${1:-}"
CREATE_FLAG="${2:-}"

if [[ -z "$IMAGE_PATH" ]]; then
    echo "Usage: $0 <path-to-receipt-image> [--create]" >&2
    exit 1
fi

if [[ ! -f "$IMAGE_PATH" ]]; then
    echo "Error: file not found: $IMAGE_PATH" >&2
    exit 1
fi

echo "==> Logging in as $USERNAME ..."
TOKEN=$(curl -sf -X POST "$BASE_URL/api/auth/login" \
    -H "Content-Type: application/json" \
    -d "{\"usernameOrEmail\":\"$USERNAME\",\"password\":\"$PASSWORD\"}" | jq -r '.data.accessToken')

if [[ -z "$TOKEN" || "$TOKEN" == "null" ]]; then
    echo "Error: login failed (check BASE_URL/USERNAME/PASSWORD, and that the app is running)." >&2
    exit 1
fi

echo "==> Looking up group \"$GROUP_NAME\" ..."
GROUP_ID=$(curl -sf "$BASE_URL/api/groups" \
    -H "Authorization: Bearer $TOKEN" | jq -r --arg name "$GROUP_NAME" '.data.content[] | select(.name == $name) | .id')

if [[ -z "$GROUP_ID" ]]; then
    echo "Error: no group named \"$GROUP_NAME\" found for this user." >&2
    exit 1
fi
echo "    groupId = $GROUP_ID"

echo "==> Scanning receipt: $IMAGE_PATH ..."
DRAFT_FILE=$(mktemp /tmp/receipt_draft.XXXXXX.json)
curl -sf -X POST "$BASE_URL/api/groups/$GROUP_ID/expenses/scan-receipt" \
    -H "Authorization: Bearer $TOKEN" \
    -F "image=@$IMAGE_PATH" | jq '.data' > "$DRAFT_FILE"

if [[ ! -s "$DRAFT_FILE" || "$(cat "$DRAFT_FILE")" == "null" ]]; then
    echo "Error: scan-receipt returned no data (check server logs)." >&2
    exit 1
fi

echo "==> Draft result:"
cat "$DRAFT_FILE"

if [[ "$CREATE_FLAG" == "--create" ]]; then
    echo ""
    echo "==> Creating real expense from draft ..."
    curl -sf -X POST "$BASE_URL/api/groups/$GROUP_ID/expenses" \
        -H "Authorization: Bearer $TOKEN" \
        -H "Content-Type: application/json" \
        -d @"$DRAFT_FILE" | jq
fi

rm -f "$DRAFT_FILE"
