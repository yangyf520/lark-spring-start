package com.larksuite.lark.web;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.larksuite.lark.api.dto.ApiResponse;
import com.larksuite.lark.service.LarkBotService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** 机器人：代理飞书 bot v3 等能力。 */
@RestController
@ConditionalOnProperty(prefix = "lark.api", name = "enabled", havingValue = "true", matchIfMissing = true)
@RequestMapping(path = "/api/lark/bot", produces = MediaType.APPLICATION_JSON_VALUE)
public class BotController {

    private final LarkBotService botService;
    private final ObjectMapper objectMapper;

    public BotController(LarkBotService botService, ObjectMapper objectMapper) {
        this.botService = botService;
        this.objectMapper = objectMapper;
    }

    /** 获取当前应用机器人信息（响应中 payload 在 {@code bot}）。 */
    @GetMapping("/info")
    public ApiResponse info(@RequestParam(required = false) String appKey) {
        try {
            JsonNode root = botService.getBotInfo(appKey);
            int code = root.path("code").asInt(0);
            if (code != 0) {
                return ApiResponse.failure(String.valueOf(code), root.path("msg").asText(""));
            }
            JsonNode data = root.get("bot");
            if (data == null || data.isNull()) {
                data = root.get("data");
            }
            if (data == null || data.isNull()) {
                return ApiResponse.success(null);
            }
            return ApiResponse.success(objectMapper.convertValue(data, Object.class));
        } catch (Exception e) {
            return ApiResponse.failure(e.getClass().getSimpleName(), e.getMessage());
        }
    }
}
