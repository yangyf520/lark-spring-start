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

/**
 * 即时通讯：群会话查询与创建。
 * <p>
 * 成功与异常由全局 Advice 与 Service 统一处理。
 */
@LarkApi
@RestController
@RequestMapping(path = "/lark/chat", produces = MediaType.APPLICATION_JSON_VALUE)
public class ChatController {

    private final ChatService chatService;

    /**
     * 构造注入。
     * <p>
     * @param chatService 会话服务
     */
    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    /**
     * 创建群会话请求体。
     * <p>
     * @param appKey    应用配置键，可空（使用 primary）
     * @param userIdType 用户 ID 类型；与 {@code body.userIdType} 二选一，顶层优先
     * @param body      建群字段 + 可选 {@code userIdType}（见 {@link CreateChatRestBody}），必填
     */
    public record CreateChatReq(String appKey, String userIdType, @Valid CreateChatRestBody body) {

        /**
         * 拷贝为 SDK 请求体（不含仅用于本接口的 {@code userIdType}）。
         * <p>
         * @return SDK {@link CreateChatReqBody}，{@code body} 为空时为 null
         */
        public CreateChatReqBody resolvedSdkBody() {
            if (body == null) {
                return null;
            }
            CreateChatReqBody plain = new CreateChatReqBody();
            BeanUtils.copyProperties(body, plain);
            return plain;
        }

        /**
         * 解析用户 ID 类型：顶层 {@code userIdType} 优先，否则 {@code body.userIdType}。
         * <p>
         * @return 非空时为用户 ID 类型字符串
         */
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

    /**
     * 按 chat_id 查询群会话信息。
     * <p>
     * @param chatId 会话 ID
     * @param appKey 应用配置键，可空（使用 primary）
     * @return 飞书 SDK {@link GetChatResp}
     */
    @GetMapping("/{chatId}")
    public GetChatResp getChat(@PathVariable String chatId, @RequestParam(required = false) String appKey) throws Exception {
        return chatService.getChat(appKey, chatId);
    }

    /**
     * 创建群聊会话。
     * <p>
     * @param req 请求体，{@code body} 必填
     * @return 飞书 SDK {@link CreateChatResp}
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public CreateChatResp createChat(@Valid @RequestBody CreateChatReq req) throws Exception {
        if (req.body() == null || req.resolvedSdkBody() == null) {
            throw new IllegalArgumentException("body is required");
        }
        return chatService.createChat(req.appKey(), req.resolvedSdkBody(), req.effectiveUserIdType());
    }
}
