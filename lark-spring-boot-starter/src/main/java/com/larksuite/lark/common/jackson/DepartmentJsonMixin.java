package com.larksuite.lark.common.jackson;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lark.oapi.service.contact.v3.model.DepartmentI18nName;

/**
 * Jackson mixin：兼容 SDK 模型的 snake_case 字段（用于 Spring MVC 入参反序列化）。
 * 通过 JsonAlias 同时接受 snake_case 与 Java 字段名。
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class DepartmentJsonMixin {
    @JsonAlias("i18n_name")
    DepartmentI18nName i18nName;

    @JsonAlias("parent_department_id")
    String parentDepartmentId;

    @JsonAlias("department_id")
    String departmentId;

    @JsonAlias("open_department_id")
    String openDepartmentId;

    @JsonAlias("leader_user_id")
    String leaderUserId;

    @JsonAlias("chat_id")
    String chatId;

    @JsonAlias("unit_ids")
    String[] unitIds;

    @JsonAlias("member_count")
    Integer memberCount;

    @JsonAlias("create_group_chat")
    Boolean createGroupChat;

    @JsonAlias("group_chat_employee_types")
    Integer[] groupChatEmployeeTypes;

    @JsonAlias("department_hrbps")
    String[] departmentHrbps;

    @JsonAlias("primary_member_count")
    Integer primaryMemberCount;
}
