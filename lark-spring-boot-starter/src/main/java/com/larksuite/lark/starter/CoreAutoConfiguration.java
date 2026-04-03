package com.larksuite.lark.starter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lark.oapi.service.contact.v3.model.Department;
import com.lark.oapi.service.contact.v3.model.User;
import com.larksuite.lark.app.service.DepartmentService;
import com.larksuite.lark.app.service.ObjectDataService;
import com.larksuite.lark.app.service.ObjectMetadataService;
import com.larksuite.lark.app.service.UserService;
import com.larksuite.lark.app.core.api.AppApiClientRegistry;
import com.larksuite.lark.app.core.api.AppApiProperties;
import com.larksuite.lark.common.jackson.DepartmentJsonMixin;
import com.larksuite.lark.common.jackson.UserJsonMixin;
import com.larksuite.lark.sdk.service.message.ImMessageService;
import com.larksuite.lark.sdk.core.ClientRegistry;
import com.larksuite.lark.sdk.core.SdkProperties;
import com.larksuite.lark.sdk.service.approval.ApprovalService;
import com.larksuite.lark.sdk.service.auth.AuthService;
import com.larksuite.lark.sdk.service.auth.LarkOAuthService;
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
        SdkProperties.class,
        ClientProperties.class,
        AppApiProperties.class
})
/** Starter 核心 Bean 注入（SDK + AE API）。 */
public class CoreAutoConfiguration {

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
    public AuthService authService(ClientRegistry registry, SdkProperties properties, ObjectMapper objectMapper, ApiExecutor executor) {
        return new AuthService(registry, properties, objectMapper, executor);
    }

    /**
     * 占位：保证 {@link LarkOAuthService} 可注入、应用能启动；{@link LarkOAuthService#onAuthorized} 默认实现会在 GET /lark/auth/authorize 时抛错。
     * 宿主提供自定义 {@link LarkOAuthService} Bean 时覆盖本 Bean。
     */
    @Bean
    @ConditionalOnMissingBean(LarkOAuthService.class)
    public LarkOAuthService larkOAuthService() {
        return new LarkOAuthService() {};
    }

    /** 通讯录 contact v3。 */
    @Bean
    @ConditionalOnMissingBean
    public ContactService contactService(ClientRegistry registry, ApiExecutor executor) {
        return new ContactService(registry, executor);
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
    public IdentityService identityService(ClientRegistry registry, ApiExecutor executor) {
        return new IdentityService(registry, executor);
    }

    /** 审批。 */
    @Bean
    @ConditionalOnMissingBean
    public ApprovalService approvalService(ClientRegistry registry, ApiExecutor executor) {
        return new ApprovalService(registry, executor);
    }

    /** 日历。 */
    @Bean
    @ConditionalOnMissingBean
    public CalendarService calendarService(ClientRegistry registry, ApiExecutor executor) {
        return new CalendarService(registry, executor);
    }

    /** 群聊 / IM 部分 API。 */
    @Bean
    @ConditionalOnMissingBean
    public ChatService chatService(ClientRegistry registry, ApiExecutor executor) {
        return new ChatService(registry, executor);
    }

    /** IM 消息接口。 */
    @Bean
    @ConditionalOnMissingBean
    public ImMessageService imMessageService(ClientRegistry registry, ObjectMapper objectMapper, ApiExecutor executor) {
        return new ImMessageService(registry, objectMapper, executor);
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
    public BotService botService(ClientRegistry registry, ApiExecutor executor, ObjectMapper objectMapper) {
        return new BotService(registry, executor, objectMapper);
    }

    /** 多维表格。 */
    @Bean
    @ConditionalOnMissingBean
    public BitableService bitableService(ClientRegistry registry, ApiExecutor executor) {
        return new BitableService(registry, executor);
    }

    /** 应用引擎 AE API 多应用客户端。 */
    @Bean
    @ConditionalOnMissingBean
    public AppApiClientRegistry appApiClientRegistry(ObjectMapper objectMapper, AppApiProperties properties) {
        return new AppApiClientRegistry(objectMapper, properties);
    }

    /** AE 部门数据。 */
    @Bean
    @ConditionalOnMissingBean
    public DepartmentService departmentService(AppApiClientRegistry registry) {
        return new DepartmentService(registry);
    }

    /** AE 对象元数据。 */
    @Bean
    @ConditionalOnMissingBean
    public ObjectMetadataService objectMetadataService(AppApiClientRegistry registry) {
        return new ObjectMetadataService(registry);
    }

    /** AE 用户数据。 */
    @Bean
    @ConditionalOnMissingBean
    public UserService userService(AppApiClientRegistry registry) {
        return new UserService(registry);
    }

    /** AE 对象实例数据。 */
    @Bean
    @ConditionalOnMissingBean
    public ObjectDataService objectDataService(AppApiClientRegistry registry) {
        return new ObjectDataService(registry);
    }
}
