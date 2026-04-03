package com.larksuite.lark.app.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.larksuite.lark.app.service.ObjectDataService;
import com.larksuite.lark.common.annotation.LarkApi;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 低代码自定义对象记录 API（与 {@code _user}、{@code _department} 并列）。
 * <p>
 * 路径变量为对象 {@code api_name}，请求体与 AE API 文档一致。
 */
@LarkApi
@RestController
@RequestMapping(
        path = "/lark/app/data/objects/{objectApiName:^(?!_user$|_department$).+}",
        produces = MediaType.APPLICATION_JSON_VALUE
)
public class ObjectDataController {

    private final ObjectDataService objectDataService;

    /**
     * 构造注入。
     * <p>
     * @param objectDataService 对象实例数据服务
     */
    public ObjectDataController(ObjectDataService objectDataService) {
        this.objectDataService = objectDataService;
    }

    /**
     * 批量新增记录。
     * <p>
     * @param objectApiName 对象 api_name
     * @param appKey        AE 应用键，可空（使用 primary）
     * @param body          请求体 JSON
     * @return AE API 响应 JSON
     */
    @PostMapping(path = "/records/batch-create", consumes = MediaType.APPLICATION_JSON_VALUE)
    public JsonNode batchCreate(
            @PathVariable String objectApiName,
            @RequestParam(required = false) String appKey,
            @RequestBody JsonNode body
    ) throws Exception {
        return objectDataService.batchCreate(appKey, objectApiName, body);
    }

    /**
     * 单条新增记录。
     * <p>
     * @param objectApiName 对象 api_name
     * @param appKey        AE 应用键，可空（使用 primary）
     * @param body          请求体 JSON
     * @return AE API 响应 JSON
     */
    @PostMapping(path = "/records/create", consumes = MediaType.APPLICATION_JSON_VALUE)
    public JsonNode createRecord(
            @PathVariable String objectApiName,
            @RequestParam(required = false) String appKey,
            @RequestBody JsonNode body
    ) throws Exception {
        return objectDataService.createRecord(appKey, objectApiName, body);
    }

    /**
     * 批量更新记录。
     * <p>
     * @param objectApiName 对象 api_name
     * @param appKey        AE 应用键，可空（使用 primary）
     * @param body          请求体 JSON
     * @return AE API 响应 JSON
     */
    @PatchMapping(path = "/records/batch-update", consumes = MediaType.APPLICATION_JSON_VALUE)
    public JsonNode batchUpdate(
            @PathVariable String objectApiName,
            @RequestParam(required = false) String appKey,
            @RequestBody JsonNode body
    ) throws Exception {
        return objectDataService.batchUpdate(appKey, objectApiName, body);
    }

    /**
     * 批量删除记录。
     * <p>
     * @param objectApiName 对象 api_name
     * @param appKey        AE 应用键，可空（使用 primary）
     * @param body          请求体 JSON
     * @return AE API 响应 JSON
     */
    @DeleteMapping(path = "/records/batch-delete", consumes = MediaType.APPLICATION_JSON_VALUE)
    public JsonNode batchDelete(
            @PathVariable String objectApiName,
            @RequestParam(required = false) String appKey,
            @RequestBody JsonNode body
    ) throws Exception {
        return objectDataService.batchDelete(appKey, objectApiName, body);
    }

    /**
     * 条件查询记录。
     * <p>
     * @param objectApiName 对象 api_name
     * @param appKey        AE 应用键，可空（使用 primary）
     * @param body          请求体 JSON
     * @return AE API 响应 JSON
     */
    @PostMapping(path = "/records/query", consumes = MediaType.APPLICATION_JSON_VALUE)
    public JsonNode query(
            @PathVariable String objectApiName,
            @RequestParam(required = false) String appKey,
            @RequestBody JsonNode body
    ) throws Exception {
        return objectDataService.query(appKey, objectApiName, body);
    }
}
