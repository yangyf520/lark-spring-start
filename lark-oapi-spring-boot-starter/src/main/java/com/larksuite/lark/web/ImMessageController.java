package com.larksuite.lark.web;

import com.lark.oapi.service.im.v1.enums.ReceiveIdTypeEnum;
import com.larksuite.lark.api.dto.ApiResponse;
import com.larksuite.lark.im.ImMessageService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** 即时消息：以应用身份发文本 / 卡片、更新消息。 */
@RestController
@ConditionalOnProperty(prefix = "lark.api", name = "enabled", havingValue = "true", matchIfMissing = true)
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

    /** 发送文本消息到用户或群。 */
    @PostMapping(path = "/send-text", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse sendText(@Valid @RequestBody SendTextReq req) {
        try {
            var resp = im.sendText(req.appKey(), req.receiveIdType(), req.receiveId(), req.text());
            if (!resp.success()) {
                return ApiResponse.failure(String.valueOf(resp.getCode()), resp.getMsg());
            }
            return ApiResponse.success(resp.getData());
        } catch (Exception e) {
            return ApiResponse.failure(e.getClass().getSimpleName(), e.getMessage());
        }
    }

    /** 发送交互式卡片消息。 */
    @PostMapping(path = "/send-card", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse sendCard(@Valid @RequestBody SendCardReq req) {
        try {
            var resp = im.sendCard(req.appKey(), req.receiveIdType(), req.receiveId(), req.cardJson());
            if (!resp.success()) {
                return ApiResponse.failure(String.valueOf(resp.getCode()), resp.getMsg());
            }
            return ApiResponse.success(resp.getData());
        } catch (Exception e) {
            return ApiResponse.failure(e.getClass().getSimpleName(), e.getMessage());
        }
    }

    /** 更新已发送消息内容。 */
    @PostMapping(path = "/update-message", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse updateMessage(@Valid @RequestBody UpdateMessageReq req) {
        try {
            var resp = im.updateMessage(req.appKey(), req.messageId(), req.contentJson());
            if (!resp.success()) {
                return ApiResponse.failure(String.valueOf(resp.getCode()), resp.getMsg());
            }
            return ApiResponse.success(resp.getData());
        } catch (Exception e) {
            return ApiResponse.failure(e.getClass().getSimpleName(), e.getMessage());
        }
    }
}

