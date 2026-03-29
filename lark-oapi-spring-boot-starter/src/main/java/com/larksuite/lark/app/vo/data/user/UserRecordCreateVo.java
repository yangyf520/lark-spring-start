package com.larksuite.lark.app.vo.data.user;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * AE OpenAPI single-record create payload wrapper.
 * Shape: { "record": { ... } }
 */
public record UserRecordCreateVo(JsonNode record) {
}

