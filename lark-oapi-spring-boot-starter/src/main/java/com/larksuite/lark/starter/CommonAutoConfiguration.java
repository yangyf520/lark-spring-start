package com.larksuite.lark.starter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.larksuite.lark.core.token.TenantAccessTokenProvider;
import com.larksuite.lark.oapi.spring.LarkWebProperties;
import com.larksuite.lark.oapi.spring.OapiClientRegistry;
import com.larksuite.lark.oapi.spring.OapiProperties;
import com.larksuite.lark.service.LarkApprovalService;
import com.larksuite.lark.service.LarkAuthService;
import com.larksuite.lark.service.LarkBotService;
import com.larksuite.lark.service.LarkCalendarService;
import com.larksuite.lark.service.LarkChatService;
import com.larksuite.lark.service.LarkContactService;
import com.larksuite.lark.service.LarkIdentityService;
import com.larksuite.lark.support.LarkApiExecutor;
import com.larksuite.lark.support.LarkClientProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import java.net.http.HttpClient;
import java.time.Duration;

@AutoConfiguration
@EnableConfigurationProperties({OapiProperties.class, LarkWebProperties.class, LarkClientProperties.class})
public class CommonAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public HttpClient httpClient() {
        return HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(15))
                .build();
    }

    @Bean
    @ConditionalOnMissingBean
    public TenantAccessTokenProvider tenantAccessTokenProvider(
            HttpClient httpClient,
            OapiProperties oapiProperties,
            ObjectMapper objectMapper
    ) {
        return new TenantAccessTokenProvider(httpClient, oapiProperties, objectMapper);
    }

    @Bean
    @ConditionalOnMissingBean
    public LarkAuthService larkAuthService(OapiClientRegistry registry, OapiProperties properties) {
        return new LarkAuthService(registry, properties);
    }

    @Bean
    @ConditionalOnMissingBean
    public LarkContactService larkContactService(OapiClientRegistry registry) {
        return new LarkContactService(registry);
    }

    @Bean
    @ConditionalOnMissingBean
    public LarkApiExecutor larkApiExecutor(LarkClientProperties properties) {
        return new LarkApiExecutor(properties);
    }

    @Bean
    @ConditionalOnMissingBean
    public LarkIdentityService larkIdentityService(OapiClientRegistry registry, LarkApiExecutor executor) {
        return new LarkIdentityService(registry, executor);
    }

    @Bean
    @ConditionalOnMissingBean
    public LarkApprovalService larkApprovalService(OapiClientRegistry registry, LarkApiExecutor executor) {
        return new LarkApprovalService(registry, executor);
    }

    @Bean
    @ConditionalOnMissingBean
    public LarkCalendarService larkCalendarService(OapiClientRegistry registry, LarkApiExecutor executor) {
        return new LarkCalendarService(registry, executor);
    }

    @Bean
    @ConditionalOnMissingBean
    public LarkChatService larkChatService(OapiClientRegistry registry, LarkApiExecutor executor) {
        return new LarkChatService(registry, executor);
    }

    @Bean
    @ConditionalOnMissingBean
    public LarkBotService larkBotService(OapiClientRegistry registry, LarkApiExecutor executor, ObjectMapper objectMapper) {
        return new LarkBotService(registry, executor, objectMapper);
    }
}

