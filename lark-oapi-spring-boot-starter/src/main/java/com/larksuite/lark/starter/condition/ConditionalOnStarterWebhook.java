package com.larksuite.lark.starter.condition;

import org.springframework.context.annotation.Conditional;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** 总开关 {@code lark.api.enabled} 与 Webhook 子开关 {@code lark.api.webhook.enabled} 均为 true（默认）。 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Conditional(OnStarterWebhookCondition.class)
public @interface ConditionalOnStarterWebhook {
}
