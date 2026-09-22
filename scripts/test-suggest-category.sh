#!/usr/bin/env bash
# Quick manual test for the AI group-category-suggestion feature.
#
# Usage:
#   ./scripts/test-suggest-category.sh "<group name>" ["<note>"] [--create]
#
#   --create   also create a real group from the suggestion via POST /api/groups
#              (default: only suggests and prints the result, nothing is saved)
#
# Overridable via env vars: BASE_URL, USERNAME, PASSWORD
# Requires: curl, jq

set -euo pipefail

BASE_URL="${BASE_URL:-http://localhost:8080}"
USERNAME="${USERNAME:-trihung}"
PASSWORD="${PASSWORD:-123456}"

NAME="${1:-}"
shift || true

NOTE=""
CREATE_FLAG=""
for arg in "$@"; do
    if [[ "$arg" == "--create" ]]; then
        CREATE_FLAG="--create"
    else
        NOTE="$arg"
    fi
done

if [[ -z "$NAME" ]]; then
    echo "Usage: $0 \"<group name>\" [\"<note>\"] [--create]" >&2
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

echo "==> Suggesting category for group \"$NAME\"${NOTE:+ (note: \"$NOTE\")} ..."
REQUEST=$(jq -n --arg name "$NAME" --arg note "$NOTE" \
    '{name: $name} + (if $note == "" then {} else {note: $note} end)')

SUGGEST_RESPONSE=$(curl -s -X POST "$BASE_URL/api/groups/suggest-category" \
    -H "Authorization: Bearer $TOKEN" \
    -H "Content-Type: application/json" \
    -d "$REQUEST") \
    || fail "cannot reach $BASE_URL"

SUGGESTION=$(echo "$SUGGEST_RESPONSE" | jq '.data // empty')
[[ -n "$SUGGESTION" ]] || fail "suggest-category failed. Server response: $SUGGEST_RESPONSE"

echo "==> Suggested category:"
echo "$SUGGESTION"

if [[ "$CREATE_FLAG" == "--create" ]]; then
    echo ""
    echo "==> Creating real group from suggestion ..."
    BODY=$(jq -n --arg name "$NAME" --arg note "$NOTE" --argjson category "$SUGGESTION" '
        {name: $name}
        + (if $note == "" then {} else {note: $note} end)
        + ($category | with_entries(select(.value != null)))') \
        || fail "could not build group body"

    curl -s -X POST "$BASE_URL/api/groups" \
        -H "Authorization: Bearer $TOKEN" \
        -H "Content-Type: application/json" \
        -d "$BODY" | jq
fi
