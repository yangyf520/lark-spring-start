package com.larksuite.lark.core.advice;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/** 注册 OpenAPI 控制器拦截器：统一日志，不包含事件回调（原始响应由 SDK 写出）。 */
@Configuration
@ConditionalOnProperty(prefix = "lark.api", name = "enabled", havingValue = "true", matchIfMissing = true)
public class OapiWebMvcConfiguration implements WebMvcConfigurer {

    private final ApiLoggingInterceptor loggingInterceptor;

    public OapiWebMvcConfiguration(ApiLoggingInterceptor loggingInterceptor) {
        this.loggingInterceptor = loggingInterceptor;
    }

    @Override
    public void addInterceptors(@NonNull InterceptorRegistry registry) {
        registry.addInterceptor(loggingInterceptor)
                .addPathPatterns("/api/lark/**")
                .excludePathPatterns("/api/lark/webhook", "/api/lark/webhook/**");
    }
}
