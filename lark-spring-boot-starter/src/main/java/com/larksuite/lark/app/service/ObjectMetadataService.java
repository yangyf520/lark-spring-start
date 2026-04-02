package com.larksuite.lark.app.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.larksuite.lark.app.core.openapi.AppOpenApiClient;
import com.larksuite.lark.app.core.openapi.AppOpenApiClientRegistry;
import com.larksuite.lark.app.vo.metadata.ObjectMetadataBatchCreateVo;
import com.larksuite.lark.app.vo.metadata.ObjectMetadataBatchDeleteVo;
import com.larksuite.lark.app.vo.metadata.ObjectMetadataBatchUpdateVo;
import org.springframework.http.HttpMethod;

/** AE 对象元数据接口封装。 */
public class ObjectMetadataService {

    private static final String BATCH_CREATE_PATH = "/v1/namespaces/%s/objects/batch_create";
    private static final String BATCH_UPDATE_PATH = "/v1/namespaces/%s/objects/batch_update";
    private static final String BATCH_DELETE_PATH = "/v1/namespaces/%s/objects/batch_delete";

    private final AppOpenApiClientRegistry registry;

    public ObjectMetadataService(AppOpenApiClientRegistry registry) {
        this.registry = registry;
    }

    /** 批量创建对象元数据。 */
    public JsonNode batchCreate(String appKey, ObjectMetadataBatchCreateVo req) throws Exception {
        AppOpenApiClient client = registry.getClient(appKey);
        String namespace = namespaceOrThrow(client);
        return client.request(HttpMethod.POST, BATCH_CREATE_PATH.formatted(namespace), req);
    }

    /** 批量更新对象元数据。 */
    public JsonNode batchUpdate(String appKey, ObjectMetadataBatchUpdateVo req) throws Exception {
        AppOpenApiClient client = registry.getClient(appKey);
        String namespace = namespaceOrThrow(client);
        return client.request(HttpMethod.POST, BATCH_UPDATE_PATH.formatted(namespace), req);
    }

    /** 批量删除对象元数据。 */
    public JsonNode batchDelete(String appKey, ObjectMetadataBatchDeleteVo req) throws Exception {
        AppOpenApiClient client = registry.getClient(appKey);
        String namespace = namespaceOrThrow(client);
        return client.request(HttpMethod.POST, BATCH_DELETE_PATH.formatted(namespace), req);
    }

    private String namespaceOrThrow(AppOpenApiClient client) {
        String namespace = client.namespace();
        if (namespace == null || namespace.isBlank()) {
            throw new IllegalStateException("lark.apass.apps.<appKey>.namespace is required");
        }
        return namespace;
    }
}
