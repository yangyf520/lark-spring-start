package com.larksuite.lark.oapi.spring;

import com.lark.oapi.Client;
import com.lark.oapi.core.enums.BaseUrlEnum;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public final class OapiClientFactory {

    private OapiClientFactory() {}

    public static Client create(OapiProperties.App app) {
        Objects.requireNonNull(app, "app");
        if (app.getAppId() == null || app.getAppId().isBlank()) {
            throw new IllegalArgumentException("appId is blank");
        }
        if (app.getAppSecret() == null || app.getAppSecret().isBlank()) {
            throw new IllegalArgumentException("appSecret is blank");
        }

        Client.Builder builder = Client.newBuilder(app.getAppId(), app.getAppSecret());

        if (app.isMarketplaceApp()) {
            builder.marketplaceApp();
        }

        if (app.getBaseUrl() != null) {
            builder.openBaseUrl(toSdkBaseUrl(app.getBaseUrl()));
        }

        if (Boolean.TRUE.equals(app.getLogReqAtDebug())) {
            builder.logReqAtDebug(true);
        }

        if (app.getRequestTimeoutMs() != null && app.getRequestTimeoutMs() > 0) {
            builder.requestTimeout(app.getRequestTimeoutMs(), TimeUnit.MILLISECONDS);
        }

        if (app.isDisableTokenCache()) {
            builder.disableTokenCache();
        }

        return builder.build();
    }

    private static BaseUrlEnum toSdkBaseUrl(OapiProperties.BaseUrl baseUrl) {
        // Avoid switch-on-enum: javac emits synthetic OapiClientFactory$1; stale incremental jars can omit it.
        if (baseUrl == OapiProperties.BaseUrl.FEISHU) {
            return BaseUrlEnum.FeiShu;
        }
        if (baseUrl == OapiProperties.BaseUrl.LARK_SUITE) {
            return BaseUrlEnum.LarkSuite;
        }
        throw new IllegalArgumentException("Unknown baseUrl: " + baseUrl);
    }
}

