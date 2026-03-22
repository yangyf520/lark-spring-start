package com.larksuite.lark.starter.condition;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;

/** {@code lark.api.enabled} 与 {@code lark.api.rest.enabled} 均为 true（默认）。 */
public final class OnStarterRestApiCondition implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        Environment env = context.getEnvironment();
        boolean master = env.getProperty("lark.api.enabled", Boolean.class, true);
        boolean rest = env.getProperty("lark.api.rest.enabled", Boolean.class, true);
        return master && rest;
    }
}
