package com.larksuite.lark.web;

import com.lark.oapi.service.im.v1.enums.ReceiveIdTypeEnum;
import com.lark.oapi.service.im.v1.model.CreateMessageResp;
import com.lark.oapi.service.im.v1.model.UpdateMessageResp;
import com.larksuite.lark.core.common.LarkApi;
import com.larksuite.lark.im.ImMessageService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import com.larksuite.lark.starter.condition.ConditionalOnStarterRestApi;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** 即时消息：以应用身份发文本 / 卡片、更新消息。 */
@LarkApi
@RestController
@ConditionalOnStarterRestApi
@RequestMapping(path = "/api/lark/im", produces = MediaType.APPLICATION_JSON_VALUE)
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

    /** 更新消息内容：按 messageId 更新已发送消息。 */
    @PostMapping(path = "/update-message", consumes = MediaType.APPLICATION_JSON_VALUE)
    public UpdateMessageResp updateMessage(@Valid @RequestBody UpdateMessageReq req) throws Exception {
        return im.updateMessage(req.appKey(), req.messageId(), req.contentJson());
    }
}
