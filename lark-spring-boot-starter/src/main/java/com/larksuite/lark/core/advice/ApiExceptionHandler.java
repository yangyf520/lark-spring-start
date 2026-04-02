package com.larksuite.lark.core.advice;

import com.larksuite.lark.common.annotation.LarkApi;
import com.larksuite.lark.core.exception.SystemException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

/** 仅作用于 {@link LarkApi}：将异常统一为 {@link SystemException} 并返回合适的 HTTP 状态码。 */
@RestControllerAdvice(annotations = LarkApi.class)
public class ApiExceptionHandler {

    /** 业务显式抛出的错误码与文案。 */
    @ExceptionHandler(SystemException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public SystemException handleSystem(SystemException e) {
        return e;
    }

    /** 参数不合法（如非法 appKey）。 */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public SystemException handleIllegalArgument(IllegalArgumentException e) {
        return new SystemException("INVALID_ARGUMENT", e.getMessage());
    }

    /** Bean Validation（@Valid）校验失败。 */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public SystemException handleValidation(MethodArgumentNotValidException e) {
        String msg = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));
        return new SystemException("VALIDATION_ERROR", msg.isBlank() ? e.getMessage() : msg);
    }

    /** JSON 反序列化失败等。 */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public SystemException handleNotReadable(HttpMessageNotReadableException e) {
        Throwable root = e.getMostSpecificCause() == null ? e : e.getMostSpecificCause();
        return new SystemException("BAD_REQUEST", root.getMessage());
    }

    /** 兜底：其它未分类异常。 */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public SystemException handleAny(Exception e) {
        return new SystemException(e.getClass().getSimpleName(), e.getMessage());
    }
}
