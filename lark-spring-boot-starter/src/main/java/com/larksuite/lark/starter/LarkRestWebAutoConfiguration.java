package com.larksuite.lark.starter;

import com.larksuite.lark.sdk.core.OapiAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.ComponentScan;

/**
 * 将 starter 内 REST 层（控制器、统一异常处理等）注册进宿主上下文，
 * 宿主无需额外配置 scanBasePackages。
 */
@AutoConfiguration
@AutoConfigureAfter({OapiAutoConfiguration.class, CommonAutoConfiguration.class})
@ComponentScan(basePackages = {
        "com.larksuite.lark.sdk.controller",
        "com.larksuite.lark.app.controller",
        "com.larksuite.lark.core.advice"
})
public class LarkRestWebAutoConfiguration {
}
