package com.larksuite.lark.web;

import com.lark.oapi.service.im.v1.model.CreateChatReqBody;
import com.larksuite.lark.api.dto.ApiResponse;
import com.larksuite.lark.service.LarkChatService;
import jakarta.validation.Valid;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** 即时通讯：群会话查询与创建。 */
@RestController
@ConditionalOnProperty(prefix = "lark.api", name = "enabled", havingValue = "true", matchIfMissing = true)
@RequestMapping(path = "/api/lark/chat", produces = MediaType.APPLICATION_JSON_VALUE)
public class ChatController {

    private final LarkChatService chatService;

    public ChatController(LarkChatService chatService) {
        this.chatService = chatService;
    }

    public record CreateChatReq(String appKey, @Valid CreateChatReqBody body) {}

    /** 按 chat_id 获取会话信息。 */
    @GetMapping("/{chatId}")
    public ApiResponse getChat(@PathVariable String chatId, @RequestParam(required = false) String appKey) {
        try {
            var resp = chatService.getChat(appKey, chatId);
            if (!resp.success()) {
                return ApiResponse.failure(String.valueOf(resp.getCode()), resp.getMsg());
            }
            return ApiResponse.success(resp.getData());
        } catch (Exception e) {
            return ApiResponse.failure(e.getClass().getSimpleName(), e.getMessage());
        }
    }

    /** 创建群会话。 */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse createChat(@Valid @RequestBody CreateChatReq req) {
        if (req.body() == null) {
            return ApiResponse.failure("INVALID_ARGUMENT", "body is required");
        }
        try {
            var resp = chatService.createChat(req.appKey(), req.body());
            if (!resp.success()) {
                return ApiResponse.failure(String.valueOf(resp.getCode()), resp.getMsg());
            }
            return ApiResponse.success(resp.getData());
        } catch (Exception e) {
            return ApiResponse.failure(e.getClass().getSimpleName(), e.getMessage());
        }
    }
}
