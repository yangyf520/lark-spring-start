package com.larksuite.lark.app.vo.metadata;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;

/** AE 批量更新对象元数据请求体。 */
public record ObjectMetadataBatchUpdateVo(List<JsonNode> objects) {
}

