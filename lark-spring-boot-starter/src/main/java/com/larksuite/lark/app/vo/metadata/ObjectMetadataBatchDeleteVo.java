package com.larksuite.lark.app.vo.metadata;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/** AE 批量删除对象元数据请求体。 */
public record ObjectMetadataBatchDeleteVo(
        @JsonProperty("object_api_names") List<String> objectApiNames
) {
}

