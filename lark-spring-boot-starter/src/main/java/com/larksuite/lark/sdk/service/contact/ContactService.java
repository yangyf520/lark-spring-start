package com.larksuite.lark.sdk.service.contact;

import com.lark.oapi.Client;
import com.lark.oapi.service.contact.v3.model.BatchGetIdUserReq;
import com.lark.oapi.service.contact.v3.model.BatchGetIdUserReqBody;
import com.lark.oapi.service.contact.v3.model.BatchGetIdUserResp;
import com.lark.oapi.service.contact.v3.model.CreateDepartmentReq;
import com.lark.oapi.service.contact.v3.model.CreateDepartmentResp;
import com.lark.oapi.service.contact.v3.model.CreateUserReq;
import com.lark.oapi.service.contact.v3.model.CreateUserResp;
import com.lark.oapi.service.contact.v3.model.DeleteDepartmentReq;
import com.lark.oapi.service.contact.v3.model.DeleteDepartmentResp;
import com.lark.oapi.service.contact.v3.model.DeleteUserReq;
import com.lark.oapi.service.contact.v3.model.DeleteUserResp;
import com.lark.oapi.service.contact.v3.model.Department;
import com.lark.oapi.service.contact.v3.model.GetDepartmentReq;
import com.lark.oapi.service.contact.v3.model.GetDepartmentResp;
import com.lark.oapi.service.contact.v3.model.GetUserReq;
import com.lark.oapi.service.contact.v3.model.GetUserResp;
import com.lark.oapi.service.contact.v3.model.ListDepartmentReq;
import com.lark.oapi.service.contact.v3.model.ListDepartmentResp;
import com.lark.oapi.service.contact.v3.model.ListUserReq;
import com.lark.oapi.service.contact.v3.model.ListUserResp;
import com.lark.oapi.service.contact.v3.model.UpdateDepartmentReq;
import com.lark.oapi.service.contact.v3.model.UpdateDepartmentResp;
import com.lark.oapi.service.contact.v3.model.UpdateUserReq;
import com.lark.oapi.service.contact.v3.model.UpdateUserResp;
import com.lark.oapi.service.contact.v3.model.User;
import com.larksuite.lark.common.support.ApiExecutor;
import com.larksuite.lark.sdk.core.ClientRegistry;

/**
 * 通讯录 contact v3：用户、部门、批量换 ID。
 * <p>
 * 返回完整 SDK Resp 正文；异常由宿主应用全局异常处理器统一处理。
 * 若其它模块直接注入调用，请自行根据 {@code success()} / {@code getData()} 处理。
 */
public class ContactService {

    private final ClientRegistry clientRegistry;
    private final ApiExecutor executor;

    public ContactService(ClientRegistry clientRegistry, ApiExecutor executor) {
        this.clientRegistry = clientRegistry;
        this.executor = executor;
    }

    public GetUserResp getUser(String appKey, String userId, String userIdType, String departmentIdType) throws Exception {
        Client client = resolveClient(appKey);
        GetUserReq req = GetUserReq.newBuilder()
                .userId(userId)
                .userIdType(userIdType == null || userIdType.isBlank() ? "open_id" : userIdType)
                .departmentIdType(departmentIdType == null || departmentIdType.isBlank() ? "open_department_id" : departmentIdType)
                .build();
        return executor.execute("contact.v3.user.get", appKey, "userId=" + userId, () -> client.contact().v3().user().get(req));
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
        return executor.execute("contact.v3.department.list", appKey, "parentDepartmentId=" + (parentDepartmentId == null ? "" : parentDepartmentId),
                () -> client.contact().v3().department().list(req));
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
        return executor.execute("contact.v3.user.batchGetId", appKey, "userIdType=" + (userIdType == null ? "" : userIdType),
                () -> client.contact().v3().user().batchGetId(req));
    }

    public ListUserResp listUsers(
            String appKey,
            String departmentId,
            Integer pageSize,
            String pageToken,
            String userIdType,
            String departmentIdType
    ) throws Exception {
        Client client = resolveClient(appKey);
        ListUserReq req = ListUserReq.newBuilder()
                .departmentId(departmentId == null || departmentId.isBlank() ? "0" : departmentId)
                .pageSize(pageSize == null || pageSize <= 0 ? 20 : pageSize)
                .pageToken(pageToken)
                .userIdType(userIdType == null || userIdType.isBlank() ? "open_id" : userIdType)
                .departmentIdType(departmentIdType == null || departmentIdType.isBlank() ? "open_department_id" : departmentIdType)
                .build();
        return executor.execute("contact.v3.user.list", appKey, "departmentId=" + (departmentId == null ? "" : departmentId),
                () -> client.contact().v3().user().list(req));
    }

    public CreateUserResp createUser(
            String appKey,
            String userIdType,
            String departmentIdType,
            String clientToken,
            User user
    ) throws Exception {
        Client client = resolveClient(appKey);
        CreateUserReq req = CreateUserReq.newBuilder()
                .userIdType(userIdType == null || userIdType.isBlank() ? "open_id" : userIdType)
                .departmentIdType(departmentIdType == null || departmentIdType.isBlank() ? "open_department_id" : departmentIdType)
                .clientToken(clientToken)
                .user(user)
                .build();
        return executor.execute("contact.v3.user.create", appKey, "clientToken=" + (clientToken == null ? "" : clientToken),
                () -> client.contact().v3().user().create(req));
    }

    public UpdateUserResp updateUser(
            String appKey,
            String userId,
            String userIdType,
            String departmentIdType,
            User user
    ) throws Exception {
        Client client = resolveClient(appKey);
        UpdateUserReq req = UpdateUserReq.newBuilder()
                .userId(userId)
                .userIdType(userIdType == null || userIdType.isBlank() ? "open_id" : userIdType)
                .departmentIdType(departmentIdType == null || departmentIdType.isBlank() ? "open_department_id" : departmentIdType)
                .user(user)
                .build();
        return executor.execute("contact.v3.user.update", appKey, "userId=" + userId,
                () -> client.contact().v3().user().update(req));
    }

    public DeleteUserResp deleteUser(String appKey, String userId, String userIdType) throws Exception {
        Client client = resolveClient(appKey);
        DeleteUserReq req = DeleteUserReq.newBuilder()
                .userId(userId)
                .userIdType(userIdType == null || userIdType.isBlank() ? "open_id" : userIdType)
                .build();
        return executor.execute("contact.v3.user.delete", appKey, "userId=" + userId,
                () -> client.contact().v3().user().delete(req));
    }

    public GetDepartmentResp getDepartment(
            String appKey,
            String departmentId,
            String userIdType,
            String departmentIdType
    ) throws Exception {
        Client client = resolveClient(appKey);
        GetDepartmentReq req = GetDepartmentReq.newBuilder()
                .departmentId(departmentId)
                .userIdType(userIdType == null || userIdType.isBlank() ? "open_id" : userIdType)
                .departmentIdType(departmentIdType == null || departmentIdType.isBlank() ? "open_department_id" : departmentIdType)
                .build();
        return executor.execute("contact.v3.department.get", appKey, "departmentId=" + departmentId,
                () -> client.contact().v3().department().get(req));
    }

    public CreateDepartmentResp createDepartment(
            String appKey,
            String userIdType,
            String departmentIdType,
            String clientToken,
            Department department
    ) throws Exception {
        Client client = resolveClient(appKey);
        CreateDepartmentReq req = CreateDepartmentReq.newBuilder()
                .userIdType(userIdType == null || userIdType.isBlank() ? "open_id" : userIdType)
                .departmentIdType(departmentIdType == null || departmentIdType.isBlank() ? "open_department_id" : departmentIdType)
                .clientToken(clientToken)
                .department(department)
                .build();
        return executor.execute("contact.v3.department.create", appKey, "clientToken=" + (clientToken == null ? "" : clientToken),
                () -> client.contact().v3().department().create(req));
    }

    public UpdateDepartmentResp updateDepartment(
            String appKey,
            String departmentId,
            String userIdType,
            String departmentIdType,
            Department department
    ) throws Exception {
        Client client = resolveClient(appKey);
        UpdateDepartmentReq req = UpdateDepartmentReq.newBuilder()
                .departmentId(departmentId)
                .userIdType(userIdType == null || userIdType.isBlank() ? "open_id" : userIdType)
                .departmentIdType(departmentIdType == null || departmentIdType.isBlank() ? "open_department_id" : departmentIdType)
                .department(department)
                .build();
        return executor.execute("contact.v3.department.update", appKey, "departmentId=" + departmentId,
                () -> client.contact().v3().department().update(req));
    }

    public DeleteDepartmentResp deleteDepartment(String appKey, String departmentId, String departmentIdType) throws Exception {
        Client client = resolveClient(appKey);
        DeleteDepartmentReq req = DeleteDepartmentReq.newBuilder()
                .departmentId(departmentId)
                .departmentIdType(departmentIdType == null || departmentIdType.isBlank() ? "open_department_id" : departmentIdType)
                .build();
        return executor.execute("contact.v3.department.delete", appKey, "departmentId=" + departmentId,
                () -> client.contact().v3().department().delete(req));
    }

    private Client resolveClient(String appKey) {
        if (appKey == null || appKey.isBlank()) {
            return clientRegistry.primary();
        }
        return clientRegistry.get(appKey);
    }
}
