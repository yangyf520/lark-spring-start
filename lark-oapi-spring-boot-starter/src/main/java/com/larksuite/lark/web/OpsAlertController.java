package com.larksuite.lark.web;

import com.larksuite.lark.core.advice.LarkApi;
import com.larksuite.lark.service.LarkOpsAlertService;
import com.larksuite.lark.service.LarkOpsAlertService.OpsAlertRequest;
import com.larksuite.lark.service.LarkOpsAlertService.OpsAlertResult;
import jakarta.validation.Valid;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 运维告警：一次请求「新建群并发首条消息」或「向已有群发一条消息」。
 * <p>
 * 建群时 {@code memberOpenIds} / {@code botOpenIds} 均为飞书 open_id；与 {@link com.larksuite.lark.service.LarkChatService#createChat} 一致。
 */
@LarkApi
@RestController
@ConditionalOnProperty(prefix = "lark.api", name = "enabled", havingValue = "true", matchIfMissing = true)
@RequestMapping(path = "/api/lark/ops", produces = MediaType.APPLICATION_JSON_VALUE)
public class OpsAlertController {

    private final LarkOpsAlertService opsAlertService;

    public OpsAlertController(LarkOpsAlertService opsAlertService) {
        this.opsAlertService = opsAlertService;
    }

    @PostMapping(path = "/alert", consumes = MediaType.APPLICATION_JSON_VALUE)
    public OpsAlertResult alert(@Valid @RequestBody OpsAlertRequest req) throws Exception {
        return opsAlertService.sendOpsAlert(req);
    }
}
