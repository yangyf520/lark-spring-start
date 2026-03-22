package com.larksuite.lark.core.advice;

import com.larksuite.lark.api.dto.ApiResponse;
import com.larksuite.lark.api.exception.SdkException;
import com.larksuite.lark.core.common.LarkApi;
import com.larksuite.lark.starter.condition.ConditionalOnStarterRestApi;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

/** 将未捕获异常转为 {@link ApiResponse}（HTTP 200 + ok=false），与 {@link ApiResponseBodyAdvice} 成功包装对称。 */
@RestControllerAdvice(annotations = LarkApi.class)
@ConditionalOnStarterRestApi
public class ApiExceptionHandler {

    @ExceptionHandler(SdkException.class)
    public ApiResponse handleSdk(SdkException e) {
        return ApiResponse.failure(e.getErrorCode(), e.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ApiResponse handleIllegalArgument(IllegalArgumentException e) {
        return ApiResponse.failure("INVALID_ARGUMENT", e.getMessage() == null ? "" : e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResponse handleValidation(MethodArgumentNotValidException e) {
        String msg = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));
        if (msg.isBlank()) {
            msg = e.getMessage();
        }
        return ApiResponse.failure("VALIDATION_ERROR", msg == null ? "" : msg);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ApiResponse handleNotReadable(HttpMessageNotReadableException e) {
        return ApiResponse.failure("BAD_REQUEST", e.getMostSpecificCause().getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ApiResponse handleAny(Exception e) {
        return ApiResponse.failure(e.getClass().getSimpleName(), e.getMessage() == null ? "" : e.getMessage());
    }
}
