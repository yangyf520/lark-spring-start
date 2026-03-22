package com.larksuite.lark.service.chat;

import com.lark.oapi.Client;
import com.lark.oapi.service.im.v1.enums.CreateChatUserIdTypeEnum;
import com.lark.oapi.service.im.v1.model.CreateChatReq;
import com.lark.oapi.service.im.v1.model.CreateChatReqBody;
import com.lark.oapi.service.im.v1.model.CreateChatResp;
import com.lark.oapi.service.im.v1.model.GetChatReq;
import com.lark.oapi.service.im.v1.model.GetChatResp;
import com.larksuite.lark.oapi.spring.OapiClientRegistry;
import com.larksuite.lark.support.ApiExecutor;

/** IM 群：查询与创建会话；返回完整 SDK Resp。 */
public class ChatService {

    private final OapiClientRegistry registry;
    private final ApiExecutor executor;

    public ChatService(OapiClientRegistry registry, ApiExecutor executor) {
        this.registry = registry;
        this.executor = executor;
    }

    public GetChatResp getChat(String appKey, String chatId) throws Exception {
        Client client = resolveClient(appKey);
        GetChatReq req = GetChatReq.newBuilder()
                .chatId(chatId)
                .build();
        return executor.execute(() -> client.im().chat().get(req));
    }

    public CreateChatResp createChat(String appKey, CreateChatReqBody body) throws Exception {
        Client client = resolveClient(appKey);
        CreateChatReq req = CreateChatReq.newBuilder()
                .userIdType(CreateChatUserIdTypeEnum.OPEN_ID)
                .createChatReqBody(body)
                .build();
        return executor.execute(() -> client.im().chat().create(req));
    }

    private Client resolveClient(String appKey) {
        if (appKey == null || appKey.isBlank()) {
            return registry.primary();
        }
        return registry.get(appKey);
    }
}
