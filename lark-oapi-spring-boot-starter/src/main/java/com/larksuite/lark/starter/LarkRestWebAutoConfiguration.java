package com.larksuite.lark.starter;

import com.larksuite.lark.oapi.spring.OapiAutoConfiguration;
import com.larksuite.lark.starter.condition.ConditionalOnStarterRestApi;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.ComponentScan;

/**
 * 将 starter 内 REST 层（{@code /api/lark/**} 控制器、统一响应 Advice、拦截器）注册进宿主上下文，
 * 宿主无需 {@code scanBasePackages} 包含 {@code com.larksuite.lark}。
 */
@AutoConfiguration
@AutoConfigureAfter({OapiAutoConfiguration.class, CommonAutoConfiguration.class, ImAutoConfiguration.class})
@ConditionalOnStarterRestApi
@ComponentScan(basePackages = {
        "com.larksuite.lark.web",
        "com.larksuite.lark.core.advice"
})
public class LarkRestWebAutoConfiguration {
}
