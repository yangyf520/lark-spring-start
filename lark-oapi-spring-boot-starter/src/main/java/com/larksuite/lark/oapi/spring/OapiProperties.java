package com.larksuite.lark.oapi.spring;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.LinkedHashMap;
import java.util.Map;

@Validated
@ConfigurationProperties(prefix = "lark.oapi")
public class OapiProperties {

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
        private boolean marketplaceApp;

        /**
         * FEISHU or LARK_SUITE.
         */
        private BaseUrl baseUrl = BaseUrl.FEISHU;

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
        private boolean disableTokenCache;

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

        public boolean isMarketplaceApp() {
            return marketplaceApp;
        }

        public void setMarketplaceApp(boolean marketplaceApp) {
            this.marketplaceApp = marketplaceApp;
        }

        public BaseUrl getBaseUrl() {
            return baseUrl;
        }

        public void setBaseUrl(BaseUrl baseUrl) {
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

        public boolean isDisableTokenCache() {
            return disableTokenCache;
        }

        public void setDisableTokenCache(boolean disableTokenCache) {
            this.disableTokenCache = disableTokenCache;
        }
    }

    public enum BaseUrl {
        FEISHU,
        LARK_SUITE
    }
}

