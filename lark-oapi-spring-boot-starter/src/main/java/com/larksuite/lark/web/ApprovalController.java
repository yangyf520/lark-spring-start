package com.larksuite.lark.web;

import com.larksuite.lark.api.dto.ApiResponse;
import com.larksuite.lark.service.LarkApprovalService;
import com.lark.oapi.service.approval.v4.model.InstanceCreate;
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
@RestController
@ConditionalOnProperty(prefix = "lark.api", name = "enabled", havingValue = "true", matchIfMissing = true)
@RequestMapping(path = "/api/lark/approval", produces = MediaType.APPLICATION_JSON_VALUE)
public class ApprovalController {

    private final LarkApprovalService approvalService;

    public ApprovalController(LarkApprovalService approvalService) {
        this.approvalService = approvalService;
    }

    public record CreateInstanceReq(String appKey, @Valid InstanceCreate body) {}

    /** 获取审批定义。 */
    @GetMapping("/approvals/{approvalCode}")
    public ApiResponse getApproval(
            @PathVariable String approvalCode,
            @RequestParam(required = false) String appKey
    ) {
        try {
            var resp = approvalService.getApproval(appKey, approvalCode);
            if (!resp.success()) {
                return ApiResponse.failure(String.valueOf(resp.getCode()), resp.getMsg());
            }
            return ApiResponse.success(resp.getData());
        } catch (Exception e) {
            return ApiResponse.failure(e.getClass().getSimpleName(), e.getMessage());
        }
    }

    /** 获取审批实例详情。 */
    @GetMapping("/instances/{instanceId}")
    public ApiResponse getInstance(
            @PathVariable String instanceId,
            @RequestParam(required = false) String appKey,
            @RequestParam(required = false) String userId,
            @RequestParam(required = false, defaultValue = "user_id") String userIdType
    ) {
        try {
            var resp = approvalService.getInstance(appKey, instanceId, userId, userIdType);
            if (!resp.success()) {
                return ApiResponse.failure(String.valueOf(resp.getCode()), resp.getMsg());
            }
            return ApiResponse.success(resp.getData());
        } catch (Exception e) {
            return ApiResponse.failure(e.getClass().getSimpleName(), e.getMessage());
        }
    }

    /** 创建审批实例。 */
    @PostMapping(path = "/instances", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse createInstance(@Valid @RequestBody CreateInstanceReq req) {
        if (req.body() == null) {
            return ApiResponse.failure("INVALID_ARGUMENT", "body is required");
        }
        try {
            var resp = approvalService.createInstance(req.appKey(), req.body());
            if (!resp.success()) {
                return ApiResponse.failure(String.valueOf(resp.getCode()), resp.getMsg());
            }
            return ApiResponse.success(resp.getData());
        } catch (Exception e) {
            return ApiResponse.failure(e.getClass().getSimpleName(), e.getMessage());
        }
    }
}
