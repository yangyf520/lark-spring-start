package com.larksuite.lark.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

@Schema(
        name = "ApiResponse",
        description = "Lark JSON API 统一响应。HTTP 多为 200；业务失败时 ok=false，error/message 有值。"
)
public record ApiResponse(
        @Schema(description = "是否调用成功（含飞书业务成功）") boolean ok,
        @Schema(description = "服务端时间（ISO-8601）") String time,
        @Schema(description = "成功时的业务载荷") Object data,
        @Schema(description = "错误码（飞书 code 或 VALIDATION_ERROR 等）") String error,
        @Schema(description = "错误说明") String message
) {
    public static ApiResponse success(Object data) {
        return new ApiResponse(true, Instant.now().toString(), data, "", "");
    }

    public static ApiResponse failure(String error, String message) {
        return new ApiResponse(false, Instant.now().toString(), null, error, message == null ? "" : message);
    }
}
