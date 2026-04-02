package com.larksuite.lark.app.vo.data.department;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;

/** AE 部门批量更新请求体。 */
public record DepartmentUpdateVo(List<JsonNode> records) {
}

