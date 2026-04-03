package com.larksuite.lark.app.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.larksuite.lark.app.core.api.AppApiClient;
import com.larksuite.lark.app.core.api.AppApiClientRegistry;
import com.larksuite.lark.app.vo.data.department.DepartmentCreateVo;
import com.larksuite.lark.app.vo.data.department.DepartmentDeleteVo;
import com.larksuite.lark.app.vo.data.department.DepartmentQueryVo;
import com.larksuite.lark.app.vo.data.department.DepartmentUpdateVo;
import org.springframework.http.HttpMethod;

/** AE 部门数据接口封装。 */
public class DepartmentService {

    private static final String RECORDS_BATCH_PATH = "/v1/data/namespaces/%s/objects/_department/records_batch";
    private static final String RECORDS_QUERY_PATH = "/v1/data/namespaces/%s/objects/_department/records_query";

    private final AppApiClientRegistry registry;

    public DepartmentService(AppApiClientRegistry registry) {
        this.registry = registry;
    }

    /** 批量新增部门记录。 */
    public JsonNode batchCreate(String appKey, DepartmentCreateVo req) throws Exception {
        AppApiClient client = registry.getClient(appKey);
        String namespace = namespaceOrThrow(client);
        return client.request(HttpMethod.POST, RECORDS_BATCH_PATH.formatted(namespace), req);
    }

    /** 批量更新部门记录。 */
    public JsonNode batchUpdate(String appKey, DepartmentUpdateVo req) throws Exception {
        AppApiClient client = registry.getClient(appKey);
        String namespace = namespaceOrThrow(client);
        return client.request(HttpMethod.PATCH, RECORDS_BATCH_PATH.formatted(namespace), req);
    }

    /** 批量删除部门记录。 */
    public JsonNode batchDelete(String appKey, DepartmentDeleteVo req) throws Exception {
        AppApiClient client = registry.getClient(appKey);
        String namespace = namespaceOrThrow(client);
        return client.request(HttpMethod.DELETE, RECORDS_BATCH_PATH.formatted(namespace), req);
    }

    /** 查询部门记录。 */
    public JsonNode query(String appKey, DepartmentQueryVo req) throws Exception {
        AppApiClient client = registry.getClient(appKey);
        String namespace = namespaceOrThrow(client);
        return client.request(HttpMethod.POST, RECORDS_QUERY_PATH.formatted(namespace), req);
    }

    private String namespaceOrThrow(AppApiClient client) {
        String namespace = client.namespace();
        if (namespace == null || namespace.isBlank()) {
            throw new IllegalStateException("lark.apass.apps.<appKey>.namespace is required");
        }
        return namespace;
    }
}

