package com.larksuite.lark.sdk.controller;

import com.larksuite.lark.common.annotation.LarkApi;
import com.larksuite.lark.sdk.service.ops.OpsAlertService;
import com.larksuite.lark.sdk.service.ops.OpsAlertService.OpsAlertRequest;
import com.larksuite.lark.sdk.service.ops.OpsAlertService.OpsAlertResult;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 运维告警：一次请求「新建群并发首条消息」或「向已有群发一条消息」。
 * <p>
 * 建群时 {@code memberOpenIds} 的类型由 {@code userIdType} 指定（默认 {@code open_id}）；{@code botOpenIds} 仍为机器人 open_id。
 */
@LarkApi
@RestController
@RequestMapping(path = "/lark/ops", produces = MediaType.APPLICATION_JSON_VALUE)
public class OpsAlertController {

    private final OpsAlertService opsAlertService;

    /**
     * 构造注入。
     * <p>
     * @param opsAlertService 运维告警服务
     */
    public OpsAlertController(OpsAlertService opsAlertService) {
        this.opsAlertService = opsAlertService;
    }

    /**
     * 发送运维告警（建群或向已有群发消息）。
     * <p>
     * @param req 告警请求体
     * @return 发送结果
     */
    @PostMapping(path = "/alert", consumes = MediaType.APPLICATION_JSON_VALUE)
    public OpsAlertResult alert(@Valid @RequestBody OpsAlertRequest req) throws Exception {
        return opsAlertService.sendOpsAlert(req);
    }
}
