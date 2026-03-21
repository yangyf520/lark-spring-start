# Sense Lark

基于 **Spring Boot 3** 与 **飞书开放平台 Java SDK（oapi-sdk）** 的示例工程：封装常用 REST 能力（通讯录、消息、审批、日历、机器人信息等），并提供事件回调入口，便于对接控制台或自动化脚本。

## 功能概览

- **多应用配置**：通过 `lark.oapi.apps` 管理多个飞书应用（`app-id` / `app-secret` 等）。
- **统一 REST**：`/api/lark/**` 下提供鉴权、通讯录、IM、审批、日历、机器人等代理接口。
- **事件回调**：`POST /api/lark/webhook`（及带 `appKey` 的路径）交由 SDK `EventDispatcher` 处理验签/解密（业务处理器需自行注册）。
- **本地运维**：`/api/admin/health`、Swagger UI、可选 Actuator。

## 工程结构（Maven 多模块）

```text
sense-lark/
├── pom.xml                          # 父 POM（Java 17）
├── lark-oapi-spring-boot-starter/   # 飞书 OAPI 封装与自动配置
├── backend/                         # 可运行的 Spring Boot 应用（依赖 starter）
├── .cursor/rules/                   # Cursor 编辑器规则（建议纳入 Git，统一团队 AI 提示）
├── scripts/                         # curl 冒烟脚本与 API 说明
├── start.sh                         # 打包并后台启动 backend，跟随日志
└── stop.sh                          # 按 pid 文件停止 backend
```

## 环境要求

- **JDK 17**
- **Maven 3.6+**
- 飞书开放平台应用：`APP_ID`、`APP_SECRET` 等（见 `backend/.env.example`）

## 快速开始

### 1. 配置环境变量

```bash
cp backend/.env.example backend/.env
# 编辑 backend/.env，填入 APP_ID、APP_SECRET、VERIFICATION_TOKEN、ENCRYPT_KEY 等
```

### 2. 启动后端

**方式 A（推荐）：项目根目录一键脚本**

```bash
./start.sh
```

启动前会尝试释放 **8080** 端口（可通过 `SERVER_PORT` 指定其他端口）。日志：`.run/logs/backend.log`。

**方式 B：Maven 直接运行**

```bash
cd backend
mvn spring-boot:run
```

默认地址：**http://127.0.0.1:8080**

### 3. 文档与调试

- **Swagger UI**：浏览器打开 [http://127.0.0.1:8080/](http://127.0.0.1:8080/) 会重定向到 Swagger 页面。
- **接口列表与 curl 示例**：见 [`scripts/README-api.md`](scripts/README-api.md)。
- **一键冒烟**：`chmod +x scripts/api-lark-smoke.sh && ./scripts/api-lark-smoke.sh`

## 配置说明要点

- 主配置：`backend/src/main/resources/application.yml`（应用端口、`lark.oapi` 等）。
- 敏感信息：放在 **`backend/.env`**（已被 `.gitignore` 忽略），勿提交仓库。
- 飞书 SDK 请求日志：已对 `com.lark.oapi.core.Transport` 使用 DEBUG；生产环境请按需调低级别，避免泄露 token。

## 版本与依赖

- Spring Boot：**3.4.x**（见父 `pom.xml`）
- 飞书 oapi-sdk：**2.4.0**（属性 `lark.oapi-sdk.version`）

## 一键冒烟脚本

```bash
chmod +x scripts/api-lark-smoke.sh
./scripts/api-lark-smoke.sh
# 带群 ID 测发消息（机器人需在群内）：
CHAT_ID=oc_xxxxxxxx ./scripts/api-lark-smoke.sh
```

脚本路径：`scripts/api-lark-smoke.sh`（内含各接口说明注释）。

## 接口分组

| 前缀 | 说明 |
|------|------|
| `/api/admin` | 本机管理（健康、TAT 探测） |
| `/api/lark/oapi` | 多应用 Client 自检 |
| `/api/lark/bot` | 机器人信息（飞书 `bot/v3/info`） |
| `/api/lark/auth` | 租户/用户 token 换取 |
| `/api/lark/contact` | 通讯录 |
| `/api/lark/chat` | 会话 |
| `/api/lark/im` | 发消息 / 更新消息 |
| `/api/lark/approval` | 审批 |
| `/api/lark/calendar` | 日历事件 |
| `/api/lark/identity` | 用户身份（需 user_access_token） |
| `/api/lark` | 事件回调 `POST /webhook`（供飞书订阅配置） |
| `/` | 重定向至 Swagger UI |
| `/actuator/health` | Spring 健康（若已暴露） |

具体路径与参数以各 `*Controller` 为准；Swagger：`/swagger-ui/index.html`。