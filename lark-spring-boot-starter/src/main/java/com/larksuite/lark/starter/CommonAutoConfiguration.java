package com.larksuite.lark.starter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lark.oapi.service.contact.v3.model.Department;
import com.lark.oapi.service.contact.v3.model.User;
import com.larksuite.lark.app.service.DepartmentService;
import com.larksuite.lark.app.service.ObjectDataService;
import com.larksuite.lark.app.service.ObjectMetadataService;
import com.larksuite.lark.app.service.UserService;
import com.larksuite.lark.app.core.openapi.AppOpenApiClientRegistry;
import com.larksuite.lark.app.core.openapi.AppOpenApiProperties;
import com.larksuite.lark.common.jackson.DepartmentJsonMixin;
import com.larksuite.lark.common.jackson.UserJsonMixin;
import com.larksuite.lark.sdk.service.message.ImMessageService;
import com.larksuite.lark.sdk.core.OapiClientRegistry;
import com.larksuite.lark.sdk.core.OapiProperties;
import com.larksuite.lark.sdk.service.approval.ApprovalService;
import com.larksuite.lark.sdk.service.auth.AuthService;
import com.larksuite.lark.sdk.service.bitable.BitableService;
import com.larksuite.lark.sdk.service.bot.BotService;
import com.larksuite.lark.sdk.service.calendar.CalendarService;
import com.larksuite.lark.sdk.service.chat.ChatService;
import com.larksuite.lark.sdk.service.contact.ContactService;
import com.larksuite.lark.sdk.service.identity.IdentityService;
import com.larksuite.lark.sdk.service.ops.OpsAlertService;
import com.larksuite.lark.common.support.ApiExecutor;
import com.larksuite.lark.common.support.ClientProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@EnableConfigurationProperties({
        OapiProperties.class,
        ClientProperties.class,
        AppOpenApiProperties.class
})
/** Starter 核心 Bean 注入（SDK + AE OpenAPI）。 */
public class CommonAutoConfiguration {

    /**
     * SDK contact 模型使用 Gson 注解；这里通过 Jackson mixin 兼容飞书 snake_case 字段（如 parent_department_id）。
     */
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer larkSdkDepartmentJsonAliases() {
        return builder -> builder.mixIn(Department.class, DepartmentJsonMixin.class);
    }

    /** User 模型 Jackson 别名，兼容飞书 snake_case 请求体。 */
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer larkSdkUserJsonAliases() {
        return builder -> builder.mixIn(User.class, UserJsonMixin.class);
    }

    /** 认证相关 OpenAPI。 */
    @Bean
    @ConditionalOnMissingBean
    public AuthService authService(OapiClientRegistry registry, OapiProperties properties, ObjectMapper objectMapper) {
        return new AuthService(registry, properties, objectMapper);
    }

    /** 通讯录 contact v3。 */
    @Bean
    @ConditionalOnMissingBean
    public ContactService contactService(OapiClientRegistry registry) {
        return new ContactService(registry);
    }

    /** 包装 OpenAPI 调用的简单重试。 */
    @Bean
    @ConditionalOnMissingBean
    public ApiExecutor apiExecutor(ClientProperties properties) {
        return new ApiExecutor(properties);
    }

    /** 帐号 / 身份接口。 */
    @Bean
    @ConditionalOnMissingBean
    public IdentityService identityService(OapiClientRegistry registry, ApiExecutor executor) {
        return new IdentityService(registry, executor);
    }

    /** 审批。 */
    @Bean
    @ConditionalOnMissingBean
    public ApprovalService approvalService(OapiClientRegistry registry, ApiExecutor executor) {
        return new ApprovalService(registry, executor);
    }

    /** 日历。 */
    @Bean
    @ConditionalOnMissingBean
    public CalendarService calendarService(OapiClientRegistry registry, ApiExecutor executor) {
        return new CalendarService(registry, executor);
    }

    /** 群聊 / IM 部分 API。 */
    @Bean
    @ConditionalOnMissingBean
    public ChatService chatService(OapiClientRegistry registry, ApiExecutor executor) {
        return new ChatService(registry, executor);
    }

    /** IM 消息接口。 */
    @Bean
    @ConditionalOnMissingBean
    public ImMessageService imMessageService(OapiClientRegistry registry, ObjectMapper objectMapper) {
        return new ImMessageService(registry, objectMapper);
    }

    /** 运维告警（依赖群与消息）。 */
    @Bean
    @ConditionalOnMissingBean
    public OpsAlertService opsAlertService(ChatService chatService, ImMessageService imMessageService) {
        return new OpsAlertService(chatService, imMessageService);
    }

    /** 机器人。 */
    @Bean
    @ConditionalOnMissingBean
    public BotService botService(OapiClientRegistry registry, ApiExecutor executor, ObjectMapper objectMapper) {
        return new BotService(registry, executor, objectMapper);
    }

    /** 多维表格。 */
    @Bean
    @ConditionalOnMissingBean
    public BitableService bitableService(OapiClientRegistry registry, ApiExecutor executor) {
        return new BitableService(registry, executor);
    }

    /** 应用开放平台 AE API 多应用客户端。 */
    @Bean
    @ConditionalOnMissingBean
    public AppOpenApiClientRegistry appOpenApiClientRegistry(ObjectMapper objectMapper, AppOpenApiProperties properties) {
        return new AppOpenApiClientRegistry(objectMapper, properties);
    }

    /** AE 部门数据。 */
    @Bean
    @ConditionalOnMissingBean
    public DepartmentService departmentService(AppOpenApiClientRegistry registry) {
        return new DepartmentService(registry);
    }

    /** AE 对象元数据。 */
    @Bean
    @ConditionalOnMissingBean
    public ObjectMetadataService objectMetadataService(AppOpenApiClientRegistry registry) {
        return new ObjectMetadataService(registry);
    }

    /** AE 用户数据。 */
    @Bean
    @ConditionalOnMissingBean
    public UserService userService(AppOpenApiClientRegistry registry) {
        return new UserService(registry);
    }

    /** AE 对象实例数据。 */
    @Bean
    @ConditionalOnMissingBean
    public ObjectDataService objectDataService(AppOpenApiClientRegistry registry) {
        return new ObjectDataService(registry);
    }
}
