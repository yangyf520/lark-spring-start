package com.larksuite.lark.app.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.larksuite.lark.app.core.openapi.AppOpenApiClient;
import com.larksuite.lark.app.core.openapi.AppOpenApiClientRegistry;
import com.larksuite.lark.app.vo.data.user.UserCreateVo;
import com.larksuite.lark.app.vo.data.user.UserDeleteVo;
import com.larksuite.lark.app.vo.data.user.UserQueryVo;
import com.larksuite.lark.app.vo.data.user.UserUpdateVo;
import org.springframework.http.HttpMethod;

/** AE 用户数据接口封装。 */
public class UserService {

    private static final String RECORDS_BATCH_PATH = "/v1/data/namespaces/%s/objects/_user/records_batch";
    private static final String RECORDS_PATH = "/v1/data/namespaces/%s/objects/_user/records";
    private static final String RECORDS_QUERY_PATH = "/v1/data/namespaces/%s/objects/_user/records_query";

    private final AppOpenApiClientRegistry registry;

    public UserService(AppOpenApiClientRegistry registry) {
        this.registry = registry;
    }

    /** 批量新增用户记录。 */
    public JsonNode batchCreate(String appKey, UserCreateVo req) throws Exception {
        AppOpenApiClient client = registry.getClient(appKey);
        String namespace = namespaceOrThrow(client);
        return client.request(HttpMethod.POST, RECORDS_BATCH_PATH.formatted(namespace), req);
    }

    /** 新增单条用户记录。 */
    public JsonNode createRecord(String appKey, com.larksuite.lark.app.vo.data.user.UserRecordCreateVo req) throws Exception {
        AppOpenApiClient client = registry.getClient(appKey);
        String namespace = namespaceOrThrow(client);
        return client.request(HttpMethod.POST, RECORDS_PATH.formatted(namespace), req);
    }

    /** 批量更新用户记录。 */
    public JsonNode batchUpdate(String appKey, UserUpdateVo req) throws Exception {
        AppOpenApiClient client = registry.getClient(appKey);
        String namespace = namespaceOrThrow(client);
        return client.request(HttpMethod.PATCH, RECORDS_BATCH_PATH.formatted(namespace), req);
    }

    /** 批量删除用户记录。 */
    public JsonNode batchDelete(String appKey, UserDeleteVo req) throws Exception {
        AppOpenApiClient client = registry.getClient(appKey);
        String namespace = namespaceOrThrow(client);
        return client.request(HttpMethod.DELETE, RECORDS_BATCH_PATH.formatted(namespace), req);
    }

    /** 查询用户记录。 */
    public JsonNode query(String appKey, UserQueryVo req) throws Exception {
        AppOpenApiClient client = registry.getClient(appKey);
        String namespace = namespaceOrThrow(client);
        return client.request(HttpMethod.POST, RECORDS_QUERY_PATH.formatted(namespace), req);
    }

    private String namespaceOrThrow(AppOpenApiClient client) {
        String namespace = client.namespace();
        if (namespace == null || namespace.isBlank()) {
            throw new IllegalStateException("lark.apass.apps.<appKey>.namespace is required");
        }
        return namespace;
    }
}

