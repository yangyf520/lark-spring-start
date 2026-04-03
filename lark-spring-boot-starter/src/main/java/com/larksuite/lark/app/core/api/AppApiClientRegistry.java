package com.larksuite.lark.app.core.api;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/** 按 appKey 管理 AE API 客户端（带本地缓存）。 */
public class AppApiClientRegistry {

    private final ObjectMapper objectMapper;
    private final AppApiProperties properties;
    private final Map<String, AppApiClient> clients = new ConcurrentHashMap<>();

    public AppApiClientRegistry(ObjectMapper objectMapper, AppApiProperties properties) {
        this.objectMapper = objectMapper;
        this.properties = properties;
    }

    /** 获取指定 appKey 的客户端；未传入且仅配置一套 {@code lark.apass.apps} 时自动使用该 key。 */
    public AppApiClient getClient(String appKey) {
        String resolvedKey = resolveAppKey(appKey);
        AppApiProperties.App app = properties.getApps().get(resolvedKey);
        if (app == null) {
            throw new IllegalArgumentException("lark.apass.apps." + resolvedKey + " is not configured");
        }
        normalizeAppDefaults(app);
        return clients.computeIfAbsent(resolvedKey, key -> new AppApiClient(objectMapper, app, key));
    }

    private void normalizeAppDefaults(AppApiProperties.App app) {
        if (app.getBaseUrl() == null || app.getBaseUrl().isBlank()) {
            app.setBaseUrl(properties.getBaseUrl());
        }
    }

    private String resolveAppKey(String appKey) {
        if (appKey != null && !appKey.isBlank()) {
            return appKey;
        }
        Map<String, AppApiProperties.App> apps = properties.getApps();
        if (apps != null && apps.size() == 1) {
            return apps.keySet().iterator().next();
        }
        throw new IllegalArgumentException("appKey is required when lark.apass.apps has multiple entries (or pass appKey explicitly)");
    }
}
