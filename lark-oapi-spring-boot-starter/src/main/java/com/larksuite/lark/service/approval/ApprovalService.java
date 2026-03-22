package com.larksuite.lark.service.approval;

import com.lark.oapi.Client;
import com.lark.oapi.service.approval.v4.model.CreateInstanceReq;
import com.lark.oapi.service.approval.v4.model.CreateInstanceResp;
import com.lark.oapi.service.approval.v4.model.GetApprovalReq;
import com.lark.oapi.service.approval.v4.model.GetApprovalResp;
import com.lark.oapi.service.approval.v4.model.GetInstanceReq;
import com.lark.oapi.service.approval.v4.model.GetInstanceResp;
import com.lark.oapi.service.approval.v4.model.InstanceCreate;
import com.larksuite.lark.oapi.spring.OapiClientRegistry;
import com.larksuite.lark.support.ApiExecutor;

/** 审批 v4：定义、实例、创建；返回完整 SDK Resp。 */
public class ApprovalService {

    private final OapiClientRegistry registry;
    private final ApiExecutor executor;

    public ApprovalService(OapiClientRegistry registry, ApiExecutor executor) {
        this.registry = registry;
        this.executor = executor;
    }

    public GetApprovalResp getApproval(String appKey, String approvalCode) throws Exception {
        Client client = resolveClient(appKey);
        GetApprovalReq req = GetApprovalReq.newBuilder()
                .approvalCode(approvalCode)
                .build();
        return executor.execute(() -> client.approval().v4().approval().get(req));
    }

    public GetInstanceResp getInstance(String appKey, String instanceId, String userId, String userIdType) throws Exception {
        Client client = resolveClient(appKey);
        GetInstanceReq req = GetInstanceReq.newBuilder()
                .instanceId(instanceId)
                .userId(userId)
                .userIdType(userIdType == null || userIdType.isBlank() ? "user_id" : userIdType)
                .build();
        return executor.execute(() -> client.approval().v4().instance().get(req));
    }

    public CreateInstanceResp createInstance(String appKey, InstanceCreate body) throws Exception {
        Client client = resolveClient(appKey);
        CreateInstanceReq req = CreateInstanceReq.newBuilder()
                .instanceCreate(body)
                .build();
        return executor.execute(() -> client.approval().v4().instance().create(req));
    }

    private Client resolveClient(String appKey) {
        if (appKey == null || appKey.isBlank()) {
            return registry.primary();
        }
        return registry.get(appKey);
    }
}
