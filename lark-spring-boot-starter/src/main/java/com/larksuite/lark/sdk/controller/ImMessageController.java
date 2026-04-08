package com.larksuite.lark.sdk.controller;

import com.lark.oapi.service.im.v1.enums.ReceiveIdTypeEnum;
import com.lark.oapi.service.im.v1.model.CreateMessageResp;
import com.lark.oapi.service.im.v1.model.DeleteMessageResp;
import com.lark.oapi.service.im.v1.model.GetMessageResp;
import com.lark.oapi.service.im.v1.model.ListMessageResp;
import com.lark.oapi.service.im.v1.model.ReplyMessageReq;
import com.lark.oapi.service.im.v1.model.ReplyMessageResp;
import com.lark.oapi.service.im.v1.model.UpdateMessageResp;
import com.larksuite.lark.common.annotation.LarkApi;
import com.larksuite.lark.common.support.SdkModelJson;
import com.larksuite.lark.sdk.service.message.ImMessageService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 即时消息：以应用身份发文本 / 卡片、更新消息。
 * <p>
 * 成功与异常由全局 Advice 与 Service 统一处理。
 */
@LarkApi
@RestController
@RequestMapping(path = "/lark/im", produces = MediaType.APPLICATION_JSON_VALUE)
public class ImMessageController {

    private final ImMessageService im;

    /**
     * 构造注入。
     * <p>
     * @param im 即时消息服务
     */
    public ImMessageController(ImMessageService im) {
        this.im = im;
    }

    /**
     * 发送文本消息请求体。
     * <p>
     * @param appKey        应用配置键，可空（使用 primary）
     * @param receiveIdType 接收者 ID 类型，必填
     * @param receiveId     接收者 ID，必填
     * @param text          文本内容，必填
     */
    public record SendTextReq(
            String appKey,
            @NotNull ReceiveIdTypeEnum receiveIdType,
            @NotBlank String receiveId,
            @NotBlank String text
    ) {}

    /**
     * 发送卡片消息请求体。
     * <p>
     * @param appKey        应用配置键，可空（使用 primary）
     * @param receiveIdType 接收者 ID 类型，必填
     * @param receiveId     接收者 ID，必填
     * @param cardJson      卡片 JSON，必填
     */
    public record SendCardReq(
            String appKey,
            @NotNull ReceiveIdTypeEnum receiveIdType,
            @NotBlank String receiveId,
            @NotBlank String cardJson
    ) {}

    /**
     * 发送消息卡片模板请求体（开放平台模板 {@code template_id}）。
     * <p>
     * @param appKey            应用配置键，可空（使用 primary）
     * @param receiveIdType     接收者 ID 类型，必填
     * @param receiveId         接收者 ID，必填
     * @param templateId        模板 ID，必填
     * @param templateVariable  模板变量，可空（无变量时可为 null）
     */
    public record SendCardTemplateReq(
            String appKey,
            @NotNull ReceiveIdTypeEnum receiveIdType,
            @NotBlank String receiveId,
            @NotBlank String templateId,
            Map<String, Object> templateVariable
    ) {}

    /**
     * 更新消息请求体。
     * <p>
     * @param appKey      应用配置键，可空（使用 primary）
     * @param messageId   消息 ID，必填
     * @param contentJson 新内容 JSON，必填
     */
    public record UpdateMessageReq(
            String appKey,
            @NotBlank String messageId,
            @NotBlank String contentJson
    ) {}

    /**
     * 向用户或群会话发送文本消息。
     * <p>
     * @param req 请求体
     * @return 飞书 SDK {@link CreateMessageResp}
     */
    @PostMapping(path = "/send-text", consumes = MediaType.APPLICATION_JSON_VALUE)
    public CreateMessageResp sendText(@Valid @RequestBody SendTextReq req) throws Exception {
        return im.sendText(req.appKey(), req.receiveIdType(), req.receiveId(), req.text());
    }

    /**
     * 向用户或群会话发送交互式卡片消息。
     * <p>
     * @param req 请求体
     * @return 飞书 SDK {@link CreateMessageResp}
     */
    @PostMapping(path = "/send-card", consumes = MediaType.APPLICATION_JSON_VALUE)
    public CreateMessageResp sendCard(@Valid @RequestBody SendCardReq req) throws Exception {
        return im.sendCard(req.appKey(), req.receiveIdType(), req.receiveId(), req.cardJson());
    }

    /**
     * 发送消息卡片模板（仅需 templateId + 变量）。
     * <p>
     * @param req 请求体
     * @return 飞书 SDK {@link CreateMessageResp}
     */
    @PostMapping(path = "/send-card-template", consumes = MediaType.APPLICATION_JSON_VALUE)
    public CreateMessageResp sendCardTemplate(@Valid @RequestBody SendCardTemplateReq req) throws Exception {
        return im.sendCardTemplate(
                req.appKey(),
                req.receiveIdType(),
                req.receiveId(),
                req.templateId(),
                req.templateVariable());
    }

    /**
     * 按 messageId 更新已发送消息。
     * <p>
     * @param req 请求体
     * @return 飞书 SDK {@link UpdateMessageResp}
     */
    @PostMapping(path = "/update-message", consumes = MediaType.APPLICATION_JSON_VALUE)
    public UpdateMessageResp updateMessage(@Valid @RequestBody UpdateMessageReq req) throws Exception {
        return im.updateMessage(req.appKey(), req.messageId(), req.contentJson());
    }

    /**
     * 获取单条消息内容。
     * <p>
     * @param messageId   消息 ID
     * @param appKey      应用配置键，可空
     * @param userIdType  用户 ID 类型，可空（与开放平台 query 一致）
     */
    @GetMapping("/messages/{messageId}")
    public GetMessageResp getMessage(
            @PathVariable String messageId,
            @RequestParam(required = false) String appKey,
            @RequestParam(required = false) String userIdType
    ) throws Exception {
        return im.getMessage(appKey, messageId, userIdType);
    }

    /**
     * 分页拉取会话历史消息（容器类型一般为 {@code chat}）。
     */
    @GetMapping("/messages")
    public ListMessageResp listMessages(
            @RequestParam(required = false) String appKey,
            @RequestParam(defaultValue = "chat") String containerIdType,
            @RequestParam String containerId,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime,
            @RequestParam(required = false) String sortType,
            @RequestParam(required = false) Integer pageSize,
            @RequestParam(required = false) String pageToken
    ) throws Exception {
        return im.listMessages(appKey, containerIdType, containerId, startTime, endTime, sortType, pageSize, pageToken);
    }

    /**
     * 撤回消息（与开放平台「删除消息」一致）。
     */
    @DeleteMapping("/messages/{messageId}")
    public DeleteMessageResp deleteMessage(
            @PathVariable String messageId,
            @RequestParam(required = false) String appKey
    ) throws Exception {
        return im.deleteMessage(appKey, messageId);
    }

    /**
     * 回复某条消息。请求体为飞书 SDK {@link ReplyMessageReq} 的 JSON（含 {@code message_id} 与 {@code body}）。
     */
    @PostMapping(path = "/reply-message", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ReplyMessageResp replyMessage(
            @RequestParam(required = false) String appKey,
            @RequestBody String jsonBody
    ) throws Exception {
        ReplyMessageReq req = SdkModelJson.fromJson(jsonBody, ReplyMessageReq.class);
        return im.replyMessage(appKey, req);
    }
}
