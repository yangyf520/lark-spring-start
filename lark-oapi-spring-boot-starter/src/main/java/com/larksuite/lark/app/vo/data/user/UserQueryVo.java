package com.larksuite.lark.app.vo.data.user;

import com.fasterxml.jackson.databind.JsonNode;

public record UserQueryVo(
        JsonNode filter,
        JsonNode order_by,
        Boolean use_page_token,
        Integer page_size,
        Integer offset,
        JsonNode select,
        JsonNode group_by,
        String page_token,
        Boolean need_total_count,
        Boolean query_deleted_record
) {
}

