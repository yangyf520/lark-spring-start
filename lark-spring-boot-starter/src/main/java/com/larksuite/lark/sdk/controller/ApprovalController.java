package com.larksuite.lark.sdk.controller;

import com.lark.oapi.service.approval.v4.model.AddSignInstanceResp;
import com.lark.oapi.service.approval.v4.model.ApproveTaskResp;
import com.lark.oapi.service.approval.v4.model.CancelInstanceResp;
import com.lark.oapi.service.approval.v4.model.CcInstanceResp;
import com.lark.oapi.service.approval.v4.model.CreateInstanceResp;
import com.lark.oapi.service.approval.v4.model.GetApprovalResp;
import com.lark.oapi.service.approval.v4.model.GetInstanceResp;
import com.lark.oapi.service.approval.v4.model.InstanceCreate;
import com.lark.oapi.service.approval.v4.model.ListInstanceResp;
import com.lark.oapi.service.approval.v4.model.PreviewInstanceResp;
import com.lark.oapi.service.approval.v4.model.QueryInstanceResp;
import com.lark.oapi.service.approval.v4.model.QueryTaskResp;
import com.lark.oapi.service.approval.v4.model.RejectTaskResp;
import com.lark.oapi.service.approval.v4.model.ResubmitTaskResp;
import com.lark.oapi.service.approval.v4.model.SearchCcInstanceResp;
import com.lark.oapi.service.approval.v4.model.SearchTaskResp;
import com.lark.oapi.service.approval.v4.model.SpecifiedRollbackInstanceResp;
import com.lark.oapi.service.approval.v4.model.TransferTaskResp;
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

    /**
     * 按条件分页查询审批实例（query 与飞书 {@code ListInstanceReq} 一致）。
     */
    @GetMapping("/instances/list")
    public ListInstanceResp listInstances(
            @RequestParam(required = false) String appKey,
            @RequestParam(required = false) Integer pageSize,
            @RequestParam(required = false) String pageToken,
            @RequestParam(required = false) String approvalCode,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime
    ) throws Exception {
        return approvalService.listInstances(appKey, pageSize, pageToken, approvalCode, startTime, endTime);
    }

    /**
     * 撤销「审批中」实例。请求体 JSON 与飞书 SDK {@code CancelInstanceReq} 一致（含 {@code user_id_type} 与 {@code body}）。
     */
    @PostMapping(path = "/instances/cancel", consumes = MediaType.APPLICATION_JSON_VALUE)
    public CancelInstanceResp cancelInstance(
            @RequestParam(required = false) String appKey,
            @RequestBody String jsonBody
    ) throws Exception {
        return approvalService.cancelInstance(appKey, jsonBody);
    }

    @PostMapping(path = "/instances/add-sign", consumes = MediaType.APPLICATION_JSON_VALUE)
    public AddSignInstanceResp addSignInstance(
            @RequestParam(required = false) String appKey,
            @RequestBody String jsonBody
    ) throws Exception {
        return approvalService.addSignInstance(appKey, jsonBody);
    }

    @PostMapping(path = "/instances/query", consumes = MediaType.APPLICATION_JSON_VALUE)
    public QueryInstanceResp queryInstance(
            @RequestParam(required = false) String appKey,
            @RequestBody String jsonBody
    ) throws Exception {
        return approvalService.queryInstance(appKey, jsonBody);
    }

    @PostMapping(path = "/instances/preview", consumes = MediaType.APPLICATION_JSON_VALUE)
    public PreviewInstanceResp previewInstance(
            @RequestParam(required = false) String appKey,
            @RequestBody String jsonBody
    ) throws Exception {
        return approvalService.previewInstance(appKey, jsonBody);
    }

    @PostMapping(path = "/instances/cc", consumes = MediaType.APPLICATION_JSON_VALUE)
    public CcInstanceResp ccInstance(
            @RequestParam(required = false) String appKey,
            @RequestBody String jsonBody
    ) throws Exception {
        return approvalService.ccInstance(appKey, jsonBody);
    }

    @PostMapping(path = "/instances/search-cc", consumes = MediaType.APPLICATION_JSON_VALUE)
    public SearchCcInstanceResp searchCcInstance(
            @RequestParam(required = false) String appKey,
            @RequestBody String jsonBody
    ) throws Exception {
        return approvalService.searchCcInstance(appKey, jsonBody);
    }

    @PostMapping(path = "/instances/specified-rollback", consumes = MediaType.APPLICATION_JSON_VALUE)
    public SpecifiedRollbackInstanceResp specifiedRollbackInstance(
            @RequestParam(required = false) String appKey,
            @RequestBody String jsonBody
    ) throws Exception {
        return approvalService.specifiedRollbackInstance(appKey, jsonBody);
    }

    /** 同意审批任务。请求体 JSON 与飞书 SDK {@code ApproveTaskReq} 一致。 */
    @PostMapping(path = "/tasks/approve", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ApproveTaskResp approveTask(
            @RequestParam(required = false) String appKey,
            @RequestBody String jsonBody
    ) throws Exception {
        return approvalService.approveTask(appKey, jsonBody);
    }

    @PostMapping(path = "/tasks/reject", consumes = MediaType.APPLICATION_JSON_VALUE)
    public RejectTaskResp rejectTask(
            @RequestParam(required = false) String appKey,
            @RequestBody String jsonBody
    ) throws Exception {
        return approvalService.rejectTask(appKey, jsonBody);
    }

    @PostMapping(path = "/tasks/transfer", consumes = MediaType.APPLICATION_JSON_VALUE)
    public TransferTaskResp transferTask(
            @RequestParam(required = false) String appKey,
            @RequestBody String jsonBody
    ) throws Exception {
        return approvalService.transferTask(appKey, jsonBody);
    }

    @PostMapping(path = "/tasks/query", consumes = MediaType.APPLICATION_JSON_VALUE)
    public QueryTaskResp queryTask(
            @RequestParam(required = false) String appKey,
            @RequestBody String jsonBody
    ) throws Exception {
        return approvalService.queryTask(appKey, jsonBody);
    }

    @PostMapping(path = "/tasks/search", consumes = MediaType.APPLICATION_JSON_VALUE)
    public SearchTaskResp searchTask(
            @RequestParam(required = false) String appKey,
            @RequestBody String jsonBody
    ) throws Exception {
        return approvalService.searchTask(appKey, jsonBody);
    }

    @PostMapping(path = "/tasks/resubmit", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResubmitTaskResp resubmitTask(
            @RequestParam(required = false) String appKey,
            @RequestBody String jsonBody
    ) throws Exception {
        return approvalService.resubmitTask(appKey, jsonBody);
    }
}
