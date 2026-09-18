#!/usr/bin/env bash
# Quick manual test for the AI receipt-scan feature.
#
# Usage:
#   ./scripts/test-receipt-scan.sh <path-to-receipt-image> [--create]
#
#   --create   also create a real expense from the scanned data via POST /expenses, with the
#              caller as sole payer and an EQUAL split across all members (default: only scans
#              and prints the result, nothing is saved)
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

fail() {
    echo "Error: $1" >&2
    exit 1
}

echo "==> Logging in as $USERNAME ..."
LOGIN_RESPONSE=$(curl -s -X POST "$BASE_URL/api/auth/login" \
    -H "Content-Type: application/json" \
    -d "{\"usernameOrEmail\":\"$USERNAME\",\"password\":\"$PASSWORD\"}") \
    || fail "cannot reach $BASE_URL — is the backend running? (./gradlew bootRun)"

TOKEN=$(echo "$LOGIN_RESPONSE" | jq -r '.data.accessToken // empty')
[[ -n "$TOKEN" ]] || fail "login failed. Server response: $LOGIN_RESPONSE"

echo "==> Looking up group \"$GROUP_NAME\" ..."
GROUPS_RESPONSE=$(curl -s "$BASE_URL/api/groups" -H "Authorization: Bearer $TOKEN") \
    || fail "cannot reach $BASE_URL"

GROUP_ID=$(echo "$GROUPS_RESPONSE" | jq -r --arg name "$GROUP_NAME" '.data.content[]? | select(.name == $name) | .id')
[[ -n "$GROUP_ID" ]] || fail "no group named \"$GROUP_NAME\" found for this user. Server response: $GROUPS_RESPONSE"
echo "    groupId = $GROUP_ID"

echo "==> Scanning receipt: $IMAGE_PATH ..."
SCAN_RESPONSE=$(curl -s -X POST "$BASE_URL/api/groups/$GROUP_ID/expenses/scan-receipt" \
    -H "Authorization: Bearer $TOKEN" \
    -F "image=@$IMAGE_PATH") \
    || fail "cannot reach $BASE_URL"

DRAFT=$(echo "$SCAN_RESPONSE" | jq '.data // empty')
[[ -n "$DRAFT" ]] || fail "scan-receipt failed. Server response: $SCAN_RESPONSE"

echo "==> Draft result:"
echo "$DRAFT"

if [[ "$CREATE_FLAG" == "--create" ]]; then
    # The scan only returns description/amount/currency. Payers, shares and split type come
    # from the user in the real UI — here we simulate that: caller pays all, EQUAL split across members.
    echo ""
    echo "==> Creating real expense (caller pays all, equal split across all members) ..."
    MEMBERS_RESPONSE=$(curl -s "$BASE_URL/api/groups/$GROUP_ID/members" -H "Authorization: Bearer $TOKEN") \
        || fail "cannot reach $BASE_URL"

    BODY=$(jq -n --argjson draft "$DRAFT" --argjson members "$MEMBERS_RESPONSE" --arg me "$USERNAME" --arg today "$(date +%F)" '
        ($members.data | map(.user)) as $users
        | ($users | map(select(.username == $me)) | .[0].id) as $meId
        | {
            description: $draft.description,
            totalAmount: $draft.totalAmount,
            currencyId: $draft.currency.id,
            expenseDate: $today,
            splitType: "EQUAL",
            payers: [{userId: $meId, amount: $draft.totalAmount}],
            shares: ($users | map({userId: .id}))
          }') || fail "could not build expense body. Members response: $MEMBERS_RESPONSE"

    curl -s -X POST "$BASE_URL/api/groups/$GROUP_ID/expenses" \
        -H "Authorization: Bearer $TOKEN" \
        -H "Content-Type: application/json" \
        -d "$BODY" | jq
fi
