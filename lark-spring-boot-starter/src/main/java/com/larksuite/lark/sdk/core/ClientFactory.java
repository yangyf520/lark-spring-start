package com.larksuite.lark.sdk.core;

import com.larksuite.lark.core.advice.HttpAccessLogger;
import com.lark.oapi.Client;
import com.lark.oapi.core.enums.BaseUrlEnum;
import com.lark.oapi.core.httpclient.OkHttpTransport;
import com.lark.oapi.core.utils.OKHttps;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/** 构建飞书 SDK {@link Client} 的工厂（基于 {@link SdkProperties}）。 */
public final class ClientFactory {

    private ClientFactory() {}

    /** 根据单个 app 配置创建 SDK Client。 */
    public static Client create(SdkProperties.App app) {
        Objects.requireNonNull(app, "app");
        if (app.getAppId() == null || app.getAppId().isBlank()) {
            throw new IllegalArgumentException("appId is blank");
        }
        if (app.getAppSecret() == null || app.getAppSecret().isBlank()) {
            throw new IllegalArgumentException("appSecret is blank");
        }

        Client.Builder builder = Client.newBuilder(app.getAppId(), app.getAppSecret());

        if (Boolean.TRUE.equals(app.getMarketplaceApp())) {
            builder.marketplaceApp();
        }

        if (app.getBaseUrl() != null && !app.getBaseUrl().isBlank()) {
            builder.openBaseUrl(toSdkBaseUrl(app.getBaseUrl()));
        }

        if (Boolean.TRUE.equals(app.getLogReqAtDebug())) {
            builder.logReqAtDebug(true);
        }

        if (app.getRequestTimeoutMs() != null && app.getRequestTimeoutMs() > 0) {
            builder.requestTimeout(app.getRequestTimeoutMs(), TimeUnit.MILLISECONDS);
        }

        if (Boolean.TRUE.equals(app.getDisableTokenCache())) {
            builder.disableTokenCache();
        }

        OkHttpTransport okHttp = (app.getRequestTimeoutMs() != null && app.getRequestTimeoutMs() > 0)
                ? new OkHttpTransport(OKHttps.create(app.getRequestTimeoutMs(), TimeUnit.MILLISECONDS))
                : new OkHttpTransport(OKHttps.defaultClient);
        builder.httpTransport(HttpAccessLogger.wrapOapiTransport(okHttp));

        return builder.build();
    }

    /** 将配置里的 baseUrl 映射为 SDK 枚举。 */
    private static BaseUrlEnum toSdkBaseUrl(String baseUrl) {
        String v = baseUrl == null ? "" : baseUrl.trim().toLowerCase();
        if (v.contains("open.feishu.cn")) {
            return BaseUrlEnum.FeiShu;
        }
        if (v.contains("open.larksuite.com")) {
            return BaseUrlEnum.LarkSuite;
        }
        throw new IllegalArgumentException("Unsupported lark.oapi.base-url: " + baseUrl);
    }
}
