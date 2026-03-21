package com.larksuite.lark.service;

import com.lark.oapi.Client;
import com.lark.oapi.service.approval.v4.model.CreateInstanceReq;
import com.lark.oapi.service.approval.v4.model.CreateInstanceResp;
import com.lark.oapi.service.approval.v4.model.InstanceCreate;
import com.lark.oapi.service.approval.v4.model.GetApprovalReq;
import com.lark.oapi.service.approval.v4.model.GetApprovalResp;
import com.lark.oapi.service.approval.v4.model.GetInstanceReq;
import com.lark.oapi.service.approval.v4.model.GetInstanceResp;
import com.larksuite.lark.oapi.spring.OapiClientRegistry;
import com.larksuite.lark.support.LarkApiExecutor;

/** 审批 v4：定义、实例、创建。 */
public class LarkApprovalService {

    private final OapiClientRegistry registry;
    private final LarkApiExecutor executor;

    public LarkApprovalService(OapiClientRegistry registry, LarkApiExecutor executor) {
        this.registry = registry;
        this.executor = executor;
    }

    /** 获取审批定义。 */
    public GetApprovalResp getApproval(String appKey, String approvalCode) throws Exception {
        Client client = resolveClient(appKey);
        GetApprovalReq req = GetApprovalReq.newBuilder()
                .approvalCode(approvalCode)
                .build();
        return executor.execute(() -> client.approval().v4().approval().get(req));
    }

    /** 获取审批实例。 */
    public GetInstanceResp getInstance(String appKey, String instanceId, String userId, String userIdType) throws Exception {
        Client client = resolveClient(appKey);
        GetInstanceReq req = GetInstanceReq.newBuilder()
                .instanceId(instanceId)
                .userId(userId)
                .userIdType(userIdType == null || userIdType.isBlank() ? "user_id" : userIdType)
                .build();
        return executor.execute(() -> client.approval().v4().instance().get(req));
    }

    /** 创建审批实例。 */
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
