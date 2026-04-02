package com.larksuite.lark.core.exception;

/** 业务层抛出：由 {@link com.larksuite.lark.core.advice.ApiExceptionHandler} 统一写出为 HTTP JSON。 */
public class SystemException extends RuntimeException {

    private final String errorCode;

    /**
     * @param errorCode 业务错误码
     * @param message 对人可读说明
     */
    public SystemException(String errorCode, String message) {
        super(message == null ? "" : message);
        this.errorCode = errorCode == null ? "" : errorCode;
    }

    /** 业务错误码。 */
    public String getErrorCode() {
        return errorCode;
    }
}

