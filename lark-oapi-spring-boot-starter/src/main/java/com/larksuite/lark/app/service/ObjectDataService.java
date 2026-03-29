package com.larksuite.lark.app.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.larksuite.lark.app.openapi.AppOpenApiClient;
import com.larksuite.lark.app.openapi.AppOpenApiClientRegistry;
import org.springframework.http.HttpMethod;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * 低代码 App 任意业务对象的记录 CRUD（路径中的对象 api_name 与 ae-openapi 一致）。
 */
public class ObjectDataService {

    private static final String RECORDS_BATCH_PATH = "/v1/data/namespaces/%s/objects/%s/records_batch";
    private static final String RECORDS_PATH = "/v1/data/namespaces/%s/objects/%s/records";
    private static final String RECORDS_QUERY_PATH = "/v1/data/namespaces/%s/objects/%s/records_query";

    private final AppOpenApiClientRegistry registry;

    public ObjectDataService(AppOpenApiClientRegistry registry) {
        this.registry = registry;
    }

    public JsonNode batchCreate(String appKey, String objectApiName, JsonNode body) throws Exception {
        return request(appKey, objectApiName, HttpMethod.POST, RECORDS_BATCH_PATH, body);
    }

    public JsonNode createRecord(String appKey, String objectApiName, JsonNode body) throws Exception {
        return request(appKey, objectApiName, HttpMethod.POST, RECORDS_PATH, body);
    }

    public JsonNode batchUpdate(String appKey, String objectApiName, JsonNode body) throws Exception {
        return request(appKey, objectApiName, HttpMethod.PATCH, RECORDS_BATCH_PATH, body);
    }

    public JsonNode batchDelete(String appKey, String objectApiName, JsonNode body) throws Exception {
        return request(appKey, objectApiName, HttpMethod.DELETE, RECORDS_BATCH_PATH, body);
    }

    public JsonNode query(String appKey, String objectApiName, JsonNode body) throws Exception {
        return request(appKey, objectApiName, HttpMethod.POST, RECORDS_QUERY_PATH, body);
    }

    private JsonNode request(
            String appKey,
            String objectApiName,
            HttpMethod method,
            String pathTemplate,
            JsonNode body
    ) throws Exception {
        AppOpenApiClient client = registry.getClient(appKey);
        String namespace = namespaceOrThrow(client);
        String encoded = encodePathSegment(objectApiName);
        String path = pathTemplate.formatted(namespace, encoded);
        return client.request(method, path, body);
    }

    private static String encodePathSegment(String objectApiName) {
        if (objectApiName == null || objectApiName.isBlank()) {
            throw new IllegalArgumentException("objectApiName is blank");
        }
        return URLEncoder.encode(objectApiName, StandardCharsets.UTF_8).replace("+", "%20");
    }

    private String namespaceOrThrow(AppOpenApiClient client) {
        String namespace = client.namespace();
        if (namespace == null || namespace.isBlank()) {
            throw new IllegalStateException("lark.apass.apps.<appKey>.namespace is required");
        }
        return namespace;
    }
}
