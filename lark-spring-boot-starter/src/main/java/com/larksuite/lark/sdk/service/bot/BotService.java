package com.larksuite.lark.sdk.service.bot;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lark.oapi.Client;
import com.lark.oapi.core.response.RawResponse;
import com.lark.oapi.core.token.AccessTokenType;
import com.larksuite.lark.core.exception.SystemException;
import com.larksuite.lark.sdk.core.ClientRegistry;
import com.larksuite.lark.common.support.ApiExecutor;

import java.nio.charset.StandardCharsets;

/** 机器人相关 HTTP：未生成强类型 SDK 时用 {@link Client#get} 调飞书。 */
public class BotService {
    private static final String PATH_BOT_V3_INFO = "/open-apis/bot/v3/info";

    private final ClientRegistry registry;
    private final ApiExecutor executor;
    private final ObjectMapper objectMapper;

    public BotService(ClientRegistry registry, ApiExecutor executor, ObjectMapper objectMapper) {
        this.registry = registry;
        this.executor = executor;
        this.objectMapper = objectMapper;
    }

    /** 调用飞书 GET /open-apis/bot/v3/info，返回 JSON 根节点。 */
    public JsonNode getBotInfo(String appKey) throws Exception {
        Client client = resolveClient(appKey);
        RawResponse raw = executor.execute("bot.v3.info", appKey, "path=" + PATH_BOT_V3_INFO, () ->
                client.get(PATH_BOT_V3_INFO, null, AccessTokenType.Tenant));
        byte[] body = raw.getBody();
        String bodyText = body == null ? "" : new String(body, StandardCharsets.UTF_8);
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

    /**
     * 解析 {@link #getBotInfo(String)} 的 JSON，成功时返回机器人 data（与 HTTP API 一致）；
     * 业务错误码时抛 {@link SystemException}。
     */
    public Object getBotPayload(String appKey) throws Exception {
        JsonNode root = getBotInfo(appKey);
        int code = root.path("code").asInt(0);
        if (code != 0) {
            throw new SystemException(String.valueOf(code), root.path("msg").asText(""));
        }
        JsonNode data = root.get("bot");
        if (data == null || data.isNull()) {
            data = root.get("data");
        }
        if (data == null || data.isNull()) {
            return null;
        }
        return objectMapper.convertValue(data, Object.class);
    }

    private Client resolveClient(String appKey) {
        if (appKey == null || appKey.isBlank()) {
            return registry.primary();
        }
        return registry.get(appKey);
    }
}
