package com.larksuite.lark.core.advice;

import com.lark.oapi.core.response.BaseResponse;
import com.larksuite.lark.api.dto.ApiResponse;
import com.larksuite.lark.core.common.LarkApi;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * 将 {@link LarkApi} 控制器的返回值统一为 {@link ApiResponse}。
 * <p>
 * 若返回飞书 SDK 的 {@link BaseResponse}：按 success/code 转为成功（仅 data 入包）或失败；其它类型则作为 {@code data} 原样包装。
 */
@RestControllerAdvice(annotations = LarkApi.class)
@ConditionalOnProperty(prefix = "lark.api", name = "enabled", havingValue = "true", matchIfMissing = true)
public class ApiResponseBodyAdvice implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(@NonNull MethodParameter returnType, @NonNull Class<? extends HttpMessageConverter<?>> converterType) {
        Class<?> t = returnType.getParameterType();
        if (void.class.equals(t) || Void.class.equals(t)) {
            return false;
        }
        return !ApiResponse.class.isAssignableFrom(t);
    }

    @Override
    public Object beforeBodyWrite(
            Object body,
            @NonNull MethodParameter returnType,
            @NonNull MediaType selectedContentType,
            @NonNull Class<? extends HttpMessageConverter<?>> selectedConverterType,
            @NonNull ServerHttpRequest request,
            @NonNull ServerHttpResponse response
    ) {
        if (body instanceof ApiResponse) {
            return body;
        }
        if (body == null) {
            return ApiResponse.failure("NULL_RESPONSE", "");
        }
        if (body instanceof BaseResponse<?> resp) {
            if (!resp.success()) {
                String msg = resp.getMsg() == null ? "" : resp.getMsg();
                return ApiResponse.failure(String.valueOf(resp.getCode()), msg);
            }
            return ApiResponse.success(resp.getData());
        }
        return ApiResponse.success(body);
    }
}
