package com.larksuite.lark.sdk.controller;

import com.lark.oapi.service.im.v1.model.CreateChatReqBody;
import com.lark.oapi.service.im.v1.model.CreateChatResp;
import com.lark.oapi.service.im.v1.model.GetChatResp;
import com.larksuite.lark.common.annotation.LarkApi;
import com.larksuite.lark.sdk.service.chat.ChatService;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
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
@RequestMapping(path = "/lark/chat", produces = MediaType.APPLICATION_JSON_VALUE)
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    /**
     * @param userIdType 可选，与 {@code body.userIdType} 二选一；顶层优先
     * @param body       建群字段 + 可选 {@code userIdType}（见 {@link CreateChatRestBody}）
     */
    public record CreateChatReq(String appKey, String userIdType, @Valid CreateChatRestBody body) {

        /** 拷贝为 SDK body，不包含仅用于本接口的 {@code userIdType}。 */
        public CreateChatReqBody resolvedSdkBody() {
            if (body == null) {
                return null;
            }
            CreateChatReqBody plain = new CreateChatReqBody();
            BeanUtils.copyProperties(body, plain);
            return plain;
        }

        /** 解析后的用户 ID 类型：顶层 {@code userIdType} 优先，否则用 {@code body.userIdType} */
        public String effectiveUserIdType() {
            if (userIdType != null && !userIdType.isBlank()) {
                return userIdType;
            }
            if (body != null && body.getUserIdType() != null && !body.getUserIdType().isBlank()) {
                return body.getUserIdType();
            }
            return null;
        }
    }

    /** 获取会话详情：按 chat_id 查询群会话信息。 */
    @GetMapping("/{chatId}")
    public GetChatResp getChat(@PathVariable String chatId, @RequestParam(required = false) String appKey) throws Exception {
        return chatService.getChat(appKey, chatId);
    }

    /** 创建群会话：创建一个新的群聊会话。 */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public CreateChatResp createChat(@Valid @RequestBody CreateChatReq req) throws Exception {
        if (req.body() == null || req.resolvedSdkBody() == null) {
            throw new IllegalArgumentException("body is required");
        }
        return chatService.createChat(req.appKey(), req.resolvedSdkBody(), req.effectiveUserIdType());
    }
}
