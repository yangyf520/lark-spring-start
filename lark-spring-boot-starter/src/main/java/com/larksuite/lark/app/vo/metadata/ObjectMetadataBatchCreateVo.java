package com.larksuite.lark.app.vo.metadata;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;

/** AE 批量创建对象元数据请求体。 */
public record ObjectMetadataBatchCreateVo(List<JsonNode> objects) {
}

