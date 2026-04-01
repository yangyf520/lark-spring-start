package com.larksuite.lark.service.ops;

import com.lark.oapi.service.im.v1.enums.ReceiveIdTypeEnum;
import com.lark.oapi.service.im.v1.model.CreateChatReqBody;
import com.lark.oapi.service.im.v1.model.CreateChatResp;
import com.lark.oapi.service.im.v1.model.CreateMessageResp;
import com.larksuite.lark.service.message.ImMessageService;
import com.larksuite.lark.service.chat.ChatService;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

/**
 * 运维告警：新建群并发送首条文本，或向已有群发送一条文本（成员 ID 类型由 {@code userIdType} 指定，默认 {@code open_id}）。
 */
public class OpsAlertService {

    private final ChatService chatService;
    private final ImMessageService imMessageService;

    public OpsAlertService(ChatService chatService, ImMessageService imMessageService) {
        this.chatService = chatService;
        this.imMessageService = imMessageService;
    }

    public OpsAlertResult sendOpsAlert(OpsAlertRequest req) throws Exception {
        if (req.alertText() == null || req.alertText().isBlank()) {
            throw new IllegalArgumentException("alertText is required");
        }

        String chatId;
        boolean created;
        if (req.chatId() != null && !req.chatId().isBlank()) {
            chatId = req.chatId().trim();
            created = false;
        } else {
            if (req.chatName() == null || req.chatName().isBlank()) {
                throw new IllegalArgumentException("chatName is required when chatId is omitted");
            }
            List<String> members = req.memberOpenIds() == null ? List.of() : req.memberOpenIds();
            if (members.isEmpty()) {
                throw new IllegalArgumentException("memberOpenIds is required when creating a chat");
            }
            CreateChatReqBody.Builder body = CreateChatReqBody.newBuilder()
                    .name(req.chatName().trim())
                    .userIdList(members.toArray(String[]::new));
            if (req.description() != null && !req.description().isBlank()) {
                body.description(req.description().trim());
            }
            if (req.botOpenIds() != null && !req.botOpenIds().isEmpty()) {
                body.botIdList(req.botOpenIds().toArray(String[]::new));
            }
            CreateChatResp createResp = chatService.createChat(req.appKey(), body.build(), req.userIdType());
            if (!createResp.success() || createResp.getData() == null || createResp.getData().getChatId() == null) {
                throw new IllegalStateException("create chat failed: " + createResp.getCode() + " " + createResp.getMsg());
            }
            chatId = createResp.getData().getChatId();
            created = true;
        }

        CreateMessageResp msgResp = imMessageService.sendText(
                req.appKey(),
                ReceiveIdTypeEnum.CHAT_ID,
                chatId,
                req.alertText()
        );
        if (!msgResp.success() || msgResp.getData() == null) {
            throw new IllegalStateException("send message failed: " + msgResp.getCode() + " " + msgResp.getMsg());
        }
        String messageId = msgResp.getData().getMessageId();
        return new OpsAlertResult(chatId, messageId, created);
    }

    public record OpsAlertRequest(
            String appKey,
            String chatId,
            String chatName,
            /** 成员用户 ID 列表，类型由 {@link #userIdType} 决定 */
            List<String> memberOpenIds,
            List<String> botOpenIds,
            String description,
            /** 可选：{@code open_id}（默认）、{@code user_id}、{@code union_id} */
            String userIdType,
            @NotBlank String alertText
    ) {}

    public record OpsAlertResult(String chatId, String messageId, boolean chatCreated) {}
}
