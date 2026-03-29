package com.larksuite.lark.app.openapi;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AppOpenApiClientRegistry {

    private final ObjectMapper objectMapper;
    private final AppOpenApiProperties properties;
    private final Map<String, AppOpenApiClient> clients = new ConcurrentHashMap<>();

    public AppOpenApiClientRegistry(ObjectMapper objectMapper, AppOpenApiProperties properties) {
        this.objectMapper = objectMapper;
        this.properties = properties;
    }

    public AppOpenApiClient getClient(String appKey) {
        String resolvedKey = resolveAppKey(appKey);
        AppOpenApiProperties.App app = properties.getApps().get(resolvedKey);
        if (app == null) {
            throw new IllegalArgumentException("lark.apass.apps." + resolvedKey + " is not configured");
        }
        normalizeAppDefaults(app);
        return clients.computeIfAbsent(resolvedKey, key -> new AppOpenApiClient(objectMapper, app, key));
    }

    private void normalizeAppDefaults(AppOpenApiProperties.App app) {
        if (app.getBaseUrl() == null || app.getBaseUrl().isBlank()) {
            app.setBaseUrl(properties.getBaseUrl());
        }
    }

    private String resolveAppKey(String appKey) {
        if (appKey != null && !appKey.isBlank()) {
            return appKey;
        }
        String primary = properties.getPrimary();
        if (primary != null && !primary.isBlank()) {
            return primary;
        }

        // If neither request `appKey` nor `primary` is set, fall back to the first configured app key.
        // This matches endpoints where `appKey` is optional.
        if (properties.getApps() != null && !properties.getApps().isEmpty()) {
            return properties.getApps().keySet().iterator().next();
        }

        throw new IllegalArgumentException("appKey is required (lark.apass.primary is not set, and lark.apass.apps.* is empty)");
    }
}

