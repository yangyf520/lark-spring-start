package com.larksuite.lark.core.advice;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/** 对 Lark JSON API 请求打 DEBUG 日志（路径与方法），便于联调。 */
@Component
@ConditionalOnProperty(prefix = "lark.api", name = "enabled", havingValue = "true", matchIfMissing = true)
public class LarkApiLoggingInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(LarkApiLoggingInterceptor.class);

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) {
        if (log.isDebugEnabled()) {
            log.debug("{} {}", request.getMethod(), request.getRequestURI());
        }
        return true;
    }
}
