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
        return switch (baseUrl) {
            case FEISHU -> BaseUrlEnum.FeiShu;
            case LARK_SUITE -> BaseUrlEnum.LarkSuite;
        };
    }
}

