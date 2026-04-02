package com.larksuite.lark.sdk.core;

import com.lark.oapi.event.EventDispatcher;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

/** 多应用事件回调分发器注册表（供 Webhook 回调按 appKey 路由）。 */
public class OapiEventDispatcherRegistry {

    private final Map<String, EventDispatcher> dispatchersByKey;

    public OapiEventDispatcherRegistry(Map<String, EventDispatcher> dispatchersByKey) {
        this.dispatchersByKey = Collections.unmodifiableMap(Objects.requireNonNull(dispatchersByKey));
    }

    /** 返回全部 dispatcher（只读）。 */
    public Map<String, EventDispatcher> dispatchers() {
        return dispatchersByKey;
    }

    /** 按 appKey 获取 dispatcher，未知 key 抛 IllegalArgumentException。 */
    public EventDispatcher get(String appKey) {
        EventDispatcher dispatcher = dispatchersByKey.get(appKey);
        if (dispatcher == null) {
            throw new IllegalArgumentException("Unknown lark.oapi app key: " + appKey);
        }
        return dispatcher;
    }
}

