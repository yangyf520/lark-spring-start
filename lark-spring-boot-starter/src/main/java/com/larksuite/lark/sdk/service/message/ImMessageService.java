package com.larksuite.lark.sdk.service.message;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lark.oapi.Client;
import com.lark.oapi.service.im.v1.enums.MsgTypeEnum;
import com.lark.oapi.service.im.v1.enums.ReceiveIdTypeEnum;
import com.lark.oapi.service.im.v1.model.CreateMessageReq;
import com.lark.oapi.service.im.v1.model.CreateMessageReqBody;
import com.lark.oapi.service.im.v1.model.CreateMessageResp;
import com.lark.oapi.service.im.v1.model.UpdateMessageReq;
import com.lark.oapi.service.im.v1.model.UpdateMessageReqBody;
import com.lark.oapi.service.im.v1.model.UpdateMessageResp;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.larksuite.lark.common.support.ApiExecutor;
import com.larksuite.lark.sdk.core.ClientRegistry;

import java.util.Map;
import java.util.Objects;

/** 以应用身份调用 IM v1：发文本、卡片、更新消息；返回完整 SDK Resp。 */
public class ImMessageService {

    private final ClientRegistry registry;
    private final ObjectMapper objectMapper;
    private final ApiExecutor executor;

    public ImMessageService(ClientRegistry registry, ObjectMapper objectMapper, ApiExecutor executor) {
        this.registry = registry;
        this.objectMapper = objectMapper;
        this.executor = executor;
    }

    public CreateMessageResp sendText(String appKey, ReceiveIdTypeEnum receiveIdType, String receiveId, String text) throws Exception {
        Objects.requireNonNull(receiveIdType, "receiveIdType");
        if (receiveId == null || receiveId.isBlank()) {
            throw new IllegalArgumentException("receiveId is blank");
        }
        if (text == null) {
            text = "";
        }

        Client client = (appKey == null || appKey.isBlank()) ? registry.primary() : registry.get(appKey);

        String contentJson = objectMapper.createObjectNode()
                .put("text", text)
                .toString();

        CreateMessageReq req = CreateMessageReq.newBuilder()
                .receiveIdType(receiveIdType.getValue())
                .createMessageReqBody(CreateMessageReqBody.newBuilder()
                        .receiveId(receiveId)
                        .msgType(MsgTypeEnum.MSG_TYPE_TEXT.getValue())
                        .content(contentJson)
                        .build())
                .build();

        return executor.execute("im.v1.message.create", appKey,
                "msgType=text,receiveIdType=" + receiveIdType + ",receiveId=" + receiveId,
                () -> client.im().message().create(req));
    }

    public CreateMessageResp sendCard(String appKey, ReceiveIdTypeEnum receiveIdType, String receiveId, String cardJson) throws Exception {
        Objects.requireNonNull(receiveIdType, "receiveIdType");
        if (receiveId == null || receiveId.isBlank()) {
            throw new IllegalArgumentException("receiveId is blank");
        }
        if (cardJson == null || cardJson.isBlank()) {
            throw new IllegalArgumentException("cardJson is blank");
        }

        Client client = (appKey == null || appKey.isBlank()) ? registry.primary() : registry.get(appKey);
        CreateMessageReq req = CreateMessageReq.newBuilder()
                .receiveIdType(receiveIdType.getValue())
                .createMessageReqBody(CreateMessageReqBody.newBuilder()
                        .receiveId(receiveId)
                        .msgType("interactive")
                        .content(cardJson)
                        .build())
                .build();
        return executor.execute("im.v1.message.create", appKey,
                "msgType=interactive,receiveIdType=" + receiveIdType + ",receiveId=" + receiveId,
                () -> client.im().message().create(req));
    }

    /**
     * 发送飞书「消息卡片模板」：在开放平台搭建并发布模板后，用 {@code template_id} + {@code template_variable}，
     * 底层 content 为 {@code {"type":"template","data":{"template_id":"...","template_variable":{...}}}}。
     *
     * @param templateVariable 与模板内变量名一致；无变量可传 {@code null} 或空 Map
     */
    public CreateMessageResp sendCardTemplate(
            String appKey,
            ReceiveIdTypeEnum receiveIdType,
            String receiveId,
            String templateId,
            Map<String, Object> templateVariable
    ) throws Exception {
        Objects.requireNonNull(receiveIdType, "receiveIdType");
        if (receiveId == null || receiveId.isBlank()) {
            throw new IllegalArgumentException("receiveId is blank");
        }
        if (templateId == null || templateId.isBlank()) {
            throw new IllegalArgumentException("templateId is blank");
        }

        ObjectNode root = objectMapper.createObjectNode();
        root.put("type", "template");
        ObjectNode data = objectMapper.createObjectNode();
        data.put("template_id", templateId.trim());
        if (templateVariable != null && !templateVariable.isEmpty()) {
            data.set("template_variable", objectMapper.valueToTree(templateVariable));
        } else {
            data.set("template_variable", objectMapper.createObjectNode());
        }
        root.set("data", data);
        return sendCard(appKey, receiveIdType, receiveId, objectMapper.writeValueAsString(root));
    }

    public UpdateMessageResp updateMessage(String appKey, String messageId, String contentJson) throws Exception {
        if (messageId == null || messageId.isBlank()) {
            throw new IllegalArgumentException("messageId is blank");
        }
        if (contentJson == null || contentJson.isBlank()) {
            throw new IllegalArgumentException("contentJson is blank");
        }
        Client client = (appKey == null || appKey.isBlank()) ? registry.primary() : registry.get(appKey);
        UpdateMessageReq req = UpdateMessageReq.newBuilder()
                .messageId(messageId)
                .updateMessageReqBody(UpdateMessageReqBody.newBuilder()
                        .content(contentJson)
                        .build())
                .build();
        return executor.execute("im.v1.message.update", appKey, "messageId=" + messageId,
                () -> client.im().message().update(req));
    }
}
