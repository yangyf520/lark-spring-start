package com.larksuite.lark.core.advice;

import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标记使用统一 {@link com.larksuite.lark.api.dto.ApiResponse} 契约的 REST 控制器；
 * {@link RestControllerAdvice} 仅作用于带此注解的控制器，避免影响运维接口等其它返回格式。
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface LarkApi {
}
