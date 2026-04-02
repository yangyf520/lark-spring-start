package com.larksuite.lark.common.jackson;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Jackson mixin：兼容 SDK User 的 snake_case 字段（用于 Spring MVC 入参反序列化）。
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class UserJsonMixin {
    @JsonAlias("department_ids")
    String[] departmentIds;

    @JsonAlias("employee_type")
    Integer employeeType;
}
