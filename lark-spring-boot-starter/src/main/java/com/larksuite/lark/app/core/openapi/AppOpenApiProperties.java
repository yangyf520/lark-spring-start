package com.larksuite.lark.app.core.openapi;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.LinkedHashMap;
import java.util.Map;

@ConfigurationProperties(prefix = "lark.apass")
/** AE OpenAPI 配置（支持多应用）。 */
public class AppOpenApiProperties {

    private String baseUrl = "https://ae-openapi.feishu.cn";

    /** 默认使用的 appKey（不传 appKey 时兜底）。 */
    private String primary;

    /** 多套 AE OpenAPI 配置（key 为业务侧自定义别名）。 */
    private Map<String, App> apps = new LinkedHashMap<>();

    /** 单个应用的 AE OpenAPI 配置。 */
    public static class App {
        /** 可选覆盖；为空则使用全局 baseUrl。 */
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

        /** 可选预置 token；配置后将直接使用，不再调用 /auth/v1/appToken。 */
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
