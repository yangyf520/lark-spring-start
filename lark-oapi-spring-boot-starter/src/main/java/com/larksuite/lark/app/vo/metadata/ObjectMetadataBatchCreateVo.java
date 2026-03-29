package com.larksuite.lark.app.vo.metadata;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;

public record ObjectMetadataBatchCreateVo(List<JsonNode> objects) {
}

