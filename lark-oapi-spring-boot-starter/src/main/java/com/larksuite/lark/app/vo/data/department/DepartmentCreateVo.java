package com.larksuite.lark.app.vo.data.department;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;

public record DepartmentCreateVo(List<JsonNode> records) {
}

