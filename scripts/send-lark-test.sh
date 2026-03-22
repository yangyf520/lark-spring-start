#!/usr/bin/env bash
# =============================================================================
# 可复制的 curl 单条示例（默认 http://127.0.0.1:8080）。# 行为说明，可整段粘贴到终端。
# 除「事件订阅」外，业务 JSON 多为统一包装 { ok, time, data, error, message }。
#
# 说明：
# - 「本服务参数」指当前 Spring 接口的 JSON/query；「body 嵌套」结构需符合飞书 oapi-sdk 模型（与开放平台字段一致）。
# - 占位符 ou_xxx、oc_xxx、default、审批/日历 ID 等请换成真实值；不需要的 query 键可整段删除。
# - 飞书侧若报「字段非法」，多为空字符串不被接受，删掉对应键即可。
# =============================================================================

# =============================================================================
# 应用入口
# =============================================================================

# --- GET / ---
# 功能：访问根路径，查看是否 302 重定向到 Swagger UI（不走 ApiResponse）。
# 参数：无。
curl -sSI 'http://127.0.0.1:8080/' | head -8
echo ""

# =============================================================================
# 运维与可观测
# =============================================================================

# --- GET /api/admin/health ---
# 功能：本机运维健康检查；返回应用状态、时间及 tenant_access_token 是否已缓存（不含明文）。
# 参数：无。
curl -sS 'http://127.0.0.1:8080/api/admin/health' | python3 -m json.tool
echo ""

# --- GET /actuator/health ---
# 功能：Spring Boot Actuator 健康检查（未引入或未暴露时请求会失败，已用 || true 忽略退出码）。
# 参数：无。
curl -sS 'http://127.0.0.1:8080/actuator/health' | python3 -m json.tool || true
echo ""

# =============================================================================
# OAPI 客户端（多应用注册与探测）
# =============================================================================

# --- GET /api/lark/oapi/apps ---
# 功能：列出当前进程内已注册的飞书应用 appKey 与 primary 应用键。
# 参数：无。
curl -sS 'http://127.0.0.1:8080/api/lark/oapi/apps' | python3 -m json.tool
echo ""

# --- GET /api/lark/oapi/tenant-access-token ---
# 功能：探测 primary 应用的租户 token 是否已在 SDK/缓存侧就绪（布尔，不返回 token 内容）。
# 参数：无。
curl -sS 'http://127.0.0.1:8080/api/lark/oapi/tenant-access-token' | python3 -m json.tool
echo ""

# --- GET /api/lark/oapi/check-app ---
# 功能：校验给定 appKey 能否解析为可用的飞书 OpenAPI Client（配置是否存在）。
# 参数（query）：appKey — 多应用时的键，如 default。
curl -sS 'http://127.0.0.1:8080/api/lark/oapi/check-app?appKey=default' | python3 -m json.tool
echo ""

# =============================================================================
# 鉴权（租户令牌与用户 OAuth）
# =============================================================================

# --- POST /api/lark/auth/tenant-access-token/internal ---
# 功能：用应用 app_id/app_secret 向飞书换取 tenant_access_token（服务端代理，返回 SDK 结构）。
# 参数（JSON）：appKey — 可选；省略或 null 则用 primary 应用。
curl -sS -X POST 'http://127.0.0.1:8080/api/lark/auth/tenant-access-token/internal' \
  -H 'Content-Type: application/json; charset=utf-8' \
  -d '{"appKey":"default"}' | python3 -m json.tool
echo ""

# 亦可换 TAT 且不指定应用：'{}'

# --- POST /api/lark/auth/access-token ---
# 功能：OAuth 授权码换用户 user_access_token（需将 code 换成真实授权码）。
# 参数（JSON）：appKey 可选；code 必填；grantType 可选，默认 authorization_code。
curl -sS -X POST 'http://127.0.0.1:8080/api/lark/auth/access-token' \
  -H 'Content-Type: application/json; charset=utf-8' \
  -d '{"appKey":"default","code":"oauth_code","grantType":"authorization_code"}' | python3 -m json.tool
echo ""

# --- POST /api/lark/auth/refresh-access-token ---
# 功能：用 refresh_token 刷新用户 access_token。
# 参数（JSON）：appKey 可选；refreshToken 必填；grantType 可选，默认 refresh_token。
curl -sS -X POST 'http://127.0.0.1:8080/api/lark/auth/refresh-access-token' \
  -H 'Content-Type: application/json; charset=utf-8' \
  -d '{"appKey":"default","refreshToken":"refresh_token","grantType":"refresh_token"}' | python3 -m json.tool
echo ""

# =============================================================================
# 机器人
# =============================================================================

# --- GET /api/lark/bot/info ---
# 功能：调用飞书 bot/v3/info，查询当前应用机器人资料。
# 参数（query）：appKey — 可选，多应用时指定。
curl -sS 'http://127.0.0.1:8080/api/lark/bot/info?appKey=default' | python3 -m json.tool
echo ""

# =============================================================================
# 通讯录
# =============================================================================

# --- GET /api/lark/contact/users/{userId} ---
# 功能：按用户 ID 查询通讯录用户详情。
# 参数（path）：userId — 与 userIdType 一致，如 open_id 填 ou_xxx。
# 参数（query）：appKey 可选；userIdType 默认 open_id（可选 open_id|user_id|union_id）；departmentIdType 默认 open_department_id。
curl -sS 'http://127.0.0.1:8080/api/lark/contact/users/ou_xxx?userIdType=open_id&departmentIdType=open_department_id&appKey=default' | python3 -m json.tool
echo ""

# --- GET /api/lark/contact/departments ---
# 功能：分页列出指定父部门下子部门。
# 参数（query）：appKey；parentDepartmentId 默认 0；fetchChild 默认 false；pageSize 默认 20；pageToken 翻页；
#   userIdType 默认 open_id；departmentIdType 默认 open_department_id。
curl -sS 'http://127.0.0.1:8080/api/lark/contact/departments?appKey=default&parentDepartmentId=0&fetchChild=false&pageSize=20&userIdType=open_id&departmentIdType=open_department_id' | python3 -m json.tool
echo ""

# --- POST /api/lark/contact/users/batch-get-id ---
# 功能：通过邮箱/手机号批量解析用户 ID（返回类型由 userIdType 决定，如 open_id、user_id）。
# 参数（JSON）：appKey 可选；userIdType — 目标 ID 类型；emails、mobiles — 字符串数组；includeResigned — 是否含离职，默认 false。
curl -sS -X POST 'http://127.0.0.1:8080/api/lark/contact/users/batch-get-id' \
  -H 'Content-Type: application/json; charset=utf-8' \
  -d '{"appKey":"default","userIdType":"open_id","emails":["user@company.com"],"mobiles":["8613800138000"],"includeResigned":false}' | python3 -m json.tool
echo ""

# =============================================================================
# 即时通讯（群会话、运维告警、发消息）
# =============================================================================

# --- GET /api/lark/chat/{chatId} ---
# 功能：按 chat_id 查询群会话元信息。
# 参数（path）：chatId — 如 oc_xxx。
# 参数（query）：appKey 可选。
curl -sS 'http://127.0.0.1:8080/api/lark/chat/oc_xxx?appKey=default' | python3 -m json.tool
echo ""

# --- POST /api/lark/chat ---
# 功能：创建群聊。嵌套 body 对应飞书 CreateChatReqBody（创建时 userIdList 等为 open_id，与本服务 ChatService 一致）。
# 参数（JSON）：appKey 可选。
# body 常用字段：name 必填；description；avatar；ownerId；userIdList[]；botIdList[]；groupMessageType；chatMode；chatType；
#   joinMessageVisibility；leaveMessageVisibility；membershipApproval；urgentSetting；videoConferenceSetting；editPermission；hideMemberCountSetting；
#   i18nNames、restrictedModeSetting 等见开放平台「创建群」文档。
curl -sS -X POST 'http://127.0.0.1:8080/api/lark/chat' \
  -H 'Content-Type: application/json; charset=utf-8' \
  -d '{"appKey":"default","body":{"name":"运维 on-call 群","description":"监控自动建群","userIdList":["ou_运维A","ou_运维B"],"botIdList":["ou_本应用机器人open_id"],"ownerId":"ou_群主open_id可选"}}' | python3 -m json.tool
echo ""

# --- POST /api/lark/ops/alert（已有群，只发消息）---
# 功能：向已有群发送一条运维告警文本。
# 参数（JSON，OpsAlertRequest）：appKey 可选；chatId + alertText 必填。勿传 chatId:null（会误走「建群」分支）。
# 可选字段（建群才用）：chatName、memberOpenIds、botOpenIds、description — 本场景可整段省略。
curl -sS -X POST 'http://127.0.0.1:8080/api/lark/ops/alert' \
  -H 'Content-Type: application/json; charset=utf-8' \
  -d '{"appKey":"default","chatId":"oc_xxx","alertText":"【P1】服务异常，请关注\n时间：2030-01-01 12:00:00\n链接：https://grafana.example/d/xxx"}' | python3 -m json.tool
echo ""

# --- POST /api/lark/ops/alert（新建群 + 首条消息）---
# 功能：新建群并发送首条消息。chatId 必须省略或空字符串。
# 参数（JSON）：appKey；chatName；memberOpenIds（至少一个 open_id）；botOpenIds（建议含机器人 open_id）；description；alertText。
curl -sS -X POST 'http://127.0.0.1:8080/api/lark/ops/alert' \
  -H 'Content-Type: application/json; charset=utf-8' \
  -d '{"appKey":"default","chatName":"【告警】支付链路 2030-01-01","memberOpenIds":["ou_运维A","ou_运维B"],"botOpenIds":["ou_本应用机器人open_id"],"description":"Prometheus/Alertmanager 自动拉群","alertText":"【初始化】告警群已建立，后续消息将发在本群。"}' | python3 -m json.tool
echo ""

# --- POST /api/lark/im/send-text ---
# 功能：以应用身份发送文本消息。
# 参数（JSON）：appKey 可选；receiveIdType — 枚举名：OPEN_ID | USER_ID | UNION_ID | EMAIL | CHAT_ID（与 receiveId 一致）；receiveId；text。
curl -sS -X POST 'http://127.0.0.1:8080/api/lark/im/send-text' \
  -H 'Content-Type: application/json; charset=utf-8' \
  -d '{"appKey":"default","receiveIdType":"CHAT_ID","receiveId":"oc_xxx","text":"多行可用\\n换行\\n第三行"}' | python3 -m json.tool
echo ""

# --- POST /api/lark/im/send-card ---
# 功能：以应用身份发送交互式卡片。cardJson 为「卡片对象」整段 JSON 的字符串（下面已双层转义，可整段复制）。
# 参数（JSON）：appKey；receiveIdType；receiveId；cardJson。
curl -sS -X POST 'http://127.0.0.1:8080/api/lark/im/send-card' \
  -H 'Content-Type: application/json; charset=utf-8' \
  -d '{"appKey":"default","receiveIdType":"CHAT_ID","receiveId":"oc_xxx","cardJson":"{\"config\":{\"wide_screen_mode\":true},\"header\":{\"template\":\"red\",\"title\":{\"tag\":\"plain_text\",\"content\":\"【告警】示例卡片\"}},\"elements\":[{\"tag\":\"div\",\"text\":{\"tag\":\"lark_md\",\"content\":\"**级别**：P1\\n**摘要**：接口超时\"}}]}"}' | python3 -m json.tool
echo ""

# --- POST /api/lark/im/update-message ---
# 功能：按 message_id 更新已发送消息（类型需支持更新，如文本）。
# 参数（JSON）：appKey 可选；messageId — 如 om_xxx；contentJson — 文本消息示例：{"text":"新内容"}。
curl -sS -X POST 'http://127.0.0.1:8080/api/lark/im/update-message' \
  -H 'Content-Type: application/json; charset=utf-8' \
  -d '{"appKey":"default","messageId":"om_xxx","contentJson":"{\"text\":\"更新后的文本内容\"}"}' | python3 -m json.tool
echo ""

# =============================================================================
# 身份（用户态）
# =============================================================================

# --- POST /api/lark/identity/user-info ---
# 功能：用用户 user_access_token 拉取当前用户资料。
# 参数（JSON）：userAccessToken 必填；appKey 可选。
curl -sS -X POST 'http://127.0.0.1:8080/api/lark/identity/user-info' \
  -H 'Content-Type: application/json; charset=utf-8' \
  -d '{"appKey":"default","userAccessToken":"uat_用户授权后换取的access_token"}' | python3 -m json.tool
echo ""

# =============================================================================
# 审批
# =============================================================================

# --- GET /api/lark/approval/approvals/{approvalCode} ---
# 功能：按审批定义 code 查询审批模板/定义信息。
# 参数（path）：approvalCode。
# 参数（query）：appKey 可选。
curl -sS 'http://127.0.0.1:8080/api/lark/approval/approvals/APPROVAL_CODE_PLACEHOLDER?appKey=default' | python3 -m json.tool
echo ""

# --- GET /api/lark/approval/instances/{instanceId} ---
# 功能：按实例 ID 查询审批单详情。
# 参数（path）：instanceId。
# 参数（query）：appKey；userId 可选（部分场景必填）；userIdType 默认 user_id。
curl -sS 'http://127.0.0.1:8080/api/lark/approval/instances/INSTANCE_ID_PLACEHOLDER?appKey=default&userId=USER_ID_PLACEHOLDER&userIdType=user_id' | python3 -m json.tool
echo ""

# --- POST /api/lark/approval/instances ---
# 功能：创建审批实例。嵌套 body 为飞书 InstanceCreate（字段以审批定义为准）。
# 参数（JSON）：appKey 可选；body — 常见字段：approval_code；user_id 或 open_id；department_id；uuid（幂等客户端键）；
#   form — 控件数组，每项含 id（控件 ID）、type、value 等，与审批模板表单一致。
curl -sS -X POST 'http://127.0.0.1:8080/api/lark/approval/instances' \
  -H 'Content-Type: application/json; charset=utf-8' \
  -d '{"appKey":"default","body":{"approval_code":"APPROVAL_CODE_PLACEHOLDER","user_id":"USER_ID","department_id":"DEPT_ID_OPTIONAL","uuid":"client-uuid-optional","form":[{"id":"widget_input_xxx","type":"input","value":"示例填写值"}]}}' | python3 -m json.tool
echo ""

# =============================================================================
# 日历
# =============================================================================

# --- GET /api/lark/calendar/events/{calendarId}/{eventId} ---
# 功能：查询指定日历下某一事件详情。
# 参数（path）：calendarId、eventId。
# 参数（query）：appKey 可选。
curl -sS 'http://127.0.0.1:8080/api/lark/calendar/events/CALENDAR_ID_PLACEHOLDER/EVENT_ID_PLACEHOLDER?appKey=default' | python3 -m json.tool
echo ""

# --- POST /api/lark/calendar/events ---
# 功能：在指定日历下创建日程。嵌套 body 为飞书 CalendarEvent（字段以 v4 文档为准）。
# 参数（JSON）：appKey 可选；calendarId 必填；body — 常见：summary、description、need_notification、
#   start_time/end_time（含 date 或 timestamp、timezone）、visibility、attendee_ability、color、reminders、recurrence 等。
curl -sS -X POST 'http://127.0.0.1:8080/api/lark/calendar/events' \
  -H 'Content-Type: application/json; charset=utf-8' \
  -d '{"appKey":"default","calendarId":"CALENDAR_ID_PLACEHOLDER","body":{"summary":"评审会议","description":"飞书日历事件示例","need_notification":true,"start_time":{"date":"2030-06-01","timezone":"Asia/Shanghai"},"end_time":{"date":"2030-06-01","timezone":"Asia/Shanghai"},"visibility":"default"}}' | python3 -m json.tool
echo ""

# =============================================================================
# 事件订阅（Webhook）
# =============================================================================

# --- POST /api/lark/webhook ---
# 功能：事件订阅 URL 回调（如 URL 校验 challenge）；响应为飞书/SDK 原生格式，非统一 ApiResponse。
# 参数（JSON）：平台下发的完整体；校验示例仅含 challenge。加密事件需 encrypt 等字段，见飞书「事件订阅」文档。
curl -sS -X POST 'http://127.0.0.1:8080/api/lark/webhook' \
  -H 'Content-Type: application/json; charset=utf-8' \
  -d '{"challenge":"test_challenge_string"}'
echo ""

# 多应用路径示例：/api/lark/webhook/yourAppKey
