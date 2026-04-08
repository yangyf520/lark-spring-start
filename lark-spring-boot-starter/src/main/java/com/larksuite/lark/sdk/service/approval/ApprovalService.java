package com.larksuite.lark.sdk.service.approval;

import com.lark.oapi.Client;
import com.lark.oapi.service.approval.v4.model.AddSignInstanceReq;
import com.lark.oapi.service.approval.v4.model.AddSignInstanceResp;
import com.lark.oapi.service.approval.v4.model.ApproveTaskReq;
import com.lark.oapi.service.approval.v4.model.ApproveTaskResp;
import com.lark.oapi.service.approval.v4.model.CancelInstanceReq;
import com.lark.oapi.service.approval.v4.model.CancelInstanceResp;
import com.lark.oapi.service.approval.v4.model.CcInstanceReq;
import com.lark.oapi.service.approval.v4.model.CcInstanceResp;
import com.lark.oapi.service.approval.v4.model.CreateInstanceReq;
import com.lark.oapi.service.approval.v4.model.CreateInstanceResp;
import com.lark.oapi.service.approval.v4.model.GetApprovalReq;
import com.lark.oapi.service.approval.v4.model.GetApprovalResp;
import com.lark.oapi.service.approval.v4.model.GetInstanceReq;
import com.lark.oapi.service.approval.v4.model.GetInstanceResp;
import com.lark.oapi.service.approval.v4.model.InstanceCreate;
import com.lark.oapi.service.approval.v4.model.ListInstanceReq;
import com.lark.oapi.service.approval.v4.model.ListInstanceResp;
import com.lark.oapi.service.approval.v4.model.PreviewInstanceReq;
import com.lark.oapi.service.approval.v4.model.PreviewInstanceResp;
import com.lark.oapi.service.approval.v4.model.QueryInstanceReq;
import com.lark.oapi.service.approval.v4.model.QueryInstanceResp;
import com.lark.oapi.service.approval.v4.model.QueryTaskReq;
import com.lark.oapi.service.approval.v4.model.QueryTaskResp;
import com.lark.oapi.service.approval.v4.model.RejectTaskReq;
import com.lark.oapi.service.approval.v4.model.RejectTaskResp;
import com.lark.oapi.service.approval.v4.model.ResubmitTaskReq;
import com.lark.oapi.service.approval.v4.model.ResubmitTaskResp;
import com.lark.oapi.service.approval.v4.model.SearchCcInstanceReq;
import com.lark.oapi.service.approval.v4.model.SearchCcInstanceResp;
import com.lark.oapi.service.approval.v4.model.SearchTaskReq;
import com.lark.oapi.service.approval.v4.model.SearchTaskResp;
import com.lark.oapi.service.approval.v4.model.SpecifiedRollbackInstanceReq;
import com.lark.oapi.service.approval.v4.model.SpecifiedRollbackInstanceResp;
import com.lark.oapi.service.approval.v4.model.TransferTaskReq;
import com.lark.oapi.service.approval.v4.model.TransferTaskResp;
import com.larksuite.lark.common.support.SdkModelJson;
import com.larksuite.lark.sdk.core.ClientRegistry;
import com.larksuite.lark.common.support.ApiExecutor;

import java.util.Objects;

/** 审批 v4：定义、实例、任务、创建；返回完整 SDK Resp。 */
public class ApprovalService {

    private final ClientRegistry registry;
    private final ApiExecutor executor;

    public ApprovalService(ClientRegistry registry, ApiExecutor executor) {
        this.registry = registry;
        this.executor = executor;
    }

    public GetApprovalResp getApproval(String appKey, String approvalCode) throws Exception {
        Client client = resolveClient(appKey);
        GetApprovalReq req = GetApprovalReq.newBuilder()
                .approvalCode(approvalCode)
                .build();
        return executor.execute("approval.v4.approval.get", appKey, "approvalCode=" + approvalCode, () -> client.approval().v4().approval().get(req));
    }

    public GetInstanceResp getInstance(String appKey, String instanceId, String userId, String userIdType) throws Exception {
        Client client = resolveClient(appKey);
        GetInstanceReq req = GetInstanceReq.newBuilder()
                .instanceId(instanceId)
                .userId(userId)
                .userIdType(userIdType == null || userIdType.isBlank() ? "user_id" : userIdType)
                .build();
        return executor.execute("approval.v4.instance.get", appKey, "instanceId=" + instanceId, () -> client.approval().v4().instance().get(req));
    }

    public CreateInstanceResp createInstance(String appKey, InstanceCreate body) throws Exception {
        Client client = resolveClient(appKey);
        CreateInstanceReq req = CreateInstanceReq.newBuilder()
                .instanceCreate(body)
                .build();
        return executor.execute("approval.v4.instance.create", appKey, "approvalCode=" + (body == null ? "" : body.getApprovalCode()), () -> client.approval().v4().instance().create(req));
    }

    public ListInstanceResp listInstances(
            String appKey,
            Integer pageSize,
            String pageToken,
            String approvalCode,
            String startTime,
            String endTime
    ) throws Exception {
        Client client = resolveClient(appKey);
        ListInstanceReq.Builder b = ListInstanceReq.newBuilder();
        if (pageSize != null) {
            b.pageSize(pageSize);
        }
        if (pageToken != null && !pageToken.isBlank()) {
            b.pageToken(pageToken);
        }
        if (approvalCode != null && !approvalCode.isBlank()) {
            b.approvalCode(approvalCode);
        }
        if (startTime != null && !startTime.isBlank()) {
            b.startTime(startTime);
        }
        if (endTime != null && !endTime.isBlank()) {
            b.endTime(endTime);
        }
        ListInstanceReq req = b.build();
        return executor.execute("approval.v4.instance.list", appKey, "approvalCode=" + approvalCode,
                () -> client.approval().v4().instance().list(req));
    }

    public CancelInstanceResp cancelInstance(String appKey, String jsonBody) throws Exception {
        CancelInstanceReq req = SdkModelJson.fromJson(Objects.requireNonNull(jsonBody, "jsonBody"), CancelInstanceReq.class);
        Client client = resolveClient(appKey);
        return executor.execute("approval.v4.instance.cancel", appKey, "", () -> client.approval().v4().instance().cancel(req));
    }

    public AddSignInstanceResp addSignInstance(String appKey, String jsonBody) throws Exception {
        AddSignInstanceReq req = SdkModelJson.fromJson(Objects.requireNonNull(jsonBody, "jsonBody"), AddSignInstanceReq.class);
        Client client = resolveClient(appKey);
        return executor.execute("approval.v4.instance.addSign", appKey, "", () -> client.approval().v4().instance().addSign(req));
    }

    public QueryInstanceResp queryInstance(String appKey, String jsonBody) throws Exception {
        QueryInstanceReq req = SdkModelJson.fromJson(Objects.requireNonNull(jsonBody, "jsonBody"), QueryInstanceReq.class);
        Client client = resolveClient(appKey);
        return executor.execute("approval.v4.instance.query", appKey, "", () -> client.approval().v4().instance().query(req));
    }

    public PreviewInstanceResp previewInstance(String appKey, String jsonBody) throws Exception {
        PreviewInstanceReq req = SdkModelJson.fromJson(Objects.requireNonNull(jsonBody, "jsonBody"), PreviewInstanceReq.class);
        Client client = resolveClient(appKey);
        return executor.execute("approval.v4.instance.preview", appKey, "", () -> client.approval().v4().instance().preview(req));
    }

    public CcInstanceResp ccInstance(String appKey, String jsonBody) throws Exception {
        CcInstanceReq req = SdkModelJson.fromJson(Objects.requireNonNull(jsonBody, "jsonBody"), CcInstanceReq.class);
        Client client = resolveClient(appKey);
        return executor.execute("approval.v4.instance.cc", appKey, "", () -> client.approval().v4().instance().cc(req));
    }

    public SearchCcInstanceResp searchCcInstance(String appKey, String jsonBody) throws Exception {
        SearchCcInstanceReq req = SdkModelJson.fromJson(Objects.requireNonNull(jsonBody, "jsonBody"), SearchCcInstanceReq.class);
        Client client = resolveClient(appKey);
        return executor.execute("approval.v4.instance.searchCc", appKey, "", () -> client.approval().v4().instance().searchCc(req));
    }

    public SpecifiedRollbackInstanceResp specifiedRollbackInstance(String appKey, String jsonBody) throws Exception {
        SpecifiedRollbackInstanceReq req = SdkModelJson.fromJson(Objects.requireNonNull(jsonBody, "jsonBody"), SpecifiedRollbackInstanceReq.class);
        Client client = resolveClient(appKey);
        return executor.execute("approval.v4.instance.specifiedRollback", appKey, "",
                () -> client.approval().v4().instance().specifiedRollback(req));
    }

    public ApproveTaskResp approveTask(String appKey, String jsonBody) throws Exception {
        ApproveTaskReq req = SdkModelJson.fromJson(Objects.requireNonNull(jsonBody, "jsonBody"), ApproveTaskReq.class);
        Client client = resolveClient(appKey);
        return executor.execute("approval.v4.task.approve", appKey, "", () -> client.approval().v4().task().approve(req));
    }

    public RejectTaskResp rejectTask(String appKey, String jsonBody) throws Exception {
        RejectTaskReq req = SdkModelJson.fromJson(Objects.requireNonNull(jsonBody, "jsonBody"), RejectTaskReq.class);
        Client client = resolveClient(appKey);
        return executor.execute("approval.v4.task.reject", appKey, "", () -> client.approval().v4().task().reject(req));
    }

    public TransferTaskResp transferTask(String appKey, String jsonBody) throws Exception {
        TransferTaskReq req = SdkModelJson.fromJson(Objects.requireNonNull(jsonBody, "jsonBody"), TransferTaskReq.class);
        Client client = resolveClient(appKey);
        return executor.execute("approval.v4.task.transfer", appKey, "", () -> client.approval().v4().task().transfer(req));
    }

    public QueryTaskResp queryTask(String appKey, String jsonBody) throws Exception {
        QueryTaskReq req = SdkModelJson.fromJson(Objects.requireNonNull(jsonBody, "jsonBody"), QueryTaskReq.class);
        Client client = resolveClient(appKey);
        return executor.execute("approval.v4.task.query", appKey, "", () -> client.approval().v4().task().query(req));
    }

    public SearchTaskResp searchTask(String appKey, String jsonBody) throws Exception {
        SearchTaskReq req = SdkModelJson.fromJson(Objects.requireNonNull(jsonBody, "jsonBody"), SearchTaskReq.class);
        Client client = resolveClient(appKey);
        return executor.execute("approval.v4.task.search", appKey, "", () -> client.approval().v4().task().search(req));
    }

    public ResubmitTaskResp resubmitTask(String appKey, String jsonBody) throws Exception {
        ResubmitTaskReq req = SdkModelJson.fromJson(Objects.requireNonNull(jsonBody, "jsonBody"), ResubmitTaskReq.class);
        Client client = resolveClient(appKey);
        return executor.execute("approval.v4.task.resubmit", appKey, "", () -> client.approval().v4().task().resubmit(req));
    }

    private Client resolveClient(String appKey) {
        if (appKey == null || appKey.isBlank()) {
            return registry.primary();
        }
        return registry.get(appKey);
    }
}
