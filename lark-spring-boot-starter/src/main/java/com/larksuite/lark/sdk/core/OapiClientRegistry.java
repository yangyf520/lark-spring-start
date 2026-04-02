package com.larksuite.lark.sdk.core;

import com.lark.oapi.Client;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

/** 多应用 {@link Client} 注册表，key 与 {@code lark.oapi.apps} 一致。 */
public class OapiClientRegistry {

    private final Map<String, Client> clientsByKey;
    private final String primaryKey;

    public OapiClientRegistry(Map<String, Client> clientsByKey, String primaryKey) {
        this.clientsByKey = Collections.unmodifiableMap(Objects.requireNonNull(clientsByKey));
        this.primaryKey = primaryKey;
    }

    /** 全部已配置 client（不可变）。 */
    public Map<String, Client> clients() {
        return clientsByKey;
    }

    /** 按 appKey 取 client，未知 key 抛 IllegalArgumentException。 */
    public Client get(String appKey) {
        Client client = clientsByKey.get(appKey);
        if (client == null) {
            throw new IllegalArgumentException("Unknown lark.oapi app key: " + appKey);
        }
        return client;
    }

    /** 主应用 key，可能为 null。 */
    public String primaryKey() {
        return primaryKey;
    }

    /** 主应用 client；未配置 primary 时抛 IllegalStateException。 */
    public Client primary() {
        if (primaryKey == null || primaryKey.isBlank()) {
            throw new IllegalStateException("No primary app configured (set lark.oapi.primary)");
        }
        return get(primaryKey);
    }
}

