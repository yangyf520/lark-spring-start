package com.larksuite.lark.web;

import com.lark.oapi.service.approval.v4.model.CreateInstanceResp;
import com.lark.oapi.service.approval.v4.model.GetApprovalResp;
import com.lark.oapi.service.approval.v4.model.GetInstanceResp;
import com.lark.oapi.service.approval.v4.model.InstanceCreate;
import com.larksuite.lark.core.advice.LarkApi;
import com.larksuite.lark.service.LarkApprovalService;
import jakarta.validation.Valid;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** 审批：定义、实例查询与创建。 */
@LarkApi
@RestController
@ConditionalOnProperty(prefix = "lark.api", name = "enabled", havingValue = "true", matchIfMissing = true)
@RequestMapping(path = "/api/lark/approval", produces = MediaType.APPLICATION_JSON_VALUE)
public class ApprovalController {

    private final LarkApprovalService approvalService;

    public ApprovalController(LarkApprovalService approvalService) {
        this.approvalService = approvalService;
    }

    public record CreateInstanceReq(
            String appKey,
            @Valid InstanceCreate body
    ) {}

    /** 获取审批定义：根据 approvalCode 获取审批定义详情。 */
    @GetMapping("/approvals/{approvalCode}")
    public GetApprovalResp getApproval(
            @PathVariable String approvalCode,
            @RequestParam(required = false) String appKey
    ) throws Exception {
        return approvalService.getApproval(appKey, approvalCode);
    }

    /** 获取审批实例详情：根据 instanceId 查询审批实例。 */
    @GetMapping("/instances/{instanceId}")
    public GetInstanceResp getInstance(
            @PathVariable String instanceId,
            @RequestParam(required = false) String appKey,
            @RequestParam(required = false) String userId,
            @RequestParam(required = false, defaultValue = "user_id") String userIdType
    ) throws Exception {
        return approvalService.getInstance(appKey, instanceId, userId, userIdType);
    }

    /** 创建审批实例：提交审批实例创建请求。 */
    @PostMapping(path = "/instances", consumes = MediaType.APPLICATION_JSON_VALUE)
    public CreateInstanceResp createInstance(@Valid @RequestBody CreateInstanceReq req) throws Exception {
        if (req.body() == null) {
            throw new IllegalArgumentException("body is required");
        }
        return approvalService.createInstance(req.appKey(), req.body());
    }
}
