package com.larksuite.lark.sdk.controller;

import com.larksuite.lark.common.annotation.LarkApi;
import com.larksuite.lark.sdk.service.bot.BotService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 机器人：代理飞书 bot v3 等能力。
 * <p>
 * 成功与异常由全局 Advice 与 Service 统一处理。
 */
@LarkApi
@RestController
@RequestMapping(path = "/lark/bot", produces = MediaType.APPLICATION_JSON_VALUE)
public class BotController {

    private final BotService botService;

    /**
     * 构造注入。
     * <p>
     * @param botService 机器人服务
     */
    public BotController(BotService botService) {
        this.botService = botService;
    }

    /**
     * 查询当前应用机器人信息。
     * <p>
     * @param appKey 应用配置键，可空（使用 primary）
     * @return 飞书 SDK 业务载荷（由全局 Advice 包装）
     */
    @GetMapping("/info")
    public Object info(@RequestParam(required = false) String appKey) throws Exception {
        return botService.getBotPayload(appKey);
    }
}
