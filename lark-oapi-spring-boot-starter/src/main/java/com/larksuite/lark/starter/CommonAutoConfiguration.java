package com.larksuite.lark.starter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.larksuite.lark.core.token.TenantAccessTokenProvider;
import com.larksuite.lark.im.ImMessageService;
import com.larksuite.lark.oapi.spring.OapiClientRegistry;
import com.larksuite.lark.oapi.spring.OapiProperties;
import com.larksuite.lark.oapi.spring.StarterApiProperties;
import com.larksuite.lark.service.approval.ApprovalService;
import com.larksuite.lark.service.auth.AuthService;
import com.larksuite.lark.service.bitable.BitableService;
import com.larksuite.lark.service.bot.BotService;
import com.larksuite.lark.service.calendar.CalendarService;
import com.larksuite.lark.service.chat.ChatService;
import com.larksuite.lark.service.contact.ContactService;
import com.larksuite.lark.service.identity.IdentityService;
import com.larksuite.lark.service.ops.OpsAlertService;
import com.larksuite.lark.support.ApiExecutor;
import com.larksuite.lark.support.ClientProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import java.net.http.HttpClient;
import java.time.Duration;

@AutoConfiguration
@EnableConfigurationProperties({OapiProperties.class, StarterApiProperties.class, ClientProperties.class})
public class CommonAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public TenantAccessTokenProvider tenantAccessTokenProvider(
            OapiProperties oapiProperties,
            ObjectMapper objectMapper
    ) {
        HttpClient httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(15))
                .build();
        return new TenantAccessTokenProvider(httpClient, oapiProperties, objectMapper);
    }

    @Bean
    @ConditionalOnMissingBean
    public AuthService authService(OapiClientRegistry registry, OapiProperties properties) {
        return new AuthService(registry, properties);
    }

    @Bean
    @ConditionalOnMissingBean
    public ContactService contactService(OapiClientRegistry registry) {
        return new ContactService(registry);
    }

    @Bean
    @ConditionalOnMissingBean
    public ApiExecutor apiExecutor(ClientProperties properties) {
        return new ApiExecutor(properties);
    }

    @Bean
    @ConditionalOnMissingBean
    public IdentityService identityService(OapiClientRegistry registry, ApiExecutor executor) {
        return new IdentityService(registry, executor);
    }

    @Bean
    @ConditionalOnMissingBean
    public ApprovalService approvalService(OapiClientRegistry registry, ApiExecutor executor) {
        return new ApprovalService(registry, executor);
    }

    @Bean
    @ConditionalOnMissingBean
    public CalendarService calendarService(OapiClientRegistry registry, ApiExecutor executor) {
        return new CalendarService(registry, executor);
    }

    @Bean
    @ConditionalOnMissingBean
    public ChatService chatService(OapiClientRegistry registry, ApiExecutor executor) {
        return new ChatService(registry, executor);
    }

    @Bean
    @ConditionalOnMissingBean
    public OpsAlertService opsAlertService(ChatService chatService, ImMessageService imMessageService) {
        return new OpsAlertService(chatService, imMessageService);
    }

    @Bean
    @ConditionalOnMissingBean
    public BotService botService(OapiClientRegistry registry, ApiExecutor executor, ObjectMapper objectMapper) {
        return new BotService(registry, executor, objectMapper);
    }

    @Bean
    @ConditionalOnMissingBean
    public BitableService bitableService(OapiClientRegistry registry, ApiExecutor executor) {
        return new BitableService(registry, executor);
    }
}
