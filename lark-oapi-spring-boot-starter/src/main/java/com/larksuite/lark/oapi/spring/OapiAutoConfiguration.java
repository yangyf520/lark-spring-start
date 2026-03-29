package com.larksuite.lark.oapi.spring;

import com.lark.oapi.Client;
import com.lark.oapi.event.EventDispatcher;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@AutoConfiguration
@ConditionalOnClass(Client.class)
@EnableConfigurationProperties(OapiProperties.class)
public class OapiAutoConfiguration {

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

    /**
     * Expose a {@link Client} as primary bean when primary app is determined.
     * Useful for "single app" scenarios so callers can directly autowire Client.
     */
    @Bean
    @Primary
    @ConditionalOnMissingBean(Client.class)
    @ConditionalOnProperty(prefix = "lark.oapi", name = "primary")
    public Client primaryOapiClient(OapiClientRegistry registry) {
        return registry.primary();
    }

    @Bean
    @ConditionalOnClass(EventDispatcher.class)
    @ConditionalOnMissingBean
    public OapiEventDispatcherRegistry oapiEventDispatcherRegistry(
            OapiProperties props,
            List<OapiEventDispatcherCustomizer> customizers
    ) {
        Map<String, EventDispatcher> dispatchers = new LinkedHashMap<>();
        for (Map.Entry<String, OapiProperties.App> e : props.getApps().entrySet()) {
            String appKey = e.getKey();
            OapiProperties.App app = withDefaults(props, e.getValue());

            String verificationToken = app.getVerificationToken() == null ? "" : app.getVerificationToken();
            String encryptKey = app.getEncryptKey() == null ? "" : app.getEncryptKey();

            EventDispatcher.Builder builder = EventDispatcher.newBuilder(verificationToken, encryptKey);
            for (OapiEventDispatcherCustomizer customizer : customizers) {
                customizer.customize(appKey, builder);
            }
            dispatchers.put(appKey, builder.build());
        }
        return new OapiEventDispatcherRegistry(dispatchers);
    }

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

