package com.larksuite.lark.backend.config;

import com.larksuite.lark.common.annotation.LarkApi;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.BooleanSchema;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.responses.ApiResponses;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.HandlerMethod;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI appOpenApi() {
        return new OpenAPI().info(new Info()
                .title("Sense Lark API")
                .version("v1")
                .description(
                        "分组 **lark** 下接口：正常返回飞书 SDK 的 `*Resp`；异常时返回统一错误 envelope。"
                                + "本页通过 SpringDoc 定制把 200 响应 schema 的 **data** 对齐到 SDK `data` 业务载荷。"
                ));
    }

    /** 仅 @LarkApi：将 200 的 JSON schema 包一层固定 envelope，文档里 data 对齐到 SDK `data` 业务载荷。 */
    @Bean
    public OperationCustomizer larkApiResponseEnvelopeCustomizer() {
        return (operation, handlerMethod) -> {
            if (!isLarkApi(handlerMethod)) {
                return operation;
            }
            ApiResponses responses = operation.getResponses();
            if (responses == null) {
                return operation;
            }
            io.swagger.v3.oas.models.responses.ApiResponse r200 = responses.get("200");
            if (r200 == null) {
                return operation;
            }
            Content content = r200.getContent();
            if (content == null) {
                return operation;
            }
            MediaType json = content.get("application/json");
            if (json == null) {
                return operation;
            }
            Schema<?> inner = json.getSchema();
            if (inner == null) {
                return operation;
            }
            Schema<?> dataSchema = envelopeDataSchema(inner);
            ObjectSchema envelope = new ObjectSchema();
            envelope.addProperty("ok", new BooleanSchema());
            envelope.addProperty("time", new StringSchema());
            envelope.addProperty("data", dataSchema);
            envelope.addProperty("error", new StringSchema());
            envelope.addProperty("message", new StringSchema());
            json.setSchema(envelope);
            return operation;
        };
    }

    private static boolean isLarkApi(HandlerMethod handlerMethod) {
        return handlerMethod.getBeanType().isAnnotationPresent(LarkApi.class);
    }

    /** 若内层 schema 已有 `data`，文档只展示业务载荷，与 SDK `data` 含义对齐。 */
    private static Schema<?> envelopeDataSchema(Schema<?> inner) {
        if (inner instanceof ObjectSchema os && os.getProperties() != null) {
            Schema<?> data = os.getProperties().get("data");
            if (data != null) {
                return data;
            }
        }
        return inner;
    }

    @Bean
    public GroupedOpenApi allApis() {
        return GroupedOpenApi.builder()
                .group("all")
                .pathsToMatch("/admin/**", "/lark/**")
                .build();
    }

    @Bean
    public GroupedOpenApi adminApis() {
        return GroupedOpenApi.builder()
                .group("admin")
                .pathsToMatch("/admin/**")
                .build();
    }

    @Bean
    public GroupedOpenApi larkApis() {
        return GroupedOpenApi.builder()
                .group("lark")
                .pathsToMatch("/lark/**")
                .build();
    }
}
