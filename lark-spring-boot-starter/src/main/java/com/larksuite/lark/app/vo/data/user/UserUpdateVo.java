package com.larksuite.lark.app.vo.data.user;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;

/** AE 用户批量更新请求体。 */
public record UserUpdateVo(List<JsonNode> records) {
}

