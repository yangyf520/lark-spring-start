package com.larksuite.lark.sdk.core;

import com.lark.oapi.Client;
import com.lark.oapi.event.EventDispatcher;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import java.util.LinkedHashMap;
import java.util.Map;

/** 自动装配飞书 SDK 相关核心 Bean。 */
@AutoConfiguration
@ConditionalOnClass(Client.class)
@EnableConfigurationProperties(OapiProperties.class)
public class OapiAutoConfiguration {

    /** 多应用 Client 注册表（key 对应配置里的 appKey）。 */
    @Bean
    @ConditionalOnMissingBean
    public OapiClientRegistry oapiClientRegistry(OapiProperties props) {
        Map<String, Client> clients = new LinkedHashMap<>();
        for (Map.Entry<String, OapiProperties.App> e : props.getApps().entrySet()) {
            clients.put(e.getKey(), OapiClientFactory.create(withDefaults(props, e.getValue())));
        }

        String primary = props.getPrimary();
        if ((primary == null || primary.isBlank()) && clients.size() == 1) {
            primary = clients.keySet().iterator().next();
        }

        return new OapiClientRegistry(clients, primary);
    }

    /** 事件回调处理器注册表（按 appKey）。 */
    @Bean
    @ConditionalOnClass(EventDispatcher.class)
    @ConditionalOnMissingBean
    public OapiEventDispatcherRegistry oapiEventDispatcherRegistry(OapiProperties props) {
        Map<String, EventDispatcher> dispatchers = new LinkedHashMap<>();
        for (Map.Entry<String, OapiProperties.App> e : props.getApps().entrySet()) {
            OapiProperties.App app = withDefaults(props, e.getValue());

            String verificationToken = app.getVerificationToken() == null ? "" : app.getVerificationToken();
            String encryptKey = app.getEncryptKey() == null ? "" : app.getEncryptKey();

            EventDispatcher.Builder builder = EventDispatcher.newBuilder(verificationToken, encryptKey);
            dispatchers.put(e.getKey(), builder.build());
        }
        return new OapiEventDispatcherRegistry(dispatchers);
    }

    /** 将 root 默认值补齐到每个 app 配置中。 */
    private static OapiProperties.App withDefaults(OapiProperties root, OapiProperties.App app) {
        if (app == null) {
            return null;
        }
        if (app.getBaseUrl() == null || app.getBaseUrl().isBlank()) {
            app.setBaseUrl(root.getBaseUrl());
        }
        if (app.getMarketplaceApp() == null) {
            app.setMarketplaceApp(root.getMarketplaceApp());
        }
        if (app.getVerificationToken() == null) {
            app.setVerificationToken(root.getVerificationToken());
        }
        if (app.getEncryptKey() == null) {
            app.setEncryptKey(root.getEncryptKey());
        }
        if (app.getRequestTimeoutMs() == null) {
            app.setRequestTimeoutMs(root.getRequestTimeoutMs());
        }
        if (app.getLogReqAtDebug() == null) {
            app.setLogReqAtDebug(root.getLogReqAtDebug());
        }
        if (app.getDisableTokenCache() == null) {
            app.setDisableTokenCache(root.getDisableTokenCache());
        }
        return app;
    }
}

