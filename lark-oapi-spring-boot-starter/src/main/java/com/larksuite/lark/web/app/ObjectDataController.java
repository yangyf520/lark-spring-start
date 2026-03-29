package com.larksuite.lark.web.app;

import com.fasterxml.jackson.databind.JsonNode;
import com.larksuite.lark.app.service.ObjectDataService;
import com.larksuite.lark.core.common.LarkApi;
import com.larksuite.lark.starter.condition.ConditionalOnStarterRestApi;
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
 * 低代码自定义对象记录 API（与 {@code /objects/_user}、{@code /objects/_department} 并列）。
 * 路径变量为对象 {@code api_name}，请求体与 ae-openapi 文档一致。
 */
@LarkApi
@RestController
@ConditionalOnStarterRestApi
@RequestMapping(
        path = "/lark/app/data/objects/{objectApiName:^(?!_user$|_department$).+}",
        produces = MediaType.APPLICATION_JSON_VALUE
)
public class ObjectDataController {

    private final ObjectDataService objectDataService;

    public ObjectDataController(ObjectDataService objectDataService) {
        this.objectDataService = objectDataService;
    }

    @PostMapping(path = "/records/batch-create", consumes = MediaType.APPLICATION_JSON_VALUE)
    public JsonNode batchCreate(
            @PathVariable String objectApiName,
            @RequestParam(required = false) String appKey,
            @RequestBody JsonNode body
    ) throws Exception {
        return objectDataService.batchCreate(appKey, objectApiName, body);
    }

    @PostMapping(path = "/records/create", consumes = MediaType.APPLICATION_JSON_VALUE)
    public JsonNode createRecord(
            @PathVariable String objectApiName,
            @RequestParam(required = false) String appKey,
            @RequestBody JsonNode body
    ) throws Exception {
        return objectDataService.createRecord(appKey, objectApiName, body);
    }

    @PatchMapping(path = "/records/batch-update", consumes = MediaType.APPLICATION_JSON_VALUE)
    public JsonNode batchUpdate(
            @PathVariable String objectApiName,
            @RequestParam(required = false) String appKey,
            @RequestBody JsonNode body
    ) throws Exception {
        return objectDataService.batchUpdate(appKey, objectApiName, body);
    }

    @DeleteMapping(path = "/records/batch-delete", consumes = MediaType.APPLICATION_JSON_VALUE)
    public JsonNode batchDelete(
            @PathVariable String objectApiName,
            @RequestParam(required = false) String appKey,
            @RequestBody JsonNode body
    ) throws Exception {
        return objectDataService.batchDelete(appKey, objectApiName, body);
    }

    @PostMapping(path = "/records/query", consumes = MediaType.APPLICATION_JSON_VALUE)
    public JsonNode query(
            @PathVariable String objectApiName,
            @RequestParam(required = false) String appKey,
            @RequestBody JsonNode body
    ) throws Exception {
        return objectDataService.query(appKey, objectApiName, body);
    }
}
