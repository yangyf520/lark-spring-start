package com.larksuite.lark.service;

import com.lark.oapi.Client;
import com.lark.oapi.service.contact.v3.model.BatchGetIdUserReq;
import com.lark.oapi.service.contact.v3.model.BatchGetIdUserReqBody;
import com.lark.oapi.service.contact.v3.model.BatchGetIdUserResp;
import com.lark.oapi.service.contact.v3.model.GetUserReq;
import com.lark.oapi.service.contact.v3.model.GetUserResp;
import com.lark.oapi.service.contact.v3.model.ListDepartmentReq;
import com.lark.oapi.service.contact.v3.model.ListDepartmentResp;
import com.larksuite.lark.oapi.spring.OapiClientRegistry;

/**
 * 通讯录 contact v3：用户、部门、批量换 ID。
 * <p>
 * 返回完整 SDK Resp；经 {@link com.larksuite.lark.core.advice.LarkApiResponseBodyAdvice} 写出 HTTP 时再解包为 {@link com.larksuite.lark.api.dto.ApiResponse}。
 * 若其它模块直接注入调用，请自行根据 {@code success()} / {@code getData()} 处理。
 */
public class LarkContactService {

    private final OapiClientRegistry clientRegistry;

    public LarkContactService(OapiClientRegistry clientRegistry) {
        this.clientRegistry = clientRegistry;
    }

    public GetUserResp getUser(String appKey, String userId, String userIdType, String departmentIdType) throws Exception {
        Client client = resolveClient(appKey);
        GetUserReq req = GetUserReq.newBuilder()
                .userId(userId)
                .userIdType(userIdType == null || userIdType.isBlank() ? "open_id" : userIdType)
                .departmentIdType(departmentIdType == null || departmentIdType.isBlank() ? "open_department_id" : departmentIdType)
                .build();
        return client.contact().v3().user().get(req);
    }

    public ListDepartmentResp listDepartments(
            String appKey,
            String parentDepartmentId,
            Boolean fetchChild,
            Integer pageSize,
            String pageToken,
            String userIdType,
            String departmentIdType
    ) throws Exception {
        Client client = resolveClient(appKey);
        ListDepartmentReq req = ListDepartmentReq.newBuilder()
                .parentDepartmentId(parentDepartmentId == null || parentDepartmentId.isBlank() ? "0" : parentDepartmentId)
                .fetchChild(fetchChild != null && fetchChild)
                .pageSize(pageSize == null || pageSize <= 0 ? 20 : pageSize)
                .pageToken(pageToken)
                .userIdType(userIdType == null || userIdType.isBlank() ? "open_id" : userIdType)
                .departmentIdType(departmentIdType == null || departmentIdType.isBlank() ? "open_department_id" : departmentIdType)
                .build();
        return client.contact().v3().department().list(req);
    }

    public BatchGetIdUserResp batchGetId(String appKey, String userIdType, String[] emails, String[] mobiles, Boolean includeResigned) throws Exception {
        Client client = resolveClient(appKey);
        BatchGetIdUserReq req = BatchGetIdUserReq.newBuilder()
                .userIdType(userIdType == null || userIdType.isBlank() ? "open_id" : userIdType)
                .batchGetIdUserReqBody(BatchGetIdUserReqBody.newBuilder()
                        .emails(emails == null ? new String[0] : emails)
                        .mobiles(mobiles == null ? new String[0] : mobiles)
                        .includeResigned(includeResigned != null && includeResigned)
                        .build())
                .build();
        return client.contact().v3().user().batchGetId(req);
    }

    private Client resolveClient(String appKey) {
        if (appKey == null || appKey.isBlank()) {
            return clientRegistry.primary();
        }
        return clientRegistry.get(appKey);
    }
}
