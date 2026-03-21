package com.larksuite.lark.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lark.oapi.Client;
import com.lark.oapi.core.response.RawResponse;
import com.lark.oapi.core.token.AccessTokenType;
import com.larksuite.lark.oapi.spring.OapiClientRegistry;
import com.larksuite.lark.support.LarkApiExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;

/** 机器人相关 HTTP：未生成强类型 SDK 时用 {@link Client#get} 调飞书。 */
public class LarkBotService {

    private static final Logger log = LoggerFactory.getLogger(LarkBotService.class);
    private static final String PATH_BOT_V3_INFO = "/open-apis/bot/v3/info";

    private final OapiClientRegistry registry;
    private final LarkApiExecutor executor;
    private final ObjectMapper objectMapper;

    public LarkBotService(OapiClientRegistry registry, LarkApiExecutor executor, ObjectMapper objectMapper) {
        this.registry = registry;
        this.executor = executor;
        this.objectMapper = objectMapper;
    }

    /** 调用飞书 GET /open-apis/bot/v3/info，返回 JSON 根节点。 */
    public JsonNode getBotInfo(String appKey) throws Exception {
        Client client = resolveClient(appKey);
        log.info("Calling Feishu bot info API, path={}, appKey={}",
                PATH_BOT_V3_INFO, appKey == null || appKey.isBlank() ? "<primary>" : appKey);
        RawResponse raw = executor.execute(() ->
                client.get(PATH_BOT_V3_INFO, null, AccessTokenType.Tenant));
        byte[] body = raw.getBody();
        String bodyText = body == null ? "" : new String(body, StandardCharsets.UTF_8);
        log.info("Feishu bot info response, status={}, requestId={}, body={}",
                raw.getStatusCode(), raw.getRequestID(), bodyText);
        if (body == null || body.length == 0) {
            throw new IllegalStateException("empty response body, http=" + raw.getStatusCode()
                    + ", requestId=" + raw.getRequestID());
        }
        if (raw.getStatusCode() < 200 || raw.getStatusCode() >= 300) {
            throw new IllegalStateException("http " + raw.getStatusCode() + ": "
                    + bodyText + ", requestId=" + raw.getRequestID());
        }
        return objectMapper.readTree(body);
    }

    private Client resolveClient(String appKey) {
        if (appKey == null || appKey.isBlank()) {
            return registry.primary();
        }
        return registry.get(appKey);
    }
}
