package com.larksuite.lark.app.vo.data.user;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * AE 单条记录新增请求体。
 * 结构：{ "record": { ... } }
 */
public record UserRecordCreateVo(JsonNode record) {
}

