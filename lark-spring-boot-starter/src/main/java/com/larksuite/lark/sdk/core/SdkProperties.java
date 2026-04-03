package com.larksuite.lark.sdk.core;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.LinkedHashMap;
import java.util.Map;

@Validated
@ConfigurationProperties(prefix = "lark.oapi")
/** 飞书 SDK 配置（支持多应用）。 */
public class SdkProperties {

    /**
     * 默认 OpenAPI 域名。
     */
    private String baseUrl = "https://open.feishu.cn";

    /**
     * 是否商店应用。
     */
    private Boolean marketplaceApp = false;

    /**
     * 事件回调校验 token（可选）。
     */
    private String verificationToken;

    /**
     * 事件回调加密 key（可选）。
     */
    private String encryptKey;

    /**
     * 请求超时（毫秒）；不填则用 SDK 默认。
     */
    private Long requestTimeoutMs;

    /**
     * DEBUG 时是否打印请求日志。
     */
    private Boolean logReqAtDebug;

    /**
     * 是否禁用 SDK 的自动 token 缓存。
     */
    private Boolean disableTokenCache = false;

    /**
     * 多应用配置（key 为 appKey）。
     */
    private Map<String, App> apps = new LinkedHashMap<>();

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
