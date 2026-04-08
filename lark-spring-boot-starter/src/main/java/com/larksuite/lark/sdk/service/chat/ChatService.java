package com.larksuite.lark.sdk.service.chat;

import com.lark.oapi.Client;
import com.lark.oapi.service.im.v1.enums.CreateChatUserIdTypeEnum;
import com.lark.oapi.service.im.v1.model.CreateChatMembersReq;
import com.lark.oapi.service.im.v1.model.CreateChatMembersResp;
import com.lark.oapi.service.im.v1.model.CreateChatReq;
import com.lark.oapi.service.im.v1.model.CreateChatReqBody;
import com.lark.oapi.service.im.v1.model.CreateChatResp;
import com.lark.oapi.service.im.v1.model.DeleteChatMembersReq;
import com.lark.oapi.service.im.v1.model.DeleteChatMembersResp;
import com.lark.oapi.service.im.v1.model.GetChatMembersReq;
import com.lark.oapi.service.im.v1.model.GetChatMembersResp;
import com.lark.oapi.service.im.v1.model.GetChatReq;
import com.lark.oapi.service.im.v1.model.GetChatResp;
import com.lark.oapi.service.im.v1.model.IsInChatChatMembersReq;
import com.lark.oapi.service.im.v1.model.IsInChatChatMembersResp;
import com.larksuite.lark.sdk.core.ClientRegistry;
import com.larksuite.lark.common.support.ApiExecutor;

import java.util.Objects;

/** IM 群：查询与创建会话；返回完整 SDK Resp。 */
public class ChatService {

    private final ClientRegistry registry;
    private final ApiExecutor executor;

    public ChatService(ClientRegistry registry, ApiExecutor executor) {
        this.registry = registry;
        this.executor = executor;
    }

    public GetChatResp getChat(String appKey, String chatId) throws Exception {
        Client client = resolveClient(appKey);
        GetChatReq req = GetChatReq.newBuilder()
                .chatId(chatId)
                .build();
        return executor.execute("im.chat.get", appKey, "chatId=" + chatId, () -> client.im().chat().get(req));
    }

    /**
     * 创建群会话。
     *
     * @param userIdType {@code userIdList} 中用户 ID 的类型：{@code open_id}（默认）、{@code user_id}、{@code union_id}
     */
    public CreateChatResp createChat(String appKey, CreateChatReqBody body, String userIdType) throws Exception {
        Client client = resolveClient(appKey);
        CreateChatReq req = CreateChatReq.newBuilder()
                .userIdType(resolveCreateChatUserIdType(userIdType))
                .createChatReqBody(body)
                .build();
        return executor.execute("im.chat.create", appKey, "userIdType=" + (userIdType == null ? "" : userIdType), () -> client.im().chat().create(req));
    }

    public GetChatMembersResp getChatMembers(
            String appKey,
            String chatId,
            String memberIdType,
            Integer pageSize,
            String pageToken
    ) throws Exception {
        if (chatId == null || chatId.isBlank()) {
            throw new IllegalArgumentException("chatId is blank");
        }
        Client client = resolveClient(appKey);
        GetChatMembersReq.Builder b = GetChatMembersReq.newBuilder().chatId(chatId);
        if (memberIdType != null && !memberIdType.isBlank()) {
            b.memberIdType(memberIdType);
        }
        if (pageSize != null) {
            b.pageSize(pageSize);
        }
        if (pageToken != null && !pageToken.isBlank()) {
            b.pageToken(pageToken);
        }
        GetChatMembersReq req = b.build();
        return executor.execute("im.chatMembers.get", appKey, "chatId=" + chatId, () -> client.im().chatMembers().get(req));
    }

    public CreateChatMembersResp createChatMembers(String appKey, CreateChatMembersReq req) throws Exception {
        Objects.requireNonNull(req, "req");
        Client client = resolveClient(appKey);
        return executor.execute("im.chatMembers.create", appKey, "chatId=" + req.getChatId(),
                () -> client.im().chatMembers().create(req));
    }

    public DeleteChatMembersResp deleteChatMembers(String appKey, DeleteChatMembersReq req) throws Exception {
        Objects.requireNonNull(req, "req");
        Client client = resolveClient(appKey);
        return executor.execute("im.chatMembers.delete", appKey, "chatId=" + req.getChatId(),
                () -> client.im().chatMembers().delete(req));
    }

    public IsInChatChatMembersResp isUserInChat(String appKey, String chatId) throws Exception {
        if (chatId == null || chatId.isBlank()) {
            throw new IllegalArgumentException("chatId is blank");
        }
        Client client = resolveClient(appKey);
        IsInChatChatMembersReq req = IsInChatChatMembersReq.newBuilder().chatId(chatId).build();
        return executor.execute("im.chatMembers.isInChat", appKey, "chatId=" + chatId,
                () -> client.im().chatMembers().isInChat(req));
    }

    static CreateChatUserIdTypeEnum resolveCreateChatUserIdType(String userIdType) {
        if (userIdType == null || userIdType.isBlank()) {
            return CreateChatUserIdTypeEnum.OPEN_ID;
        }
        return switch (userIdType.trim().toLowerCase()) {
            case "open_id" -> CreateChatUserIdTypeEnum.OPEN_ID;
            case "user_id" -> CreateChatUserIdTypeEnum.USER_ID;
            case "union_id" -> CreateChatUserIdTypeEnum.UNION_ID;
            default -> throw new IllegalArgumentException(
                    "userIdType must be open_id, user_id, or union_id, got: " + userIdType);
        };
    }

    private Client resolveClient(String appKey) {
        if (appKey == null || appKey.isBlank()) {
            return registry.primary();
        }
        return registry.get(appKey);
    }
}
