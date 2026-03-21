#!/usr/bin/env bash
# =============================================================================
# 本仓库 HTTP API 的 curl 冒烟示例（读操作为主；写操作需替换环境变量）。
#
# 用法：
#   chmod +x scripts/api-lark-smoke.sh && ./scripts/api-lark-smoke.sh
#   BASE_URL=http://127.0.0.1:8080 APP_KEY=default CHAT_ID=oc_xxx ./scripts/api-lark-smoke.sh
#
# 常用环境变量：BASE_URL、APP_KEY、OPEN_ID、CHAT_ID、CHAT_ID_GET、APPROVAL_CODE、INSTANCE_ID、CALENDAR_ID、EVENT_ID
# =============================================================================
set -uo pipefail

BASE_URL="${BASE_URL:-http://127.0.0.1:8080}"
APP_KEY="${APP_KEY:-}"
JSON_HDR=(-H 'Content-Type: application/json; charset=utf-8')

section() {
  echo ""
  echo "######## $1 ########"
}

curl_json() {
  curl -sS "$@" | python3 -m json.tool 2>/dev/null || curl -sS "$@"
  echo ""
}

json_tenant_token_body() {
  APP_KEY="${APP_KEY:-}" python3 <<'PY'
import json, os
k = os.environ.get("APP_KEY", "").strip()
print(json.dumps({"appKey": k} if k else {}, ensure_ascii=False))
PY
}

json_batch_get_id() {
  APP_KEY="${APP_KEY:-}" python3 <<'PY'
import json, os
d = {"userIdType": "user_id", "emails": [], "mobiles": []}
k = os.environ.get("APP_KEY", "").strip()
if k:
    d["appKey"] = k
print(json.dumps(d, ensure_ascii=False))
PY
}

json_send_text_chat() {
  APP_KEY="${APP_KEY:-}" CHAT_ID="${CHAT_ID:-}" python3 <<'PY'
import json, os
d = {
    "receiveIdType": "CHAT_ID",
    "receiveId": os.environ["CHAT_ID"],
    "text": "api-lark-smoke 测试",
}
k = os.environ.get("APP_KEY", "").strip()
if k:
    d["appKey"] = k
print(json.dumps(d, ensure_ascii=False))
PY
}

export APP_KEY

section "GET /api/admin/health — 管理健康检查（含 TAT 是否可用）"
curl_json "${BASE_URL}/api/admin/health"

section "GET / — 根路径重定向到 Swagger UI"
curl -sSI "${BASE_URL}/" | head -5
echo ""

section "GET /api/lark/oapi/apps — 已注册 appKey 与 primary"
curl_json "${BASE_URL}/api/lark/oapi/apps"

section "GET /api/lark/oapi/tenant-access-token — TAT 是否存在（不返回明文）"
curl_json "${BASE_URL}/api/lark/oapi/tenant-access-token"

AK_CHECK="${APP_KEY:-default}"
section "GET /api/lark/oapi/check-app — 校验 Client（appKey=${AK_CHECK}）"
curl_json "${BASE_URL}/api/lark/oapi/check-app?appKey=${AK_CHECK}"

section "GET /api/lark/bot/info — 机器人信息（飞书 bot/v3/info）"
if [[ -n "${APP_KEY}" ]]; then
  curl_json "${BASE_URL}/api/lark/bot/info?appKey=${APP_KEY}"
else
  curl_json "${BASE_URL}/api/lark/bot/info"
fi

section "POST /api/lark/auth/tenant-access-token/internal — 换取 tenant_access_token"
curl_json -X POST "${BASE_URL}/api/lark/auth/tenant-access-token/internal" "${JSON_HDR[@]}" -d "$(json_tenant_token_body)"

section "GET /api/lark/contact/departments — 部门列表（需通讯录权限）"
if [[ -n "${APP_KEY}" ]]; then
  curl_json "${BASE_URL}/api/lark/contact/departments?appKey=${APP_KEY}"
else
  curl_json "${BASE_URL}/api/lark/contact/departments"
fi

if [[ -n "${OPEN_ID:-}" ]]; then
  section "GET /api/lark/contact/users/{open_id} — 用户详情"
  if [[ -n "${APP_KEY}" ]]; then
    curl_json "${BASE_URL}/api/lark/contact/users/${OPEN_ID}?userIdType=open_id&appKey=${APP_KEY}"
  else
    curl_json "${BASE_URL}/api/lark/contact/users/${OPEN_ID}?userIdType=open_id"
  fi
else
  section "GET /api/lark/contact/users/{userId} —（跳过：设置 OPEN_ID=ou_xxx）"
fi

section "POST /api/lark/contact/users/batch-get-id — 邮箱/手机换 id（示例空数组）"
curl_json -X POST "${BASE_URL}/api/lark/contact/users/batch-get-id" "${JSON_HDR[@]}" -d "$(json_batch_get_id)"

if [[ -n "${CHAT_ID_GET:-}" ]]; then
  section "GET /api/lark/chat/{chatId} — 会话信息"
  if [[ -n "${APP_KEY}" ]]; then
    curl_json "${BASE_URL}/api/lark/chat/${CHAT_ID_GET}?appKey=${APP_KEY}"
  else
    curl_json "${BASE_URL}/api/lark/chat/${CHAT_ID_GET}"
  fi
else
  section "GET /api/lark/chat/{chatId} —（跳过：设置 CHAT_ID_GET=oc_xxx）"
fi

if [[ -n "${CHAT_ID:-}" ]]; then
  section "POST /api/lark/im/send-text — 向群发文本（需机器人已在群内）"
  export CHAT_ID
  curl_json -X POST "${BASE_URL}/api/lark/im/send-text" "${JSON_HDR[@]}" -d "$(json_send_text_chat)"
else
  section "POST /api/lark/im/send-text —（跳过：设置 CHAT_ID=oc_xxx）"
fi

if [[ -n "${APPROVAL_CODE:-}" ]]; then
  section "GET /api/lark/approval/approvals/{code} — 审批定义"
  if [[ -n "${APP_KEY}" ]]; then
    curl_json "${BASE_URL}/api/lark/approval/approvals/${APPROVAL_CODE}?appKey=${APP_KEY}"
  else
    curl_json "${BASE_URL}/api/lark/approval/approvals/${APPROVAL_CODE}"
  fi
else
  section "GET /api/lark/approval/approvals/{code} —（跳过：APPROVAL_CODE）"
fi

if [[ -n "${INSTANCE_ID:-}" ]]; then
  section "GET /api/lark/approval/instances/{id} — 审批实例"
  if [[ -n "${APP_KEY}" ]]; then
    curl_json "${BASE_URL}/api/lark/approval/instances/${INSTANCE_ID}?userIdType=user_id&appKey=${APP_KEY}"
  else
    curl_json "${BASE_URL}/api/lark/approval/instances/${INSTANCE_ID}?userIdType=user_id"
  fi
else
  section "GET /api/lark/approval/instances/{id} —（跳过：INSTANCE_ID；复杂场景需 userId）"
fi

if [[ -n "${CALENDAR_ID:-}" && -n "${EVENT_ID:-}" ]]; then
  section "GET /api/lark/calendar/events — 日历事件"
  if [[ -n "${APP_KEY}" ]]; then
    curl_json "${BASE_URL}/api/lark/calendar/events/${CALENDAR_ID}/${EVENT_ID}?appKey=${APP_KEY}"
  else
    curl_json "${BASE_URL}/api/lark/calendar/events/${CALENDAR_ID}/${EVENT_ID}"
  fi
else
  section "GET /api/lark/calendar/events/{cal}/{evt} —（跳过：CALENDAR_ID EVENT_ID）"
fi

section "POST /api/lark/identity/user-info —（需 user_access_token，此处不自动调用）"
echo "body 示例: {\"userAccessToken\":\"...\",\"appKey\":\"optional\"}"
echo ""

section "POST /api/lark/webhook — 飞书事件订阅（需平台配置 URL 与合法 challenge 体）"
echo "${BASE_URL}/api/lark/webhook"
echo ""

section "GET /actuator/health —（若 management 已暴露）"
curl_json "${BASE_URL}/actuator/health" || true

echo ""
echo "完成。"
