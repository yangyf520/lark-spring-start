#!/usr/bin/env bash
# =============================================================================
# 可复制的 curl 单条示例（默认 http://127.0.0.1:8080）。# 行为说明，可整段粘贴到终端。
# 说明：成功通常返回飞书 SDK Resp/原生 JSON；失败返回 SystemException（带 HTTP 状态码）。
#
# 说明：
# - 「本服务参数」指当前 Spring 接口的 JSON/query；「body 嵌套」结构需符合飞书 oapi-sdk 模型（与开放平台字段一致）。
# - 占位符 ou_xxx、oc_xxx、default、审批/日历 ID 等请换成真实值；不需要的 query 键可整段删除。
# - 飞书侧若报「字段非法」，多为空字符串不被接受，删掉对应键即可。
# - JSON Content-Type：通常 `-H 'Content-Type: application/json'` 即可；若遇 415，用 `curl -v` 看实际发出的 Content-Type。
#
# 飞书 OpenAPI 映射：下列每条 curl 上方若有「# 飞书：」行，即为 oapi-sdk 实际调用的开放平台 HTTP
#（路径前缀一般为 /open-apis；域名多为 https://open.feishu.cn ，国际版 https://open.larksuite.com ，以应用与文档为准）。
# 「（无）」表示仅本服务逻辑，不发起飞书 HTTP。
# =============================================================================

# =============================================================================
# 应用入口
# =============================================================================

BASE_URL="${BASE_URL:-http://127.0.0.1:8080}"

# --- GET / ---
# 飞书：（无）本地 Spring，非飞书 API。
# 功能：访问根路径，查看是否 302 重定向到 Swagger UI（不走 ApiResponse）。
# 参数：无。
curl -sSI "${BASE_URL}/" | head -8

# =============================================================================
# 运维与可观测
# =============================================================================

# --- GET /actuator/health ---
# 飞书：（无）Spring Actuator。
# 功能：Spring Boot Actuator 健康检查（未引入或未暴露时请求会失败，已用 || true 忽略退出码）。
# 参数：无。
curl -sS "${BASE_URL}/actuator/health" || true

# =============================================================================
# OAPI 客户端（多应用注册与探测）
# =============================================================================

# --- GET /api/lark/oapi/apps ---
# 飞书：（无）本服务列出已注册 Client，不调用飞书。
# 功能：列出当前进程内已注册的飞书应用 appKey 与 primary 应用键。
# 参数：无。
curl -sS "${BASE_URL}/api/lark/oapi/apps"

# --- GET /api/lark/oapi/check-app ---
# 飞书：（无）本服务自检配置，不调用飞书。
# 功能：校验给定 appKey 能否解析为可用的飞书 OpenAPI Client（配置是否存在）。
# 参数（query）：appKey — 多应用时的键，如 default。
curl -sS "${BASE_URL}/api/lark/oapi/check-app?appKey=default"

# =============================================================================
# 鉴权（租户令牌与用户 OAuth）
# =============================================================================

# --- POST /api/lark/auth/tenant-access-token/internal ---
# 飞书：POST /open-apis/auth/v3/tenant_access_token/internal
# 功能：用应用 app_id/app_secret 向飞书换取 tenant_access_token（服务端代理，返回 SDK 结构）。
# 参数（JSON）：appKey — 可选；省略或 null 则用 primary 应用。
curl -sS -X POST "${BASE_URL}/api/lark/auth/tenant-access-token/internal" \
  -H 'Content-Type: application/json' \
  -d '{"appKey":"default"}'

# 亦可换 TAT 且不指定应用：'{}'

# --- POST /api/lark/auth/access-token ---
# 飞书：POST /open-apis/authen/v1/access_token
# 功能：OAuth 授权码换用户 user_access_token（需将 code 换成真实授权码）。
# 参数（JSON）：appKey 可选；code 必填；grantType 可选，默认 authorization_code。
curl -sS -X POST "${BASE_URL}/api/lark/auth/access-token" \
  -H 'Content-Type: application/json' \
  -d '{"appKey":"default","code":"oauth_code","grantType":"authorization_code"}'

# --- POST /api/lark/auth/refresh-access-token ---
# 飞书：POST /open-apis/authen/v1/refresh_access_token
# 功能：用 refresh_token 刷新用户 access_token。
# 参数（JSON）：appKey 可选；refreshToken 必填；grantType 可选，默认 refresh_token。
curl -sS -X POST "${BASE_URL}/api/lark/auth/refresh-access-token" \
  -H 'Content-Type: application/json' \
  -d '{"appKey":"default","refreshToken":"refresh_token","grantType":"refresh_token"}'

# --- GET /api/lark/auth/authorize ---
# 飞书：内部会调用 POST /open-apis/authen/v1/access_token（本接口本身是 OAuth redirect 回调入口）
# 功能：浏览器 OAuth 回调入口；从 query 中取 code 换 user_access_token，然后交由后端自定义的 LarkOAuthService 处理。
# 参数（query）：code 必填；state 可选；appKey 可选（多应用时建议传）。
curl -sS "${BASE_URL}/api/lark/auth/authorize?appKey=default&code=oauth_code&state=optional_state"

# =============================================================================
# 机器人
# =============================================================================

# --- GET /api/lark/bot/info ---
# 飞书：GET /open-apis/bot/v3/info
# 功能：调用飞书 bot/v3/info，查询当前应用机器人资料。
# 参数（query）：appKey — 可选，多应用时指定。
curl -sS "${BASE_URL}/api/lark/bot/info?appKey=default"

# =============================================================================
# 智能表格（Bitable）
# =============================================================================

# --- POST /api/lark/bitable/apps/{appToken}/tables/{tableId}/records/search ---
# 飞书：POST /open-apis/bitable/v1/apps/{app_token}/tables/{table_id}/records/search
# 功能：查询智能表格记录（支持按条件筛选、排序、字段裁剪与分页）。
# 参数（path）：appToken、tableId。
# 参数（query）：appKey、userIdType、pageToken、pageSize（均可选）。
# 参数（JSON）：view_id、field_names、sort、filter、automatic_fields。
curl -sS -X POST "${BASE_URL}/api/lark/bitable/apps/bascnxxxxxxxxxxxx/tables/tblxxxxxxxxxxxx/records/search?appKey=default&pageSize=20" \
  -H 'Content-Type: application/json' \
  -d '{}'

# =============================================================================
# 通讯录
# =============================================================================

# --- GET /api/lark/contact/users/{userId} ---
# 飞书：GET /open-apis/contact/v3/users/{user_id}
# 功能：按用户 ID 查询通讯录用户详情。
# 参数（path）：userId — 与 userIdType 一致，如 user_id 填 3f64af1d。
# 参数（query）：appKey 可选；userIdType 默认 user_id（可选 user_id|open_id|union_id）；departmentIdType 默认 open_department_id。
curl -sS "${BASE_URL}/api/lark/contact/users/3f64af1d?userIdType=user_id&departmentIdType=open_department_id&appKey=default"

# --- GET /api/lark/contact/users ---
# 飞书：GET /open-apis/contact/v3/users
# 功能：分页列出部门下用户。
# 参数（query）：appKey；departmentId 默认 0；pageSize 默认 20；pageToken 翻页；userIdType 默认 user_id；departmentIdType 默认 open_department_id。
curl -sS "${BASE_URL}/api/lark/contact/users?appKey=default&departmentId=0&pageSize=20&userIdType=user_id&departmentIdType=open_department_id"

# --- POST /api/lark/contact/users ---
# 飞书：POST /open-apis/contact/v3/users
# 功能：创建用户。
# 参数（query）：appKey；userIdType 默认 user_id；departmentIdType 默认 open_department_id；clientToken 可选（幂等）。
# 参数（JSON）：飞书创建用户必填含 department_ids（主部门 open_department_id 列表）、employee_type（如 1=正式员工）；
#   与 departmentIdType 一致；starter 已支持 snake_case / camelCase。
curl -sS -X POST "${BASE_URL}/api/lark/contact/users?appKey=default&userIdType=user_id&departmentIdType=open_department_id" \
  -H 'Content-Type: application/json' \
  -d '{"name":"测试用户A","mobile":"13800000000","department_ids":["od-xxxxxxxx"],"employee_type":1}'

# --- PUT /api/lark/contact/users/{userId} ---
# 飞书：PUT /open-apis/contact/v3/users/{user_id}
# 功能：更新用户信息。
# 参数（path）：userId（与 userIdType 一致）。
# 参数（query）：appKey；userIdType 默认 user_id；departmentIdType 默认 open_department_id。
# 参数（JSON）：飞书 User 对象，按需传更新字段。
curl -sS -X PUT "${BASE_URL}/api/lark/contact/users/3f64af1d?appKey=default&userIdType=user_id&departmentIdType=open_department_id" \
  -H 'Content-Type: application/json' \
  -d '{"name":"测试用户A-已更新"}'

# --- DELETE /api/lark/contact/users/{userId} ---
# 飞书：DELETE /open-apis/contact/v3/users/{user_id}
# 功能：删除用户。
# 参数（path）：userId（与 userIdType 一致）。
# 参数（query）：appKey；userIdType 默认 user_id。
curl -sS -X DELETE "${BASE_URL}/api/lark/contact/users/3f64af1d?appKey=default&userIdType=user_id"

# --- GET /api/lark/contact/departments ---
# 飞书：GET /open-apis/contact/v3/departments
# 功能：分页列出指定父部门下子部门。
# 参数（query）：appKey；parentDepartmentId 默认 0；fetchChild 默认 false；pageSize 默认 20；pageToken 翻页；
#   userIdType 默认 user_id；departmentIdType 默认 open_department_id。
curl -sS "${BASE_URL}/api/lark/contact/departments?appKey=default&parentDepartmentId=0&fetchChild=false&pageSize=20&userIdType=user_id&departmentIdType=open_department_id"

# --- GET /api/lark/contact/departments/{departmentId} ---
# 飞书：GET /open-apis/contact/v3/departments/{department_id}
# 功能：按部门 ID 查询部门详情。
# 参数（path）：departmentId（与 departmentIdType 一致）。
# 参数（query）：appKey；userIdType 默认 user_id；departmentIdType 默认 open_department_id。
curl -sS "${BASE_URL}/api/lark/contact/departments/od-xxxxxxxx?appKey=default&userIdType=user_id&departmentIdType=open_department_id"

# --- POST /api/lark/contact/departments ---
# 飞书：POST /open-apis/contact/v3/departments
# 功能：创建部门。
# 参数（query）：appKey；userIdType 默认 user_id；departmentIdType 默认 open_department_id；clientToken 可选（幂等）。
# 参数（JSON）：与飞书文档一致可用 snake_case（如 parent_department_id）；starter 已注册 Jackson mixin，亦支持 camelCase。
curl -sS -X POST "${BASE_URL}/api/lark/contact/departments?appKey=default&userIdType=user_id&departmentIdType=open_department_id" \
  -H 'Content-Type: application/json' \
  -d '{"name":"测试部门A","parent_department_id":"0"}'

# --- PUT /api/lark/contact/departments/{departmentId} ---
# 飞书：PUT /open-apis/contact/v3/departments/{department_id}
# 功能：更新部门信息。
# 参数（path）：departmentId（与 departmentIdType 一致）。
# 参数（query）：appKey；userIdType 默认 user_id；departmentIdType 默认 open_department_id。
# 参数（JSON）：飞书 Department 对象，按需传更新字段。
curl -sS -X PUT "${BASE_URL}/api/lark/contact/departments/od-xxxxxxxx?appKey=default&userIdType=user_id&departmentIdType=open_department_id" \
  -H 'Content-Type: application/json' \
  -d '{"name":"测试部门A-已更新"}'

# --- DELETE /api/lark/contact/departments/{departmentId} ---
# 飞书：DELETE /open-apis/contact/v3/departments/{department_id}
# 功能：删除部门。
# 参数（path）：departmentId（与 departmentIdType 一致）。
# 参数（query）：appKey；departmentIdType 默认 open_department_id。
curl -sS -X DELETE "${BASE_URL}/api/lark/contact/departments/od-xxxxxxxx?appKey=default&departmentIdType=open_department_id"

# --- POST /api/lark/contact/users/batch-get-id ---
# 飞书：POST /open-apis/contact/v3/users/batch_get_id
# 功能：通过邮箱/手机号批量解析用户 ID（返回类型由 userIdType 决定，如 user_id）。
# 参数（JSON）：appKey 可选；userIdType — 目标 ID 类型；emails、mobiles — 字符串数组；includeResigned — 是否含离职，默认 false。
curl -sS -X POST "${BASE_URL}/api/lark/contact/users/batch-get-id" \
  -H 'Content-Type: application/json' \
  -d '{"appKey":"default","userIdType":"user_id","emails":["user@company.com"],"mobiles":["8613800138000"],"includeResigned":false}'

# =============================================================================
# 即时通讯（群会话、运维告警、发消息）
# =============================================================================

# --- GET /api/lark/chat/{chatId} ---
# 飞书：GET /open-apis/im/v1/chats/{chat_id}
# 功能：按 chat_id 查询群会话元信息。
# 参数（path）：chatId — 如 oc_xxx。
# 参数（query）：appKey 可选。
curl -sS "${BASE_URL}/api/lark/chat/oc_xxx?appKey=default"

# --- GET /api/lark/chat/{chatId}/members ---
# 飞书：GET /open-apis/im/v1/chats/{chat_id}/members
# 功能：分页获取群成员列表。
# 参数（path）：chatId；（query）：appKey；memberIdType（默认 open_id）；pageSize；pageToken。
curl -sS "${BASE_URL}/api/lark/chat/oc_xxx/members?appKey=default&memberIdType=open_id&pageSize=20"

# --- POST /api/lark/chat/{chatId}/members ---
# 飞书：POST /open-apis/im/v1/chats/{chat_id}/members
# 功能：拉人入群。body 为 CreateChatMembersReqBody：{"id_list":[...]}。
# 参数（path）：chatId；（query）：appKey；memberIdType（open_id/user_id/union_id/app_id）；succeedType（可选）。
curl -sS -X POST "${BASE_URL}/api/lark/chat/oc_xxx/members?appKey=default&memberIdType=user_id&succeedType=0" \
  -H 'Content-Type: application/json' \
  -d '{"id_list":["3f64af1d"]}'

# --- POST /api/lark/chat/{chatId}/members/remove ---
# 飞书：DELETE /open-apis/im/v1/chats/{chat_id}/members
# 功能：移出群成员。body 为 DeleteChatMembersReqBody（见开放平台/SDK）。
# 参数（path）：chatId；（query）：appKey；memberIdType（open_id/user_id/union_id/app_id）。
curl -sS -X POST "${BASE_URL}/api/lark/chat/oc_xxx/members/remove?appKey=default&memberIdType=user_id" \
  -H 'Content-Type: application/json' \
  -d '{"id_list":["3f64af1d"]}'

# --- GET /api/lark/chat/{chatId}/members/is-in-chat ---
# 飞书：GET /open-apis/im/v1/chats/{chat_id}/members/is_in_chat
# 功能：判断当前 token 对应的用户/机器人是否在群里（根据 access_token 类型自动判断）。
curl -sS "${BASE_URL}/api/lark/chat/oc_xxx/members/is-in-chat?appKey=default"

# --- POST /api/lark/chat ---
# 飞书：POST /open-apis/im/v1/chats
# 功能：创建群聊。嵌套 body 对应飞书 CreateChatReqBody（创建时 userIdList 等为 user_id，与本服务 ChatService 一致）。
# 参数（JSON）：appKey 可选。
# body 常用字段：name 必填；description；avatar；ownerId；userIdList[]；botIdList[]；groupMessageType；chatMode；chatType；
#   joinMessageVisibility；leaveMessageVisibility；membershipApproval；urgentSetting；videoConferenceSetting；editPermission；hideMemberCountSetting；
#   i18nNames、restrictedModeSetting 等见开放平台「创建群」文档。
curl -sS -X POST "${BASE_URL}/api/lark/chat" \
  -H 'Content-Type: application/json' \
  -d '{"appKey":"default","body":{"name":"运维 on-call 群","description":"监控自动建群","userIdList":["3f64af1d","3f64af1e"],"botIdList":["cli_xxx_bot_id"],"ownerId":"3f64af1d"}}'

# --- POST /api/lark/ops/alert（已有群，只发消息）---
# 飞书：POST /open-apis/im/v1/messages（receive_id_type=chat_id，向群发文本）
# 功能：向已有群发送一条运维告警文本。
# 参数（JSON，OpsAlertRequest）：appKey 可选；chatId + alertText 必填。勿传 chatId:null（会误走「建群」分支）。
# 可选字段（建群才用）：chatName、memberOpenIds、botOpenIds、description — 本场景可整段省略。
curl -sS -X POST "${BASE_URL}/api/lark/ops/alert" \
  -H 'Content-Type: application/json' \
  -d '{"appKey":"default","chatId":"oc_xxx","alertText":"【P1】服务异常，请关注\n时间：2030-01-01 12:00:00\n链接：https://grafana.example/d/xxx"}'

# --- POST /api/lark/ops/alert（新建群 + 首条消息）---
# 飞书：先 POST /open-apis/im/v1/chats，再 POST /open-apis/im/v1/messages（本服务编排）
# 功能：新建群并发送首条消息。chatId 必须省略或空字符串。
# 参数（JSON）：appKey；chatName；memberOpenIds（至少一个 user_id）；botOpenIds（建议含机器人 user_id）；description；alertText。
curl -sS -X POST "${BASE_URL}/api/lark/ops/alert" \
  -H 'Content-Type: application/json' \
  -d '{"appKey":"default","chatName":"【告警】支付链路 2030-01-01","memberOpenIds":["3f64af1d","3f64af1e"],"botOpenIds":["cli_xxx_bot_id"],"description":"Prometheus/Alertmanager 自动拉群","alertText":"【初始化】告警群已建立，后续消息将发在本群。"}'

# --- POST /api/lark/im/send-text ---
# 飞书：POST /open-apis/im/v1/messages
# 功能：以应用身份发送文本消息。
# 参数（JSON）：appKey 可选；receiveIdType — 枚举名：OPEN_ID | USER_ID | UNION_ID | EMAIL | CHAT_ID（与 receiveId 一致）；receiveId；text。
curl -sS -X POST "${BASE_URL}/api/lark/im/send-text" \
  -H 'Content-Type: application/json' \
  -d '{"appKey":"default","receiveIdType":"CHAT_ID","receiveId":"oc_xxx","text":"多行可用\\n换行\\n第三行"}'

# --- POST /api/lark/im/send-card ---
# 飞书：POST /open-apis/im/v1/messages（msg_type=interactive）
# 功能：以应用身份发送交互式卡片。cardJson 为「卡片对象」整段 JSON 的字符串（下面已双层转义，可整段复制）。
# 参数（JSON）：appKey；receiveIdType；receiveId；cardJson。
curl -sS -X POST "${BASE_URL}/api/lark/im/send-card" \
  -H 'Content-Type: application/json' \
  -d '{"appKey":"default","receiveIdType":"CHAT_ID","receiveId":"oc_xxx","cardJson":"{\"config\":{\"wide_screen_mode\":true},\"header\":{\"template\":\"red\",\"title\":{\"tag\":\"plain_text\",\"content\":\"【告警】示例卡片\"}},\"elements\":[{\"tag\":\"div\",\"text\":{\"tag\":\"lark_md\",\"content\":\"**级别**：P1\\n**摘要**：接口超时\"}}]}"}'

# --- POST /api/lark/im/send-card（发给指定 user_id，卡片正文含 UTC 时间，每次执行不同）---
# 飞书：POST /open-apis/im/v1/messages（msg_type=interactive）
# 功能：与上相同；receiveId 为通讯录 user_id（示例 3f64af1d，请按实际替换）。cardJson 由脚本生成，避免手写转义。
# 依赖：本机有 python3。用 heredoc 而非 python3 -c，避免 bash 把 Python 里的反引号当成命令替换。无 python3 时可改用上一段固定 cardJson 的 curl。
curl -sS -X POST "${BASE_URL}/api/lark/im/send-card" \
  -H 'Content-Type: application/json' \
  -d "$(python3 <<'PY'
import json, datetime
uid = '3f64af1d'
now = datetime.datetime.now(datetime.timezone.utc).strftime('%Y-%m-%d %H:%M:%S UTC')
card = {
    'config': {'wide_screen_mode': True},
    'header': {'template': 'blue', 'title': {'tag': 'plain_text', 'content': '动态卡片'}},
    'elements': [{
        'tag': 'div',
        'text': {
            'tag': 'lark_md',
            'content': '**生成时间（UTC）**：' + now + '\\n**接收 user_id**：' + uid + '\\n**说明**：每次执行本命令时间会更新；可把业务变量拼进 content。'
        }
    }]
}
body = {'appKey': 'default', 'receiveIdType': 'USER_ID', 'receiveId': uid, 'cardJson': json.dumps(card, ensure_ascii=False)}
print(json.dumps(body, ensure_ascii=False))
PY
)"

# --- POST /api/lark/im/send-card-template ---
# 飞书：POST /open-apis/im/v1/messages（content 内 type=template，与「消息卡片」一致）
# 功能：发送飞书「消息卡片模板」（开放平台卡片搭建工具发布后的 template_id），无需手写整段 card JSON。
# 参数（JSON）：appKey；receiveIdType；receiveId；templateId；templateVariable — 与模板内变量名一致（示例：user_id、complete_time、alarm_time、notes），无变量可传 {} 或省略。
curl -sS -X POST "${BASE_URL}/api/lark/im/send-card-template" \
  -H 'Content-Type: application/json' \
  -d '{"appKey":"default","receiveIdType":"USER_ID","receiveId":"gac5acc8","templateId":"AAqKFp7T1oLSK","templateVariable":{"user_id":"3f64af1d","complete_time":"2026-04-01 12:34:56","alarm_time":"2026-04-01 12:00:00","notes":"示例备注"}}'

# --- POST /api/lark/im/update-message ---
# 飞书：PUT /open-apis/im/v1/messages/{message_id}
# 功能：按 message_id 更新已发送消息（类型需支持更新，如文本）。
# 参数（JSON）：appKey 可选；messageId — 如 om_xxx；contentJson — 文本消息示例：{"text":"新内容"}。
curl -sS -X POST "${BASE_URL}/api/lark/im/update-message" \
  -H 'Content-Type: application/json' \
  -d '{"appKey":"default","messageId":"om_xxx","contentJson":"{\"text\":\"更新后的文本内容\"}"}'

# --- GET /api/lark/im/messages/{messageId} ---
# 飞书：GET /open-apis/im/v1/messages/{message_id}
# 功能：获取单条消息。
# 参数（path）：messageId；（query）：appKey；userIdType（可选，如 open_id/user_id）。
curl -sS "${BASE_URL}/api/lark/im/messages/om_xxx?appKey=default&userIdType=user_id"

# --- GET /api/lark/im/messages ---
# 飞书：GET /open-apis/im/v1/messages（container_id_type/container_id...）
# 功能：分页拉取会话历史消息（容器一般为 chat）。
# 参数（query）：appKey；containerIdType=chat；containerId=oc_xxx；startTime/endTime（秒级时间戳，可选）；sortType；pageSize；pageToken。
curl -sS "${BASE_URL}/api/lark/im/messages?appKey=default&containerIdType=chat&containerId=oc_xxx&pageSize=20"

# --- DELETE /api/lark/im/messages/{messageId} ---
# 飞书：DELETE /open-apis/im/v1/messages/{message_id}
# 功能：撤回消息（机器人撤回自己发送的，或群主/管理员撤回群内消息，受时效/权限限制）。
curl -sS -X DELETE "${BASE_URL}/api/lark/im/messages/om_xxx?appKey=default"

# --- POST /api/lark/im/reply-message ---
# 飞书：POST /open-apis/im/v1/messages/{message_id}/reply
# 功能：回复某条消息。body 需符合 SDK ReplyMessageReq（包含 message_id + body）。
# 说明：此处传入的是 SDK JSON；msg_type/content 写法与「发消息」一致。
curl -sS -X POST "${BASE_URL}/api/lark/im/reply-message?appKey=default" \
  -H 'Content-Type: application/json' \
  -d '{"message_id":"om_xxx","body":{"msg_type":"text","content":"{\"text\":\"reply from api\"}"}}'

# =============================================================================
# 身份（用户态）
# =============================================================================

# --- POST /api/lark/identity/user-info ---
# 飞书：GET /open-apis/authen/v1/user_info（请求头携带 user_access_token；SDK 封装）
# 功能：用用户 user_access_token 拉取当前用户资料。
# 参数（JSON）：userAccessToken 必填；appKey 可选。
curl -sS -X POST "${BASE_URL}/api/lark/identity/user-info" \
  -H 'Content-Type: application/json' \
  -d '{"appKey":"default","userAccessToken":"uat_用户授权后换取的access_token"}'

# =============================================================================
# 审批
# =============================================================================

# --- GET /api/lark/approval/approvals/{approvalCode} ---
# 飞书：GET /open-apis/approval/v4/approvals/{approval_code}
# 功能：按审批定义 code 查询审批模板/定义信息。
# 参数（path）：approvalCode。
# 参数（query）：appKey 可选。
curl -sS "${BASE_URL}/api/lark/approval/approvals/APPROVAL_CODE_PLACEHOLDER?appKey=default"

# --- GET /api/lark/approval/instances/{instanceId} ---
# 飞书：GET /open-apis/approval/v4/instances/{instance_id}
# 功能：按实例 ID 查询审批单详情。
# 参数（path）：instanceId。
# 参数（query）：appKey；userId 可选（部分场景必填）；userIdType 默认 user_id。
curl -sS "${BASE_URL}/api/lark/approval/instances/INSTANCE_ID_PLACEHOLDER?appKey=default&userId=USER_ID_PLACEHOLDER&userIdType=user_id"

# --- POST /api/lark/approval/instances ---
# 飞书：POST /open-apis/approval/v4/instances
# 功能：创建审批实例。嵌套 body 为飞书 InstanceCreate（字段以审批定义为准）。
# 参数（JSON）：appKey 可选；body — 常见字段：approval_code；user_id；department_id；uuid（幂等客户端键）；
#   form — 控件数组，每项含 id（控件 ID）、type、value 等，与审批模板表单一致。
curl -sS -X POST "${BASE_URL}/api/lark/approval/instances" \
  -H 'Content-Type: application/json' \
  -d '{"appKey":"default","body":{"approval_code":"APPROVAL_CODE_PLACEHOLDER","user_id":"USER_ID","department_id":"DEPT_ID_OPTIONAL","uuid":"client-uuid-optional","form":[{"id":"widget_input_xxx","type":"input","value":"示例填写值"}]}}'

# --- GET /api/lark/approval/instances/list ---
# 飞书：GET /open-apis/approval/v4/instances
# 功能：分页列出审批实例（按审批码 + 时间区间过滤）。
curl -sS "${BASE_URL}/api/lark/approval/instances/list?appKey=default&pageSize=20&approvalCode=APPROVAL_CODE_PLACEHOLDER"

# --- POST /api/lark/approval/instances/cancel ---
# 飞书：POST /open-apis/approval/v4/instances/cancel
# 功能：撤销「审批中」实例。body 与 SDK CancelInstanceReq 一致（含 user_id_type + body.instance_cancel）。
curl -sS -X POST "${BASE_URL}/api/lark/approval/instances/cancel?appKey=default" \
  -H 'Content-Type: application/json' \
  -d '{"user_id_type":"user_id","body":{"approval_code":"APPROVAL_CODE_PLACEHOLDER","instance_code":"INSTANCE_CODE","user_id":"USER_ID","comment":"撤回"}}'

# --- POST /api/lark/approval/tasks/approve ---
# 飞书：POST /open-apis/approval/v4/tasks/approve
# 功能：同意审批任务。body 与 SDK ApproveTaskReq 一致。
curl -sS -X POST "${BASE_URL}/api/lark/approval/tasks/approve?appKey=default" \
  -H 'Content-Type: application/json' \
  -d '{"user_id_type":"user_id","body":{"approval_code":"APPROVAL_CODE_PLACEHOLDER","instance_code":"INSTANCE_CODE","user_id":"USER_ID","comment":"同意","task_id":"TASK_ID"}}'

# --- POST /api/lark/approval/tasks/reject ---
# 飞书：POST /open-apis/approval/v4/tasks/reject
curl -sS -X POST "${BASE_URL}/api/lark/approval/tasks/reject?appKey=default" \
  -H 'Content-Type: application/json' \
  -d '{"user_id_type":"user_id","body":{"approval_code":"APPROVAL_CODE_PLACEHOLDER","instance_code":"INSTANCE_CODE","user_id":"USER_ID","comment":"拒绝","task_id":"TASK_ID"}}'

# =============================================================================
# 日历
# =============================================================================

# --- GET /api/lark/calendar/events/{calendarId}/{eventId} ---
# 飞书：GET /open-apis/calendar/v4/calendars/{calendar_id}/events/{event_id}
# 功能：查询指定日历下某一事件详情。
# 参数（path）：calendarId、eventId。
# 参数（query）：appKey 可选。
curl -sS "${BASE_URL}/api/lark/calendar/events/CALENDAR_ID_PLACEHOLDER/EVENT_ID_PLACEHOLDER?appKey=default"

# --- POST /api/lark/calendar/events ---
# 飞书：POST /open-apis/calendar/v4/calendars/{calendar_id}/events
# 功能：在指定日历下创建日程。嵌套 body 为飞书 CalendarEvent（字段以 v4 文档为准）。
# 参数（JSON）：appKey 可选；calendarId 必填；body — 常见：summary、description、need_notification、
#   start_time/end_time（含 date 或 timestamp、timezone）、visibility、attendee_ability、color、reminders、recurrence 等。
curl -sS -X POST "${BASE_URL}/api/lark/calendar/events" \
  -H 'Content-Type: application/json' \
  -d '{"appKey":"default","calendarId":"CALENDAR_ID_PLACEHOLDER","body":{"summary":"评审会议","description":"飞书日历事件示例","need_notification":true,"start_time":{"date":"2030-06-01","timezone":"Asia/Shanghai"},"end_time":{"date":"2030-06-01","timezone":"Asia/Shanghai"},"visibility":"default"}}'

# =============================================================================
# 事件订阅（Webhook）
# =============================================================================

# --- POST /api/lark/webhook ---
# 飞书：事件订阅 HTTP 回调（飞书 → 本服务 URL；非本服务主动请求 open-apis）
# 功能：事件订阅 URL 回调（如 URL 校验 challenge）；响应为飞书/SDK 原生格式，非统一 ApiResponse。
# 参数（JSON）：平台下发的完整体；校验示例仅含 challenge。加密事件需 encrypt 等字段，见飞书「事件订阅」文档。
curl -sS -X POST "${BASE_URL}/api/lark/webhook" \
  -H 'Content-Type: application/json' \
  -d '{"challenge":"test_challenge_string"}'

# 多应用路径示例：/api/lark/webhook/yourAppKey
