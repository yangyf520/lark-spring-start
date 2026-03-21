package com.larksuite.lark.oapi.spring;

import com.lark.oapi.event.EventDispatcher;

@FunctionalInterface
public interface OapiEventDispatcherCustomizer {

    /**
     * Customize {@link EventDispatcher.Builder} for a specific app key.
     * Implementations should register handlers via builder.onXXX(...).
     */
    void customize(String appKey, EventDispatcher.Builder builder);
}

