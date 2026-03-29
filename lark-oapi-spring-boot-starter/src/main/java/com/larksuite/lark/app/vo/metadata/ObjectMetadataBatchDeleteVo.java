package com.larksuite.lark.app.vo.metadata;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record ObjectMetadataBatchDeleteVo(
        @JsonProperty("object_api_names") List<String> objectApiNames
) {
}

