package com.larksuite.lark.starter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lark.oapi.Client;
import com.larksuite.lark.service.message.ImMessageService;
import com.larksuite.lark.oapi.spring.OapiClientRegistry;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@ConditionalOnClass(Client.class)
public class ImAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public ImMessageService imMessageService(OapiClientRegistry registry, ObjectMapper objectMapper) {
        return new ImMessageService(registry, objectMapper);
    }
}

