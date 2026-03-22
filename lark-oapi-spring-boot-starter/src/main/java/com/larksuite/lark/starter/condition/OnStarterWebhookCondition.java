package com.larksuite.lark.starter.condition;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;

/** {@code lark.api.enabled} 与 {@code lark.api.webhook.enabled} 均为 true（默认）。 */
public final class OnStarterWebhookCondition implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        Environment env = context.getEnvironment();
        boolean master = env.getProperty("lark.api.enabled", Boolean.class, true);
        boolean webhook = env.getProperty("lark.api.webhook.enabled", Boolean.class, true);
        return master && webhook;
    }
}
