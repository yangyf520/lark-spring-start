package com.larksuite.lark.im;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lark.oapi.Client;
import com.lark.oapi.core.utils.Jsons;
import com.lark.oapi.service.im.v1.enums.MsgTypeEnum;
import com.lark.oapi.service.im.v1.enums.ReceiveIdTypeEnum;
import com.lark.oapi.service.im.v1.model.CreateMessageReq;
import com.lark.oapi.service.im.v1.model.CreateMessageReqBody;
import com.lark.oapi.service.im.v1.model.CreateMessageResp;
import com.lark.oapi.service.im.v1.model.UpdateMessageReq;
import com.lark.oapi.service.im.v1.model.UpdateMessageReqBody;
import com.lark.oapi.service.im.v1.model.UpdateMessageResp;
import com.larksuite.lark.oapi.spring.OapiClientRegistry;
import com.larksuite.lark.service.LarkImApi;

import java.util.Objects;

/** 以应用身份调用 IM v1：发文本、卡片、更新消息。 */
public class ImMessageService implements LarkImApi {

    private final OapiClientRegistry registry;
    private final ObjectMapper objectMapper;

    public ImMessageService(OapiClientRegistry registry, ObjectMapper objectMapper) {
        this.registry = registry;
        this.objectMapper = objectMapper;
    }

    /** 发送文本消息。 */
    @Override
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

        CreateMessageResp resp = client.im().message().create(req);
        if (resp.getCode() != 0) {
            throw new IllegalStateException(String.format(
                    "sendText failed, code=%d, msg=%s, requestId=%s, err=%s",
                    resp.getCode(), resp.getMsg(), resp.getRequestId(), Jsons.DEFAULT.toJson(resp.getError())
            ));
        }
        return resp;
    }

    /** 发送 interactive 卡片消息。 */
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
        CreateMessageResp resp = client.im().message().create(req);
        if (resp.getCode() != 0) {
            throw new IllegalStateException(String.format(
                    "sendCard failed, code=%d, msg=%s, requestId=%s, err=%s",
                    resp.getCode(), resp.getMsg(), resp.getRequestId(), Jsons.DEFAULT.toJson(resp.getError())
            ));
        }
        return resp;
    }

    /** 更新已发送消息（content 为 JSON 字符串）。 */
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
        UpdateMessageResp resp = client.im().message().update(req);
        if (resp.getCode() != 0) {
            throw new IllegalStateException(String.format(
                    "updateMessage failed, code=%d, msg=%s, requestId=%s, err=%s",
                    resp.getCode(), resp.getMsg(), resp.getRequestId(), Jsons.DEFAULT.toJson(resp.getError())
            ));
        }
        return resp;
    }
}

