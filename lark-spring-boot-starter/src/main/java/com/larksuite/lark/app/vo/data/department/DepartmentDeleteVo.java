package com.larksuite.lark.app.vo.data.department;

import java.util.List;

/** AE 部门批量删除请求体。 */
public record DepartmentDeleteVo(List<String> ids) {
}

