package com.larksuite.lark.sdk.controller;

import com.lark.oapi.service.approval.v4.model.CreateInstanceResp;
import com.lark.oapi.service.approval.v4.model.GetApprovalResp;
import com.lark.oapi.service.approval.v4.model.GetInstanceResp;
import com.lark.oapi.service.approval.v4.model.InstanceCreate;
import com.larksuite.lark.common.annotation.LarkApi;
import com.larksuite.lark.sdk.service.approval.ApprovalService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 审批：定义、实例查询与创建。
 * <p>
 * 成功与异常由全局 Advice 与 Service 统一处理。
 */
@LarkApi
@RestController
@RequestMapping(path = "/lark/approval", produces = MediaType.APPLICATION_JSON_VALUE)
public class ApprovalController {

    private final ApprovalService approvalService;

    /**
     * 构造注入。
     * <p>
     * @param approvalService 审批服务
     */
    public ApprovalController(ApprovalService approvalService) {
        this.approvalService = approvalService;
    }

    /**
     * 创建审批实例请求体。
     * <p>
     * @param appKey 应用配置键，可空（使用 primary）
     * @param body   审批创建体，必填
     */
    public record CreateInstanceReq(
            String appKey,
            @Valid InstanceCreate body
    ) {}

    /**
     * 根据 approvalCode 获取审批定义详情。
     * <p>
     * @param approvalCode 审批定义编码
     * @param appKey       应用配置键，可空（使用 primary）
     * @return 飞书 SDK {@link GetApprovalResp}
     */
    @GetMapping("/approvals/{approvalCode}")
    public GetApprovalResp getApproval(
            @PathVariable String approvalCode,
            @RequestParam(required = false) String appKey
    ) throws Exception {
        return approvalService.getApproval(appKey, approvalCode);
    }

    /**
     * 根据 instanceId 查询审批实例。
     * <p>
     * @param instanceId  实例 ID
     * @param appKey      应用配置键，可空（使用 primary）
     * @param userId      用户 ID，可空
     * @param userIdType  用户 ID 类型，默认 {@code user_id}
     * @return 飞书 SDK {@link GetInstanceResp}
     */
    @GetMapping("/instances/{instanceId}")
    public GetInstanceResp getInstance(
            @PathVariable String instanceId,
            @RequestParam(required = false) String appKey,
            @RequestParam(required = false) String userId,
            @RequestParam(required = false, defaultValue = "user_id") String userIdType
    ) throws Exception {
        return approvalService.getInstance(appKey, instanceId, userId, userIdType);
    }

    /**
     * 提交审批实例创建请求。
     * <p>
     * @param req 请求体，{@code body} 必填
     * @return 飞书 SDK {@link CreateInstanceResp}
     */
    @PostMapping(path = "/instances", consumes = MediaType.APPLICATION_JSON_VALUE)
    public CreateInstanceResp createInstance(@Valid @RequestBody CreateInstanceReq req) throws Exception {
        if (req.body() == null) {
            throw new IllegalArgumentException("body is required");
        }
        return approvalService.createInstance(req.appKey(), req.body());
    }
}
