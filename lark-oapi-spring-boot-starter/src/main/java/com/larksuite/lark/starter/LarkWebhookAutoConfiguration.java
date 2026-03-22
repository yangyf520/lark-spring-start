package com.larksuite.lark.starter;

import com.larksuite.lark.oapi.spring.OapiAutoConfiguration;
import com.larksuite.lark.starter.condition.ConditionalOnStarterWebhook;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.ComponentScan;

/**
 * 注册飞书事件回调控制器；宿主无需额外组件扫描配置。
 */
@AutoConfiguration
@AutoConfigureAfter(OapiAutoConfiguration.class)
@ConditionalOnStarterWebhook
@ComponentScan(basePackages = "com.larksuite.lark.bot.webhook")
public class LarkWebhookAutoConfiguration {
}
