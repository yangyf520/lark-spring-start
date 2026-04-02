package com.larksuite.lark.app.core.openapi;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/** 按 appKey 管理 AE OpenAPI 客户端（带本地缓存）。 */
public class AppOpenApiClientRegistry {

    private final ObjectMapper objectMapper;
    private final AppOpenApiProperties properties;
    private final Map<String, AppOpenApiClient> clients = new ConcurrentHashMap<>();

    public AppOpenApiClientRegistry(ObjectMapper objectMapper, AppOpenApiProperties properties) {
        this.objectMapper = objectMapper;
        this.properties = properties;
    }

    /** 获取指定 appKey 的客户端；未传入则按 primary/首个配置兜底。 */
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
        throw new IllegalArgumentException("appKey is required (set lark.apass.primary, or pass appKey explicitly)");
    }
}

