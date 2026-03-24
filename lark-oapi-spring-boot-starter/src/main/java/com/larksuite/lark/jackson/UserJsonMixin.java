package com.larksuite.lark.jackson;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Jackson mixin for SDK {@code User}: Gson {@code SerializedName} is ignored by Jackson; aliases
 * accept Feishu snake_case on REST bodies. Add more {@link JsonAlias} as needed alongside camelCase.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class UserJsonMixin {
    @JsonAlias("department_ids")
    String[] departmentIds;

    @JsonAlias("employee_type")
    Integer employeeType;
}
