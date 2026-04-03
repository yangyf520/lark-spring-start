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
@EnableConfigurationProperties(SdkProperties.class)
public class SdkAutoConfiguration {

    /** 多应用 Client 注册表（key 对应配置里的 appKey）。 */
    @Bean
    @ConditionalOnMissingBean
    public ClientRegistry clientRegistry(SdkProperties props) {
        Map<String, Client> clients = new LinkedHashMap<>();
        for (Map.Entry<String, SdkProperties.App> e : props.getApps().entrySet()) {
            clients.put(e.getKey(), ClientFactory.create(withDefaults(props, e.getValue())));
        }

        String primary = clients.size() == 1 ? clients.keySet().iterator().next() : null;

        return new ClientRegistry(clients, primary);
    }

    /** 事件回调处理器注册表（按 appKey）。 */
    @Bean
    @ConditionalOnClass(EventDispatcher.class)
    @ConditionalOnMissingBean
    public EventDispatcherRegistry eventDispatcherRegistry(SdkProperties props) {
        Map<String, EventDispatcher> dispatchers = new LinkedHashMap<>();
        for (Map.Entry<String, SdkProperties.App> e : props.getApps().entrySet()) {
            SdkProperties.App app = withDefaults(props, e.getValue());

            String verificationToken = app.getVerificationToken() == null ? "" : app.getVerificationToken();
            String encryptKey = app.getEncryptKey() == null ? "" : app.getEncryptKey();

            EventDispatcher.Builder builder = EventDispatcher.newBuilder(verificationToken, encryptKey);
            dispatchers.put(e.getKey(), builder.build());
        }
        return new EventDispatcherRegistry(dispatchers);
    }

    /** 将 root 默认值补齐到每个 app 配置中。 */
    private static SdkProperties.App withDefaults(SdkProperties root, SdkProperties.App app) {
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
