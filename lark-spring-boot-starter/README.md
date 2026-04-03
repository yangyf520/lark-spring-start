# Lark Spring Boot Starter

`lark-spring-boot-starter`：基于 Spring Boot 与飞书 **oapi-sdk**，提供常用 REST 与自动配置。**业务工程从公司内部 Maven 仓库拉取依赖使用**（见下文仓库地址与坐标）。

| 约定 | 说明 |
|------|------|
| 示例里的地址 | `http://127.0.0.1:8080/api/...` 请换成你的服务地址与 **context-path** |

---

## 集成与使用

### Maven 仓库（拉取本 starter）

| | URL |
|---|-----|
| **仓库** | `https://nexus-cit.sensetime.com/repository/sensetime-cit` |

业务工程需能解析依赖，在 **`pom.xml`** 配置（若父 POM / `settings.xml` 已指向同一仓库，可省略）：

```xml
<repositories>
    <repository>
        <id>sensetime-cit</id>
        <url>https://nexus-cit.sensetime.com/repository/sensetime-cit</url>
        <releases><enabled>true</enabled></releases>
        <snapshots><enabled>true</enabled></snapshots>
    </repository>
</repositories>
```

仅使用 **Release** 构件、不依赖 SNAPSHOT 时，可将 `<snapshots><enabled>false</enabled></snapshots>`。


### 环境

| 项 | 要求 |
|----|------|
| JDK | 17+ |
| Spring Boot | 3.x（建议与 starter 构建版本一致） |

### Maven 依赖

宿主 `pom.xml` 引入 starter；并需 **`spring-boot-starter-web`**（提供 Servlet，否则 `/lark/**` 无法对外服务）。

```xml
<dependency>
    <groupId>com.larksuite</groupId>
    <artifactId>lark-spring-boot-starter</artifactId>
    <version>0.1.0</version>
</dependency>
```

Gradle：

```groovy
repositories {
    maven { url 'https://nexus-cit.sensetime.com/repository/sensetime-cit' }
}
implementation 'com.larksuite:lark-spring-boot-starter:0.1.0'
```

### 配置（`application.yml`）

| 前缀 | 用途 |
|------|------|
| **`lark.oapi`** | 飞书开放平台应用：`base-url`、`apps.{appKey}.app-id` / `app-secret` 等（必填，用于 OAuth、通讯录、IM 等） |
| **`lark.apass`** | 可选；飞书aPass应用：`base-url`、`apps.{appKey}.id` / `secret` / `namespace` 等 |

最小示例（单应用 `default`）：

```yaml
lark:
  oapi:
    base-url: https://open.feishu.cn
    apps:
      default:
        app-id: ${APP_ID:}
        app-secret: ${APP_SECRET:}
```

多应用在 `apps` 下增加多个 key，接口调用时通过 appKey 区分应用。

### 直接注入 `*Service`（可选）

下面以 **发 IM 文本** 为例（与 `POST /lark/im/send-text` 同源；需开放平台 IM 权限，`receiveId` / `receiveIdType` 与飞书文档一致）：

```java
import com.lark.oapi.service.im.v1.enums.ReceiveIdTypeEnum;
import com.larksuite.lark.sdk.service.message.ImMessageService;
import org.springframework.stereotype.Service;

@Service
public class LarkNotifyService {
    private final ImMessageService imMessageService;

    public LarkNotifyService(ImMessageService imMessageService) {
        this.imMessageService = imMessageService;
    }

    public void sendToUser(String appKey, String userId, String text) throws Exception {
        var resp = imMessageService.sendText(appKey, ReceiveIdTypeEnum.USER_ID, userId, text);
        if (!resp.success()) {
            throw new IllegalStateException(resp.getCode() + " " + resp.getMsg());
        }
    }
}
```

### 使用方式小结

| 步骤 | 说明 |
|------|------|
| 1 | 依赖 + `lark.oapi` |
| 2 | 启动后调 **`/lark/**`** 或注入 `*Service` |
| 3 | （可选）SpringDoc |

---

## 免密登录（飞书 OAuth）

### 整体步骤

| 步骤 | 做什么 |
|------|--------|
| 1 | **你**：开放平台建应用、开权限、登记 **重定向 URL**；`application.yml` 配 **`lark.oapi.apps`** |
| 2 | **你**：拼授权页 URL，用户打开并完成授权（[官方说明](https://open.feishu.cn/document/authentication-management/access-token/obtain-oauth-code)） |
| 3 | 飞书回调你的 `redirect_uri`，带上 **`code`** |
| 4 | **Starter**：`POST /lark/auth/access-token` 用 `code` 换 **`user_access_token`** |
| 5 | **Starter**（可选）：`POST /lark/identity/user-info` 拉资料 |
| 6 | **你**：写 Session/JWT、与用户表绑定 |

### Starter 免密相关接口

| 方法 | 路径 | 作用 |
|------|------|------|
| POST | `/lark/auth/access-token` | `code` → `user_access_token`（H5/App/小程序通用） |
| POST | `/lark/auth/refresh-access-token` | 刷新用户 token |
| POST | `/lark/identity/user-info` | 用 `user_access_token` 取用户信息 |
| GET | `/lark/auth/authorize`（可选） | 浏览器整页回调；未覆盖 **`LarkOAuthService`** 时默认 **`onAuthorized`** 会报错 |

### `LarkOAuthService`

**飞书免密登陆**时必须实现该接口，未实现访问 GET `/lark/auth/authorize` 抛错。不对接则不必实现。

```java
import com.lark.oapi.service.authen.v1.model.CreateAccessTokenResp;
import com.larksuite.lark.sdk.service.auth.LarkOAuthService;
import com.larksuite.lark.sdk.service.auth.LarkOAuthUserProfile;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

@Component
public class LarkOAuthServiceImpl implements LarkOAuthService {

    @Override
    public Object onAuthorized(String appKey, HttpServletRequest request, String state,
                               LarkOAuthUserProfile userProfile, CreateAccessTokenResp tokenResponse) {
        var body = userProfile.raw();
        String userName = body.getName(); // 飞书返回用户信息

        // 自行实现内容：根据用户标识从数据库加载本系统用户 
    }
}
```

### 须你配置

| 项 | 说明 |
|----|------|
| `redirect_uri` | 与开放平台登记 **完全一致**（含 https、路径、query） |
| 多应用 | 回调 URL 可加 `?appKey=配置键`（对应 `lark.oapi.apps` 的 key） |
| Swagger UI | Starter **不含**；需要时在 **宿主** 引入 **SpringDoc**（`springdoc-openapi-starter-webmvc-ui` 等） |

### Token

| Token | 含义 |
|-------|------|
| `user_access_token` | 用户身份（OAuth） |
| `tenant_access_token` | 应用身份，见「其他功能」；**不是**用户登录 |

### 脚本与 curl

**前端使用授权页 URL**

```bash
python3 - <<'PY'
import urllib.parse

client_id = "cli_a94a1fa790f91ccc"
app_key = "default"  # 多应用填写；单应用可 "" 省略 appKey
callback = "https://report-test.sensetime.com/api/lark/auth/authorize"
redirect_uri = callback + (("?appKey=" + urllib.parse.quote(app_key)) if app_key else "")
scope = "contact:contact.base:readonly"
state = "RANDOMSTRING"
base = "https://accounts.feishu.cn/open-apis/authen/v1/authorize"
q = {"client_id": client_id, "response_type": "code", "redirect_uri": redirect_uri,
     "scope": scope, "prompt": "consent", "state": state}
print(base + "?" + urllib.parse.urlencode(q, quote_via=urllib.parse.quote))
PY
```

**换票**

```bash
curl -sS -X POST 'http://127.0.0.1:8080/api/lark/auth/access-token' \
  -H 'Content-Type: application/json' \
  -d '{"appKey":"default","code":"YOUR_CODE"}'
```

**刷新**

```bash
curl -sS -X POST 'http://127.0.0.1:8080/api/lark/auth/refresh-access-token' \
  -H 'Content-Type: application/json' \
  -d '{"appKey":"default","refreshToken":"YOUR_REFRESH_TOKEN"}'
```

**用户信息**

```bash
curl -sS -X POST 'http://127.0.0.1:8080/api/lark/identity/user-info' \
  -H 'Content-Type: application/json' \
  -d '{"appKey":"default","userAccessToken":"YOUR_USER_ACCESS_TOKEN"}'
```

---

## Starter其他功能接口

| 说明 | |
|------|---|
| 下列 **curl** 可直接调 | 飞书 **权限、receiveId、业务数据** 须你在开放平台与本系统配置 |

| 能力 | 路径 | 飞书侧须你 |
|------|------|------------|
| 应用 token | `POST /lark/auth/tenant-access-token/internal` | 应用能力相关权限；[飞书文档](https://open.feishu.cn/document/server-docs/authentication-management/access-token/tenant_access_token_internal) |
| 通讯录示例 | `POST /lark/contact/users/batch-get-id` | 通讯录权限 |
| IM | `POST /lark/im/send-text` 等 | IM 权限；`receiveIdType` 与 `receiveId` 匹配 |

**应用 token**

```bash
curl -sS -X POST 'http://127.0.0.1:8080/api/lark/auth/tenant-access-token/internal' \
  -H 'Content-Type: application/json' \
  -d '{"appKey":"default"}'
```

**通讯录示例**

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

**IM**

| 类型 | 路径 |
|------|------|
| 文本 | `POST /lark/im/send-text` |
| 卡片 | `POST /lark/im/send-card` |
| 模板卡片 | `POST /lark/im/send-card-template` |
| 更新消息 | `POST /lark/im/update-message` |

```bash
curl -sS -X POST 'http://127.0.0.1:8080/api/lark/im/send-text' \
  -H 'Content-Type: application/json' \
  -d '{"appKey":"default","receiveIdType":"USER_ID","receiveId":"3f64af1d","text":"hello"}'
```

```bash
curl -sS -X POST 'http://127.0.0.1:8080/api/lark/im/send-card' \
  -H 'Content-Type: application/json' \
  -d '{"appKey":"default","receiveIdType":"USER_ID","receiveId":"3f64af1d","cardJson":"{\"config\":{\"wide_screen_mode\":true},\"header\":{\"template\":\"blue\",\"title\":{\"tag\":\"plain_text\",\"content\":\"标题\"}},\"elements\":[{\"tag\":\"div\",\"text\":{\"tag\":\"lark_md\",\"content\":\"内容\"}}]}"}'
```

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

```bash
curl -sS -X POST 'http://127.0.0.1:8080/api/lark/im/update-message' \
  -H 'Content-Type: application/json' \
  -d '{"appKey":"default","messageId":"om_xxx","contentJson":"{\"text\":\"更新后的文本内容\"}"}'
```

| 更多 | 位置 |
|------|------|
| 审批、日历、机器人、AE 等 | 源码 `*Controller` |
| 更多 `curl` | 对照源码 `*Controller` 的请求体与路径自行编写 |

---

## 工作台（Web / 小程序）

| 步骤 | 内容 |
|------|------|
| 1 | **你** 在飞书配工作台入口、域名白名单等 |
| 2 | 端上拿到 `code`（方式见飞书小程序/JS-SDK 文档） |
| 3 | `POST /lark/auth/access-token` 换票 |
| 4 | 按需 `POST /lark/identity/user-info` |
| 5 | **你** 做账号绑定、发 JWT |
