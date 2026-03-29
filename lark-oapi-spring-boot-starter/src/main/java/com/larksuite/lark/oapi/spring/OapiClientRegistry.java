package com.larksuite.lark.oapi.spring;

import com.lark.oapi.Client;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

public class OapiClientRegistry {

    private final Map<String, Client> clientsByKey;
    private final String primaryKey;

    public OapiClientRegistry(Map<String, Client> clientsByKey, String primaryKey) {
        this.clientsByKey = Collections.unmodifiableMap(Objects.requireNonNull(clientsByKey));
        this.primaryKey = primaryKey;
    }

    public Map<String, Client> clients() {
        return clientsByKey;
    }

    public Client get(String appKey) {
        Client client = clientsByKey.get(appKey);
        if (client == null) {
            throw new IllegalArgumentException("Unknown lark.open app key: " + appKey);
        }
        return client;
    }

    public String primaryKey() {
        return primaryKey;
    }

    public Client primary() {
        if (primaryKey == null || primaryKey.isBlank()) {
            throw new IllegalStateException("No primary app configured (set lark.open.primary)");
        }
        return get(primaryKey);
    }
}

