package com.larksuite.lark.oapi.spring;

import com.lark.oapi.event.EventDispatcher;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

public class OapiEventDispatcherRegistry {

    private final Map<String, EventDispatcher> dispatchersByKey;

    public OapiEventDispatcherRegistry(Map<String, EventDispatcher> dispatchersByKey) {
        this.dispatchersByKey = Collections.unmodifiableMap(Objects.requireNonNull(dispatchersByKey));
    }

    public Map<String, EventDispatcher> dispatchers() {
        return dispatchersByKey;
    }

    public EventDispatcher get(String appKey) {
        EventDispatcher dispatcher = dispatchersByKey.get(appKey);
        if (dispatcher == null) {
            throw new IllegalArgumentException("Unknown lark.open app key: " + appKey);
        }
        return dispatcher;
    }
}

