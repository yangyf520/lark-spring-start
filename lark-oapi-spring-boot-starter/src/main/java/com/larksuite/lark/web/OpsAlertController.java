package com.larksuite.lark.web;

import com.larksuite.lark.core.common.LarkApi;
import com.larksuite.lark.service.ops.OpsAlertService;
import com.larksuite.lark.service.ops.OpsAlertService.OpsAlertRequest;
import com.larksuite.lark.service.ops.OpsAlertService.OpsAlertResult;
import com.larksuite.lark.starter.condition.ConditionalOnStarterRestApi;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 运维告警：一次请求「新建群并发首条消息」或「向已有群发一条消息」。
 * <p>
 * 建群时 {@code memberOpenIds} / {@code botOpenIds} 均为飞书 open_id；与 {@link com.larksuite.lark.service.chat.ChatService#createChat} 一致。
 */
@LarkApi
@RestController
@ConditionalOnStarterRestApi
@RequestMapping(path = "/api/lark/ops", produces = MediaType.APPLICATION_JSON_VALUE)
public class OpsAlertController {

    private final OpsAlertService opsAlertService;

    public OpsAlertController(OpsAlertService opsAlertService) {
        this.opsAlertService = opsAlertService;
    }

    @PostMapping(path = "/alert", consumes = MediaType.APPLICATION_JSON_VALUE)
    public OpsAlertResult alert(@Valid @RequestBody OpsAlertRequest req) throws Exception {
        return opsAlertService.sendOpsAlert(req);
    }
}
