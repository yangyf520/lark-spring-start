package com.larksuite.lark.api.dto;

import java.time.Instant;

public record ApiResponse(
        boolean ok,
        String time,
        Object data,
        String error,
        String message
) {
    public static ApiResponse success(Object data) {
        return new ApiResponse(true, Instant.now().toString(), data, "", "");
    }

    public static ApiResponse failure(String error, String message) {
        return new ApiResponse(false, Instant.now().toString(), null, error, message == null ? "" : message);
    }
}
