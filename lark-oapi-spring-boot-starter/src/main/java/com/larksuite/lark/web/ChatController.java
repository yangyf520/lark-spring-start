package com.larksuite.lark.web;

import com.lark.oapi.service.im.v1.model.CreateChatReqBody;
import com.lark.oapi.service.im.v1.model.CreateChatResp;
import com.lark.oapi.service.im.v1.model.GetChatResp;
import com.larksuite.lark.core.common.LarkApi;
import com.larksuite.lark.service.chat.ChatService;
import com.larksuite.lark.starter.condition.ConditionalOnStarterRestApi;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** 即时通讯：群会话查询与创建。 */
@LarkApi
@RestController
@ConditionalOnStarterRestApi
@RequestMapping(path = "/api/lark/chat", produces = MediaType.APPLICATION_JSON_VALUE)
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    public record CreateChatReq(String appKey, @Valid CreateChatReqBody body) {}

    /** 获取会话详情：按 chat_id 查询群会话信息。 */
    @GetMapping("/{chatId}")
    public GetChatResp getChat(@PathVariable String chatId, @RequestParam(required = false) String appKey) throws Exception {
        return chatService.getChat(appKey, chatId);
    }

    /** 创建群会话：创建一个新的群聊会话。 */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public CreateChatResp createChat(@Valid @RequestBody CreateChatReq req) throws Exception {
        if (req.body() == null) {
            throw new IllegalArgumentException("body is required");
        }
        return chatService.createChat(req.appKey(), req.body());
    }
}
