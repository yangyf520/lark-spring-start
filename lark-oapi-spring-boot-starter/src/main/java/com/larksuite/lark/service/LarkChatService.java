package com.larksuite.lark.service;

import com.lark.oapi.Client;
import com.lark.oapi.service.im.v1.enums.CreateChatUserIdTypeEnum;
import com.lark.oapi.service.im.v1.model.CreateChatReq;
import com.lark.oapi.service.im.v1.model.CreateChatReqBody;
import com.lark.oapi.service.im.v1.model.CreateChatResp;
import com.lark.oapi.service.im.v1.model.GetChatReq;
import com.lark.oapi.service.im.v1.model.GetChatResp;
import com.larksuite.lark.oapi.spring.OapiClientRegistry;
import com.larksuite.lark.support.LarkApiExecutor;

/** IM 群：查询与创建会话。 */
public class LarkChatService {

    private final OapiClientRegistry registry;
    private final LarkApiExecutor executor;

    public LarkChatService(OapiClientRegistry registry, LarkApiExecutor executor) {
        this.registry = registry;
        this.executor = executor;
    }

    /** 按 chat_id 获取会话。 */
    public GetChatResp getChat(String appKey, String chatId) throws Exception {
        Client client = resolveClient(appKey);
        GetChatReq req = GetChatReq.newBuilder()
                .chatId(chatId)
                .build();
        return executor.execute(() -> client.im().chat().get(req));
    }

    /** 创建群聊。 */
    public CreateChatResp createChat(String appKey, CreateChatReqBody body) throws Exception {
        Client client = resolveClient(appKey);
        CreateChatReq req = CreateChatReq.newBuilder()
                // SDK v2.4.0 uses `userIdType` (not `idType`) for CreateChatReq
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
