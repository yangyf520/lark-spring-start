package com.larksuite.lark.api.exception;

/** 飞书 OpenAPI 返回业务错误码（HTTP 仍由全局 {@code ApiResponse} 表示为 ok=false）。 */
public class SdkException extends RuntimeException {

    private final String errorCode;

    public SdkException(String errorCode, String message) {
        super(message == null ? "" : message);
        this.errorCode = errorCode == null ? "" : errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
