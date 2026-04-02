# Lark Spring Boot Starter

该模块提供一组“对接飞书常用能力”的 Spring Boot 自动配置与 REST 代理接口

本文面向“企业应用对接飞书”的 **登录（OAuth）** 与 **消息推送（IM 发消息）** 两类高频场景，列出常用接口与可复制的 `curl` 示例。

---

## 快速理解两类 Token（非常重要）

- **tenant_access_token**：应用身份（租户级），用于调用通讯录、发消息等“应用权限”接口；**不等于用户登录态**。
- **user_access_token**：用户身份（OAuth 登录后换取），用于获取用户信息、以用户身份调用某些接口。

---

## 登录（OAuth）常用接口

### 1）授权页（前端跳转/扫码）

飞书会在用户授权后回调你配置的 redirect_uri，并带上 `code`。

- 授权入口文档：[获取授权码](https://open.feishu.cn/document/authentication-management/access-token/obtain-oauth-code)

> 本 starter 当前提供的是“后端换票”接口；授权页 URL 一般由前端拼接并跳转。

授权页 URL 示例（浏览器直接打开/前端重定向）：

```bash
python3 - <<'PY'
import urllib.parse

client_id = "cli_a94a1fa790f91ccc"
redirect_uri = "https://report-test.sensetime.com/api/sys/cas/lark/callback"
scope = "contact:contact.base:readonly"
state = "RANDOMSTRING"

base = "https://accounts.feishu.cn/open-apis/authen/v1/authorize"
q = {
  "client_id": client_id,
  "response_type": "code",
  "redirect_uri": redirect_uri,
  "scope": scope,
  "prompt": "consent",
  "state": state,
}
print(base + "?" + urllib.parse.urlencode(q, quote_via=urllib.parse.quote))
PY
```

### 2）后端用 code 换 user_access_token

```bash
curl -sS -X POST 'http://127.0.0.1:8080/api/lark/auth/access-token' \
  -H 'Content-Type: application/json' \
  -d '{"appKey":"default","code":"YOUR_CODE"}'
```

### 3）刷新 user_access_token

```bash
curl -sS -X POST 'http://127.0.0.1:8080/api/lark/auth/refresh-access-token' \
  -H 'Content-Type: application/json' \
  -d '{"appKey":"default","refreshToken":"YOUR_REFRESH_TOKEN"}'
```

### 4）用 user_access_token 获取用户信息

```bash
curl -sS -X POST 'http://127.0.0.1:8080/api/lark/identity/user-info' \
  -H 'Content-Type: application/json' \
  -d '{"appKey":"default","userAccessToken":"YOUR_USER_ACCESS_TOKEN"}'
```

---

## 应用级 Token（tenant_access_token）

该接口用于“应用能力”（如发消息、通讯录查询等）。返回结构与飞书官方文档一致：

- 官方文档：[`tenant_access_token/internal`](https://open.feishu.cn/document/server-docs/authentication-management/access-token/tenant_access_token_internal)

```bash
curl -sS -X POST 'http://127.0.0.1:8080/api/lark/auth/tenant-access-token/internal' \
  -H 'Content-Type: application/json' \
  -d '{"appKey":"default"}'
```

---

## 通讯录：手机号/邮箱批量换 user_id（常用于“找到接收人”）

```bash
curl -sS -X POST 'http://127.0.0.1:8080/api/lark/contact/users/batch-get-id' \
  -H 'Content-Type: application/json' \
  -d '{
    "appKey":"default",
    "userIdType":"user_id",
    "mobiles":["15900703982","13122355479"],
    "emails":["someone@example.com"]
  }'
```

---

## 推送消息（IM 发消息）常用接口

### 1）发文本消息

`receiveIdType` 与 `receiveId` 必须匹配：

- 发给个人：`OPEN_ID` / `USER_ID` / `EMAIL` 等
- 发到群：`CHAT_ID`

```bash
curl -sS -X POST 'http://127.0.0.1:8080/api/lark/im/send-text' \
  -H 'Content-Type: application/json' \
  -d '{"appKey":"default","receiveIdType":"USER_ID","receiveId":"3f64af1d","text":"hello"}'
```

### 2）发交互式卡片（自己拼 card JSON）

`cardJson` 是“卡片对象 JSON”的字符串（需要转义或用脚本生成）。

```bash
curl -sS -X POST 'http://127.0.0.1:8080/api/lark/im/send-card' \
  -H 'Content-Type: application/json' \
  -d '{"appKey":"default","receiveIdType":"USER_ID","receiveId":"3f64af1d","cardJson":"{\"config\":{\"wide_screen_mode\":true},\"header\":{\"template\":\"blue\",\"title\":{\"tag\":\"plain_text\",\"content\":\"标题\"}},\"elements\":[{\"tag\":\"div\",\"text\":{\"tag\":\"lark_md\",\"content\":\"内容\"}}]}"}'
```

### 3）发“模板卡片”（只传 templateId + 变量）

如果你在飞书开放平台卡片搭建工具里发布了卡片模板（拿到 `template_id`），可用该接口只传变量。

```bash
curl -sS -X POST 'http://127.0.0.1:8080/api/lark/im/send-card-template' \
  -H 'Content-Type: application/json' \
  -d '{
    "appKey":"default",
    "receiveIdType":"USER_ID",
    "receiveId":"3f64af1d",
    "templateId":"AAqKFp7T1oLSK",
    "templateVariable":{
      "open_id":"ou_xxx",
      "complete_time":"2026-04-01 12:34:56",
      "alarm_time":"2026-04-01 12:00:00",
      "notes":"示例备注"
    }
  }'
```

### 4）更新消息内容（按 messageId）

```bash
curl -sS -X POST 'http://127.0.0.1:8080/api/lark/im/update-message' \
  -H 'Content-Type: application/json' \
  -d '{"appKey":"default","messageId":"om_xxx","contentJson":"{\"text\":\"更新后的文本内容\"}"}'
```

---

## 相关脚本

项目根目录 `scripts/lark-sdk-test.sh` 内包含更完整的 `curl` 示例集合（含 IM、通讯录、审批、日历等）。

---

## 工作台打开 Web / 小程序：需要调用哪些接口

### 1）工作台打开 Web（H5/PC）

飞书工作台只负责“把用户带到你的 URL”，用户身份获取通常仍走 OAuth。

- **飞书侧配置**：工作台入口指向你的 `https://...` 页面（域名白名单/HTTPS 按开放平台要求配置）。
- **你侧常用链路**：
  - 页面拿到 OAuth 回调参数 `code`（由飞书跳转回你的 `redirect_uri` 携带）
  - 后端换票拿 `user_access_token`
  - 再用 `user_access_token` 拉用户信息，建立你自己系统的 session/JWT

后端接口（本 starter 已提供）：

- `POST /api/lark/auth/access-token`：code 换 `user_access_token`（见上文「登录（OAuth）常用接口」）
- `POST /api/lark/identity/user-info`：用 `user_access_token` 拉用户信息（见上文「登录（OAuth）常用接口」）

### 2）工作台打开小程序

工作台打开小程序时，“入口与跳转”由飞书侧配置，小程序端再把用户态凭证/临时 code 交给你后端换票（整体与 Web 类似）。

- **飞书侧配置**：应用开通小程序能力，并在工作台配置入口为小程序。
- **你侧常用链路**：
  - 小程序端获取到用于换票的 `code`（来自飞书小程序/JS-SDK 的授权流程）
  - 后端同样用 `POST /api/lark/auth/access-token` 换取 `user_access_token`
  - 再用 `POST /api/lark/identity/user-info` 获取用户信息并建立你自己的登录态

> 说明：本 starter 目前提供的是“后端换票 + 拉用户信息”的通用接口；小程序端“怎么拿到 code”
> 取决于你在飞书开放平台的小程序/JS-SDK 接入方式。

