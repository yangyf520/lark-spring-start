# =============================================================================
# default OpenAPI（对象元数据 / 数据表 records）
# =============================================================================

# --- POST /api/lark/app/objects/batch-create ---
# 功能：批量创建对象元数据（对象定义）。
# 参数（JSON）：与 ae-openapi.feishu.cn 文档一致——对象/字段使用 api_name，label 为多语对象，字段 type 为嵌套结构。
# （Starter 也会把旧示例里的 object_api_name / field_api_name / field_type / 字符串 label 规范成上述格式。）
curl -sS -X POST 'http://127.0.0.1:8080/api/lark/app/objects/batch-create?appKey=default' \
  -H 'Content-Type: application/json; charset=utf-8' \
  -d '{
    "objects": [
      {
        "api_name": "object_script_demo",
        "label": { "zh_cn": "脚本示例对象", "en_us": "Script demo object" },
        "settings": {
          "search_layout": ["_id"],
          "allow_search_fields": ["_id"],
          "display_name": "number"
        },
        "fields": [
          {
            "api_name": "_id",
            "label": { "zh_cn": "主键编号", "en_us": "Primary id" },
            "type": {
              "name": "number",
              "settings": {
                "decimalPlacesNumber": 0,
                "displayAsPercentage": false,
                "unique": true,
                "required": true
              }
            },
            "encrypt_type": null
          }
        ]
      }
    ]
  }'

# --- POST /api/lark/app/objects/batch-update ---
# 功能：批量更新对象元数据；请求体与 ae-openapi …/objects/batch_update 文档示例一致（经本服务转发，无需 Authorization）。
# fields[].operator（与 OpenAPI 文档一致）：add=新增字段定义，replace=替换已有字段定义，remove=删除字段；null 常表示随文档「全量描述」不传变更语义。
# 勿对系统主键 _id 使用 add（对象已存在时易被拒）；新增业务字段才对自定义 api_name 用 add。
# 若 HTTP 200 但 code=k_ec_000010「暂不支持此功能」，多为平台拒绝该操作（含错误 operator/字段组合或环境未开放），需对照凭证文档或服务台。
curl -sS -X POST 'http://127.0.0.1:8080/api/lark/app/objects/batch-update?appKey=default' \
  -H 'Content-Type: application/json; charset=utf-8' \
  -d '{
    "objects": [
      {
        "api_name": "object_abc",
        "label": { "zh_cn": "示例文本", "en_us": "Sample text" },
        "settings": {
          "display_name": "number",
          "search_layout": ["_id"],
          "allow_search_fields": ["_id"]
        },
        "fields": [
          {
            "operator": null,
            "api_name": "_id",
            "label": { "zh_cn": "示例文本", "en_us": "Sample text" },
            "type": {
              "name": "number",
              "settings": {
                "required": true,
                "decimalPlacesNumber": 0,
                "displayAsPercentage": false,
                "unique": true
              }
            },
            "encrypt_type": null
          }
        ]
      }
    ]
  }'

# --- POST /api/lark/app/data/objects/{objectApiName}/records/create ---
# 功能：单条创建记录（对应 ae-openapi …/objects/{api_name}/records）。{objectApiName} 换成任意业务对象 api_name，例如 enterprise_service_ticket。
# 请求体与官方一致：{ "record": { …字段… } }；无需 Authorization，由 lark.apass.apps.<appKey> 换 appToken。
curl -sS -X POST 'http://127.0.0.1:8080/api/lark/app/data/objects/enterprise_service_ticket/records/create?appKey=default' \
  -H 'Content-Type: application/json; charset=utf-8' \
  -d '{
    "record": {
      "priority": "low",
      "title": "Sample text",
      "disable": false,
      "submit_time": 1774703746280,
      "department": { "_id": "100" },
      "status": "pending",
      "assignee": { "_id": "100" },
      "description": {
        "raw": "<div data-zone-id=\"0\" data-line-index=\"0\" data-line=\"true\" style=\"white-space: pre;\">Sample text\n</div>"
      },
      "completion_time": 1774703746280,
      "ticket_number": "Sample text",
      "submitter": { "_id": "100" }
    }
  }'

# --- DELETE /api/lark/app/data/objects/{objectApiName}/records/batch-delete ---
# 功能：批量删除记录（对应 ae-openapi …/objects/{api_name}/records_batch，请求体含 ids）。
curl -sS -X DELETE 'http://127.0.0.1:8080/api/lark/app/data/objects/enterprise_service_ticket/records/batch-delete?appKey=default' \
  -H 'Content-Type: application/json; charset=utf-8' \
  -d '{
    "ids": ["100"]
  }'

# --- POST /api/lark/app/objects/batch-delete ---
# 功能：批量删除对象元数据（对象定义）。
curl -sS -X POST 'http://127.0.0.1:8080/api/lark/app/objects/batch-delete?appKey=default' \
  -H 'Content-Type: application/json; charset=utf-8' \
  -d '{
    "object_api_names": ["_demo_object"]
  }'

# --- POST /api/lark/app/data/objects/_department/records/batch-create ---
# 功能：批量新增部门记录（数据表）。
curl -sS -X POST 'http://127.0.0.1:8080/api/lark/app/data/objects/_department/records/batch-create?appKey=default' \
  -H 'Content-Type: application/json; charset=utf-8' \
  -d '{
    "records": [
      { "fields": { "name": "研发部", "parent_id": "0" } }
    ]
  }'

# --- PATCH /api/lark/app/data/objects/_department/records/batch-update ---
# 功能：批量更新部门记录（数据表）。
curl -sS -X PATCH 'http://127.0.0.1:8080/api/lark/app/data/objects/_department/records/batch-update?appKey=default' \
  -H 'Content-Type: application/json; charset=utf-8' \
  -d '{
    "records": [
      { "id": "recXXXXXXXXXXXX", "fields": { "name": "研发一部" } }
    ]
  }'

# --- DELETE /api/lark/app/data/objects/_department/records/batch-delete ---
# 功能：批量删除部门记录（数据表）。
curl -sS -X DELETE 'http://127.0.0.1:8080/api/lark/app/data/objects/_department/records/batch-delete?appKey=default' \
  -H 'Content-Type: application/json; charset=utf-8' \
  -d '{"ids":["recXXXXXXXXXXXX","recYYYYYYYYYYYY"]}'

# --- POST /api/lark/app/data/objects/_department/records/query ---
# 功能：查询部门记录（数据表）。
curl -sS -X POST 'http://127.0.0.1:8080/api/lark/app/data/objects/_department/records/query?appKey=default' \
  -H 'Content-Type: application/json; charset=utf-8' \
  -d '{
    "filter": {
      "conjunction": "and",
      "conditions": [
        { "field_name": "name", "operator": "contains", "value": ["研发"] }
      ]
    },
    "order_by": [{"field_name":"created_time","order":"desc"}],
    "page_size": 20,
    "need_total_count": true
  }'

# --- POST /api/lark/app/data/objects/_user/records/batch-create ---
# 功能：批量新增用户记录（数据表）。
# 注意：低代码对象 _user 的字段 API 名多为下划线前缀（如 _email、_name），与 open_api 文档示例一致；
# 传 email/name 会报「字段不存在」。
curl -sS -X POST 'http://127.0.0.1:8080/api/lark/app/data/objects/_user/records/batch-create?appKey=default' \
  -H 'Content-Type: application/json; charset=utf-8' \
  -d '{
    "records": [
      {
        "fields": {
          "_email": "zhangsan@example.com",
          "_name": { "zh_cn": "张三", "en_us": "Zhang San" }
        }
      }
    ]
  }'

# --- POST /api/lark/app/data/objects/_user/records/create ---
# 功能：单条新增用户记录（数据表）。请求体为 { "record": { ... } }，与 AE OpenAPI /records 一致。
curl -sS -X POST 'http://127.0.0.1:8080/api/lark/app/data/objects/_user/records/create?appKey=default' \
  -H 'Content-Type: application/json; charset=utf-8' \
  -d '{
    "record": {
      "_email": "lisi@example.com",
      "_name": { "zh_cn": "李四", "en_us": "Li Si" }
    }
  }'

# --- PATCH /api/lark/app/data/objects/_user/records/batch-update ---
# 功能：批量更新用户记录（数据表）。
curl -sS -X PATCH 'http://127.0.0.1:8080/api/lark/app/data/objects/_user/records/batch-update?appKey=default' \
  -H 'Content-Type: application/json; charset=utf-8' \
  -d '{
    "records": [
      {
        "id": "recXXXXXXXXXXXX",
        "fields": { "_name": { "zh_cn": "张三-已更新", "en_us": "Zhang San Updated" } }
      }
    ]
  }'

# --- DELETE /api/lark/app/data/objects/_user/records/batch-delete ---
# 功能：批量删除用户记录（数据表）。
curl -sS -X DELETE 'http://127.0.0.1:8080/api/lark/app/data/objects/_user/records/batch-delete?appKey=default' \
  -H 'Content-Type: application/json; charset=utf-8' \
  -d '{"ids":["recXXXXXXXXXXXX","recYYYYYYYYYYYY"]}'

# --- POST /api/lark/app/data/objects/_user/records/query ---
# 功能：查询用户记录（数据表）。
curl -sS -X POST 'http://127.0.0.1:8080/api/lark/app/data/objects/_user/records/query?appKey=default' \
  -H 'Content-Type: application/json; charset=utf-8' \
  -d '{
    "filter": {
      "conjunction": "and",
      "conditions": [
        { "field_name": "_name", "operator": "contains", "value": ["张"] }
      ]
    },
    "order_by": [{"field_name":"created_time","order":"desc"}],
    "page_size": 20,
    "need_total_count": true
  }'
