package com.larksuite.lark.app.openapi;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.LinkedHashMap;
import java.util.Map;

@ConfigurationProperties(prefix = "lark.apass")
public class AppOpenApiProperties {

    private String baseUrl = "https://ae-openapi.feishu.cn";

    /**
     * Which app key should be considered "primary".
     * If not set, callers should pass appKey explicitly.
     */
    private String primary;

    /**
     * Multiple App OpenAPI credentials keyed by a logical name (e.g. "apass", "hr", "finance").
     */
    private Map<String, App> apps = new LinkedHashMap<>();

    /**
     * App-level OpenAPI properties.
     */
    public static class App {
        /**
         * Optional override. If blank, fall back to {@code lark.apass.base-url}.
         */
        private String baseUrl;

        /**
         * OpenAPI 凭证：ID（ae-openapi 文档里的 OpenAPI ID/Secret）。
         */
        private String id;

        /**
         * OpenAPI 凭证：Secret。
         */
        private String secret;

        /**
         * OpenAPI namespace（用于拼接到请求路径）。
         */
        private String namespace;

        /**
         * Optional pre-issued Authorization token for AE OpenAPI (e.g. "T:xxxx").
         * <p>
         * If set, client will use this token directly and skip calling /auth/v1/appToken.
         */
        private String token;

        /**
         * 获取 appToken 的相对路径（固定为 /auth/v1/appToken）。
         */
        private String appTokenPath = "/auth/v1/appToken";

        public String getBaseUrl() {
            return baseUrl;
        }

        public void setBaseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getSecret() {
            return secret;
        }

        public void setSecret(String secret) {
            this.secret = secret;
        }

        public String getNamespace() {
            return namespace;
        }

        public void setNamespace(String namespace) {
            this.namespace = namespace;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getAppTokenPath() {
            return appTokenPath;
        }

        public void setAppTokenPath(String appTokenPath) {
            this.appTokenPath = appTokenPath;
        }
    }

    public String getPrimary() {
        return primary;
    }

    public void setPrimary(String primary) {
        this.primary = primary;
    }

    public Map<String, App> getApps() {
        return apps;
    }

    public void setApps(Map<String, App> apps) {
        this.apps = apps;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }
}
