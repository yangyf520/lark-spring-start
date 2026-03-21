#!/usr/bin/env bash
# =============================================================================
# 本仓库 HTTP 接口说明与 curl 示例（可复制执行）。
# 统一 JSON 业务接口返回 { ok, time, data, error, message }；运维 / 重定向等除外。
#
# 用法：
#   chmod +x scripts/send-lark-test.sh && ./scripts/send-lark-test.sh
#   BASE_URL=http://127.0.0.1:8080 APP_KEY=default CHAT_ID=oc_xxx ./scripts/send-lark-test.sh
#
# 更完整的冒烟遍历见：./scripts/api-lark-smoke.sh
# =============================================================================
set -uo pipefail

BASE="${BASE_URL:-http://127.0.0.1:8080}"
APP_KEY="${APP_KEY:-}"
JSON_HDR=(-H 'Content-Type: application/json; charset=utf-8')

curl_json() {
  curl -sS "$@" | python3 -m json.tool 2>/dev/null || curl -sS "$@"
  echo ""
}

section() {
  echo ""
  echo "######## $1 ########"
}

# --- GET / ---
# 说明：根路径 302 重定向到 Swagger UI（非 ApiResponse）。
section "GET / — 根路径重定向到 Swagger"
curl -sSI "${BASE}/" | head -8
echo ""

# --- GET /api/admin/health ---
# 说明：本机运维；返回 ok、时间与 tenant_access_token 是否已缓存（不含明文）。
section "GET /api/admin/health — 运维健康（Map，非 ApiResponse 包装）"
curl_json "${BASE}/api/admin/health"

# --- OAPI 注册与探测 ---
# GET /api/lark/oapi/apps — 说明：列出 primaryKey 与已注册 appKey。
section "GET /api/lark/oapi/apps — 多应用 Client 注册摘要"
curl_json "${BASE}/api/lark/oapi/apps"

# GET /api/lark/oapi/tenant-access-token — 说明：primary 应用 TAT 是否已缓存（布尔，无明文）。
section "GET /api/lark/oapi/tenant-access-token — TAT 是否已就绪"
curl_json "${BASE}/api/lark/oapi/tenant-access-token"

# GET /api/lark/oapi/check-app — 说明：校验 appKey 能否解析为 Client。
section "GET /api/lark/oapi/check-app — 校验 Client（替换 appKey）"
curl_json "${BASE}/api/lark/oapi/check-app?appKey=${APP_KEY:-default}"

# --- 鉴权 ---
# POST /api/lark/auth/tenant-access-token/internal — 说明：用 app_id/app_secret 换 tenant_access_token；body 可 {\"appKey\":\"xxx\"} 或 {}。
section "POST /api/lark/auth/tenant-access-token/internal — 换取 TAT"
curl_json -X POST "${BASE}/api/lark/auth/tenant-access-token/internal" "${JSON_HDR[@]}" \
  -d "{\"appKey\":\"${APP_KEY}\"}"

# POST /api/lark/auth/access-token — 说明：OAuth code 换 user_access_token。
section "POST /api/lark/auth/access-token — code 换用户 token（替换 code）"
curl_json -X POST "${BASE}/api/lark/auth/access-token" "${JSON_HDR[@]}" \
  -d "{\"appKey\":\"${APP_KEY}\",\"code\":\"oauth_code\",\"grantType\":\"authorization_code\"}"

# POST /api/lark/auth/refresh-access-token — 说明：刷新用户 access_token。
section "POST /api/lark/auth/refresh-access-token — 刷新用户 token"
curl_json -X POST "${BASE}/api/lark/auth/refresh-access-token" "${JSON_HDR[@]}" \
  -d "{\"appKey\":\"${APP_KEY}\",\"refreshToken\":\"refresh_token\",\"grantType\":\"refresh_token\"}"

# --- 机器人 ---
# GET /api/lark/bot/info — 说明：飞书 bot/v3/info；data 为机器人信息。
section "GET /api/lark/bot/info — 机器人信息"
if [[ -n "${APP_KEY}" ]]; then
  curl_json "${BASE}/api/lark/bot/info?appKey=${APP_KEY}"
else
  curl_json "${BASE}/api/lark/bot/info"
fi

# --- 通讯录 ---
# GET /api/lark/contact/users/{userId} — 说明：按 userId 查用户；userIdType 默认 open_id。
section "GET /api/lark/contact/users/{userId} — 用户详情（替换 OPEN_ID）"
OPEN_ID="${OPEN_ID:-ou_placeholder}"
curl_json "${BASE}/api/lark/contact/users/${OPEN_ID}?userIdType=open_id&appKey=${APP_KEY}"

# GET /api/lark/contact/departments — 说明：分页列子部门。
section "GET /api/lark/contact/departments — 部门列表"
if [[ -n "${APP_KEY}" ]]; then
  curl_json "${BASE}/api/lark/contact/departments?appKey=${APP_KEY}"
else
  curl_json "${BASE}/api/lark/contact/departments"
fi

# POST /api/lark/contact/users/batch-get-id — 说明：邮箱/手机批量换 user_id。
section "POST /api/lark/contact/users/batch-get-id — 批量换 ID"
curl_json -X POST "${BASE}/api/lark/contact/users/batch-get-id" "${JSON_HDR[@]}" \
  -d "{\"userIdType\":\"user_id\",\"mobiles\":[\"手机号\"],\"emails\":[],\"appKey\":\"${APP_KEY}\"}"

# --- 群会话 ---
# GET /api/lark/chat/{chatId} — 说明：按 chat_id 取会话信息。
section "GET /api/lark/chat/{chatId} — 会话信息（替换 CHAT_ID_GET）"
CHAT_ID_GET="${CHAT_ID_GET:-oc_placeholder}"
curl_json "${BASE}/api/lark/chat/${CHAT_ID_GET}?appKey=${APP_KEY}"

# POST /api/lark/chat — 说明：创建群；body 为飞书 CreateChatReqBody 结构。
section "POST /api/lark/chat — 创建群（body 需合法 CreateChatReqBody）"
curl_json -X POST "${BASE}/api/lark/chat" "${JSON_HDR[@]}" \
  -d "{\"appKey\":\"${APP_KEY}\",\"body\":{\"name\":\"test\"}}"

# --- IM ---
# POST /api/lark/im/send-text — 说明：向用户或群发文本；receiveIdType 为枚举名如 CHAT_ID。
section "POST /api/lark/im/send-text — 发文本"
CHAT_ID="${CHAT_ID:-oc_placeholder}"
curl_json -X POST "${BASE}/api/lark/im/send-text" "${JSON_HDR[@]}" \
  -d "{\"receiveIdType\":\"CHAT_ID\",\"receiveId\":\"${CHAT_ID}\",\"text\":\"hello\",\"appKey\":\"${APP_KEY}\"}"

# POST /api/lark/im/send-card — 说明：发交互卡片；cardJson 为卡片 JSON 字符串。
section "POST /api/lark/im/send-card — 发卡片"
curl_json -X POST "${BASE}/api/lark/im/send-card" "${JSON_HDR[@]}" \
  -d "{\"receiveIdType\":\"CHAT_ID\",\"receiveId\":\"${CHAT_ID}\",\"cardJson\":\"{}\",\"appKey\":\"${APP_KEY}\"}"

# POST /api/lark/im/update-message — 说明：更新已发送消息。
section "POST /api/lark/im/update-message — 更新消息"
curl_json -X POST "${BASE}/api/lark/im/update-message" "${JSON_HDR[@]}" \
  -d "{\"messageId\":\"om_xxx\",\"contentJson\":\"{}\",\"appKey\":\"${APP_KEY}\"}"

# --- 身份 ---
# POST /api/lark/identity/user-info — 说明：需有效 user_access_token。
section "POST /api/lark/identity/user-info — 用户资料（需 user_access_token）"
curl_json -X POST "${BASE}/api/lark/identity/user-info" "${JSON_HDR[@]}" \
  -d "{\"userAccessToken\":\"uat_xxx\",\"appKey\":\"${APP_KEY}\"}"

# --- 审批 ---
# GET /api/lark/approval/approvals/{code} — 说明：审批定义。
section "GET /api/lark/approval/approvals/{code} — 审批定义（替换 APPROVAL_CODE）"
APPROVAL_CODE="${APPROVAL_CODE:-approval_code}"
curl_json "${BASE}/api/lark/approval/approvals/${APPROVAL_CODE}?appKey=${APP_KEY}"

# GET /api/lark/approval/instances/{id} — 说明：审批实例详情。
section "GET /api/lark/approval/instances/{id} — 审批实例"
INSTANCE_ID="${INSTANCE_ID:-instance_id}"
curl_json "${BASE}/api/lark/approval/instances/${INSTANCE_ID}?userIdType=user_id&appKey=${APP_KEY}"

# POST /api/lark/approval/instances — 说明：创建审批实例；body 为飞书 InstanceCreate。
section "POST /api/lark/approval/instances — 创建审批实例（body 需合法 InstanceCreate）"
curl_json -X POST "${BASE}/api/lark/approval/instances" "${JSON_HDR[@]}" \
  -d "{\"appKey\":\"${APP_KEY}\",\"body\":{\"approval_code\":\"${APPROVAL_CODE}\"}}"

# --- 日历 ---
# GET /api/lark/calendar/events/{calendarId}/{eventId} — 说明：查询日历事件。
section "GET /api/lark/calendar/events/{cal}/{evt} — 查询事件"
CALENDAR_ID="${CALENDAR_ID:-cal_placeholder}"
EVENT_ID="${EVENT_ID:-evt_placeholder}"
curl_json "${BASE}/api/lark/calendar/events/${CALENDAR_ID}/${EVENT_ID}?appKey=${APP_KEY}"

# POST /api/lark/calendar/events — 说明：创建事件；body 为 CalendarEvent。
section "POST /api/lark/calendar/events — 创建事件（body 需合法 CalendarEvent）"
curl_json -X POST "${BASE}/api/lark/calendar/events" "${JSON_HDR[@]}" \
  -d "{\"appKey\":\"${APP_KEY}\",\"calendarId\":\"${CALENDAR_ID}\",\"body\":{\"summary\":\"evt\"}}"

# --- Webhook（非 ApiResponse，由 SDK 写原生 HTTP）---
section "POST /api/lark/webhook — 事件订阅回调（需在开放平台配置 URL；非 JSON 包装）"
echo "curl -sS -X POST \"${BASE}/api/lark/webhook\" -H 'Content-Type: application/json' -d '{\"challenge\":\"test\"}'"
echo "多应用：${BASE}/api/lark/webhook/{appKey}"
echo ""

# --- Actuator（若已暴露）---
section "GET /actuator/health — Spring Boot 健康（若启用）"
curl_json "${BASE}/actuator/health" || true

echo ""
echo "完成。占位 path/ID 会返回飞书或校验错误，替换为真实值后再测。"
