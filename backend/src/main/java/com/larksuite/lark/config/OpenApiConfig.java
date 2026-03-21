package com.larksuite.lark.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI appOpenApi() {
        return new OpenAPI().info(new Info()
                .title("Sense Lark API")
                .version("v1")
                .description("Zero-intrusion OpenAPI docs for backend and starter endpoints"));
    }

    @Bean
    public GroupedOpenApi allApis() {
        return GroupedOpenApi.builder()
                .group("all")
                .pathsToMatch("/api/**")
                .build();
    }

    @Bean
    public GroupedOpenApi adminApis() {
        return GroupedOpenApi.builder()
                .group("admin")
                .pathsToMatch("/api/admin/**")
                .build();
    }

    @Bean
    public GroupedOpenApi larkApis() {
        return GroupedOpenApi.builder()
                .group("lark")
                .pathsToMatch("/api/lark/**")
                .build();
    }
}
