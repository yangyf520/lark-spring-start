package com.larksuite.lark.app.vo.data.user;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;

public record UserCreateVo(List<JsonNode> records) {
}

