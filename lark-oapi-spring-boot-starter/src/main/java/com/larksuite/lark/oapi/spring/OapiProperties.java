package com.larksuite.lark.oapi.spring;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.LinkedHashMap;
import java.util.Map;

@Validated
@ConfigurationProperties(prefix = "lark.oapi")
public class OapiProperties {

    /**
     * Default OpenAPI base url.
     * Typical values: {@code https://open.feishu.cn} or {@code https://open.larksuite.com}.
     */
    private String baseUrl = "https://open.feishu.cn";

    /**
     * Default whether this is an ISV marketplace app.
     */
    private Boolean marketplaceApp = false;

    /**
     * Default event subscription / callback verification token (optional).
     */
    private String verificationToken;

    /**
     * Default event subscription / callback encrypt key (optional).
     */
    private String encryptKey;

    /**
     * Default request timeout in milliseconds. Null means SDK default.
     */
    private Long requestTimeoutMs;

    /**
     * Default whether to log request/response in debug mode.
     */
    private Boolean logReqAtDebug;

    /**
     * Default disable SDK's automatic tenant access token fetch & cache.
     */
    private Boolean disableTokenCache = false;

    /**
     * Which app key should be considered "primary".
     * If set and present in apps, a primary {@code com.lark.oapi.Client} bean will be exposed.
     */
    private String primary;

    /**
     * Multiple Lark apps keyed by a logical name (e.g. "default", "hr", "finance").
     */
    private Map<String, App> apps = new LinkedHashMap<>();

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

    public Boolean getMarketplaceApp() {
        return marketplaceApp;
    }

    public void setMarketplaceApp(Boolean marketplaceApp) {
        this.marketplaceApp = marketplaceApp;
    }

    public String getVerificationToken() {
        return verificationToken;
    }

    public void setVerificationToken(String verificationToken) {
        this.verificationToken = verificationToken;
    }

    public String getEncryptKey() {
        return encryptKey;
    }

    public void setEncryptKey(String encryptKey) {
        this.encryptKey = encryptKey;
    }

    public Long getRequestTimeoutMs() {
        return requestTimeoutMs;
    }

    public void setRequestTimeoutMs(Long requestTimeoutMs) {
        this.requestTimeoutMs = requestTimeoutMs;
    }

    public Boolean getLogReqAtDebug() {
        return logReqAtDebug;
    }

    public void setLogReqAtDebug(Boolean logReqAtDebug) {
        this.logReqAtDebug = logReqAtDebug;
    }

    public Boolean getDisableTokenCache() {
        return disableTokenCache;
    }

    public void setDisableTokenCache(Boolean disableTokenCache) {
        this.disableTokenCache = disableTokenCache;
    }

    public static class App {
        private String appId;
        private String appSecret;

        /**
         * Event subscription / callback verification token (optional).
         */
        private String verificationToken;

        /**
         * Event subscription / callback encrypt key (optional).
         */
        private String encryptKey;

        /**
         * Whether this is an ISV marketplace app.
         */
        private Boolean marketplaceApp;

        /**
         * OpenAPI base url override. If blank, use {@code lark.oapi.base-url}.
         */
        private String baseUrl;

        /**
         * Request timeout in milliseconds. Null means SDK default.
         */
        private Long requestTimeoutMs;

        /**
         * Whether to log request/response in debug mode.
         */
        private Boolean logReqAtDebug;

        /**
         * Disable SDK's automatic tenant access token fetch & cache.
         * When enabled, callers should pass token via RequestOptions.tenantAccessToken(...).
         */
        private Boolean disableTokenCache;

        public String getAppId() {
            return appId;
        }

        public void setAppId(String appId) {
            this.appId = appId;
        }

        public String getAppSecret() {
            return appSecret;
        }

        public void setAppSecret(String appSecret) {
            this.appSecret = appSecret;
        }

        public String getVerificationToken() {
            return verificationToken;
        }

        public void setVerificationToken(String verificationToken) {
            this.verificationToken = verificationToken;
        }

        public String getEncryptKey() {
            return encryptKey;
        }

        public void setEncryptKey(String encryptKey) {
            this.encryptKey = encryptKey;
        }

        public Boolean getMarketplaceApp() {
            return marketplaceApp;
        }

        public void setMarketplaceApp(Boolean marketplaceApp) {
            this.marketplaceApp = marketplaceApp;
        }

        public String getBaseUrl() {
            return baseUrl;
        }

        public void setBaseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
        }

        public Long getRequestTimeoutMs() {
            return requestTimeoutMs;
        }

        public void setRequestTimeoutMs(Long requestTimeoutMs) {
            this.requestTimeoutMs = requestTimeoutMs;
        }

        public Boolean getLogReqAtDebug() {
            return logReqAtDebug;
        }

        public void setLogReqAtDebug(Boolean logReqAtDebug) {
            this.logReqAtDebug = logReqAtDebug;
        }

        public Boolean getDisableTokenCache() {
            return disableTokenCache;
        }

        public void setDisableTokenCache(Boolean disableTokenCache) {
            this.disableTokenCache = disableTokenCache;
        }
    }
}

