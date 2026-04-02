package com.larksuite.lark.sdk.controller;

import com.lark.oapi.service.im.v1.enums.ReceiveIdTypeEnum;
import com.lark.oapi.service.im.v1.model.CreateMessageResp;
import com.lark.oapi.service.im.v1.model.UpdateMessageResp;
import com.larksuite.lark.common.annotation.LarkApi;
import com.larksuite.lark.sdk.service.message.ImMessageService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** 即时消息：以应用身份发文本 / 卡片、更新消息。 */
@LarkApi
@RestController
@RequestMapping(path = "/lark/im", produces = MediaType.APPLICATION_JSON_VALUE)
public class ImMessageController {

    private final ImMessageService im;

    public ImMessageController(ImMessageService im) {
        this.im = im;
    }

    public record SendTextReq(
            String appKey,
            @NotNull ReceiveIdTypeEnum receiveIdType,
            @NotBlank String receiveId,
            @NotBlank String text
    ) {}

    public record SendCardReq(
            String appKey,
            @NotNull ReceiveIdTypeEnum receiveIdType,
            @NotBlank String receiveId,
            @NotBlank String cardJson
    ) {}

    /**
     * 飞书消息卡片模板（开放平台搭建发布的 template_id），变量名与模板内定义一致。
     *
     * @param templateVariable 可为 null 或省略（无变量时发空对象）
     */
    public record SendCardTemplateReq(
            String appKey,
            @NotNull ReceiveIdTypeEnum receiveIdType,
            @NotBlank String receiveId,
            @NotBlank String templateId,
            Map<String, Object> templateVariable
    ) {}

    public record UpdateMessageReq(
            String appKey,
            @NotBlank String messageId,
            @NotBlank String contentJson
    ) {}

    /** 发送文本消息：向用户或群会话发送文本消息。 */
    @PostMapping(path = "/send-text", consumes = MediaType.APPLICATION_JSON_VALUE)
    public CreateMessageResp sendText(@Valid @RequestBody SendTextReq req) throws Exception {
        return im.sendText(req.appKey(), req.receiveIdType(), req.receiveId(), req.text());
    }

    /** 发送卡片消息：向用户或群会话发送交互式卡片消息。 */
    @PostMapping(path = "/send-card", consumes = MediaType.APPLICATION_JSON_VALUE)
    public CreateMessageResp sendCard(@Valid @RequestBody SendCardReq req) throws Exception {
        return im.sendCard(req.appKey(), req.receiveIdType(), req.receiveId(), req.cardJson());
    }

    /** 发送消息卡片模板：仅需 templateId + 变量，无需手写整段 card JSON。 */
    @PostMapping(path = "/send-card-template", consumes = MediaType.APPLICATION_JSON_VALUE)
    public CreateMessageResp sendCardTemplate(@Valid @RequestBody SendCardTemplateReq req) throws Exception {
        return im.sendCardTemplate(
                req.appKey(),
                req.receiveIdType(),
                req.receiveId(),
                req.templateId(),
                req.templateVariable());
    }

    /** 更新消息内容：按 messageId 更新已发送消息。 */
    @PostMapping(path = "/update-message", consumes = MediaType.APPLICATION_JSON_VALUE)
    public UpdateMessageResp updateMessage(@Valid @RequestBody UpdateMessageReq req) throws Exception {
        return im.updateMessage(req.appKey(), req.messageId(), req.contentJson());
    }
}
