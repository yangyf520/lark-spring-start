package com.larksuite.lark.app.vo.data.user;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;

/** AE 用户批量新增请求体。 */
public record UserCreateVo(List<JsonNode> records) {
}

