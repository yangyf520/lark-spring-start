#!/usr/bin/env bash
# 零散 curl 示例；完整列表请用：./scripts/api-lark-smoke.sh
set -euo pipefail
BASE="${BASE_URL:-http://127.0.0.1:8080}"

curl -sS "${BASE}/api/lark/bot/info" | python3 -m json.tool 2>/dev/null || curl -sS "${BASE}/api/lark/bot/info"

curl -sS -X POST "${BASE}/api/lark/contact/users/batch-get-id" \
  -H 'Content-Type: application/json' \
  -d '{"userIdType":"user_id","mobiles":["你的手机号"],"emails":[]}'

curl -sS -X POST "${BASE}/api/lark/im/send-text" \
  -H 'Content-Type: application/json' \
  -d '{"receiveIdType":"CHAT_ID","receiveId":"oc_群chat_id","text":"你好"}'
