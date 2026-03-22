package com.larksuite.lark.web;

import com.larksuite.lark.core.common.LarkApi;
import com.larksuite.lark.service.BotService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** 机器人：代理飞书 bot v3 等能力。 */
@LarkApi
@RestController
@ConditionalOnProperty(prefix = "lark.api", name = "enabled", havingValue = "true", matchIfMissing = true)
@RequestMapping(path = "/api/lark/bot", produces = MediaType.APPLICATION_JSON_VALUE)
public class BotController {

    private final BotService botService;

    public BotController(BotService botService) {
        this.botService = botService;
    }

    /** 获取机器人信息：查询当前应用机器人信息，响应 data 为机器人对象。 */
    @GetMapping("/info")
    public Object info(@RequestParam(required = false) String appKey) throws Exception {
        return botService.getBotPayload(appKey);
    }
}
