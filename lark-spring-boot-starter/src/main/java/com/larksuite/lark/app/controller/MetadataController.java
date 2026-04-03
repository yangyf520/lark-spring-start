package com.larksuite.lark.app.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.larksuite.lark.app.service.ObjectMetadataService;
import com.larksuite.lark.app.vo.metadata.ObjectMetadataBatchCreateVo;
import com.larksuite.lark.app.vo.metadata.ObjectMetadataBatchDeleteVo;
import com.larksuite.lark.app.vo.metadata.ObjectMetadataBatchUpdateVo;
import com.larksuite.lark.common.annotation.LarkApi;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * AE 对象元数据（低代码 App：对象定义）。
 * <p>
 * 成功与异常由全局 Advice 与 Service 统一处理。
 */
@LarkApi
@RestController
@RequestMapping(path = "/lark/app/objects", produces = MediaType.APPLICATION_JSON_VALUE)
public class MetadataController {

    private final ObjectMetadataService objectMetadataService;

    /**
     * 构造注入。
     * <p>
     * @param objectMetadataService 对象元数据服务
     */
    public MetadataController(ObjectMetadataService objectMetadataService) {
        this.objectMetadataService = objectMetadataService;
    }

    /**
     * 批量创建对象元数据。
     * <p>
     * @param appKey AE 应用键，可空（使用 primary）
     * @param req    请求体：{@code objects} 对象定义数组
     * @return AE API 响应 JSON
     */
    @PostMapping(path = "/batch-create", consumes = MediaType.APPLICATION_JSON_VALUE)
    public JsonNode batchCreate(
            @RequestParam(required = false) String appKey,
            @RequestBody ObjectMetadataBatchCreateVo req
    ) throws Exception {
        return objectMetadataService.batchCreate(appKey, req);
    }

    /**
     * 批量更新对象元数据。
     * <p>
     * @param appKey AE 应用键，可空（使用 primary）
     * @param req    请求体：{@code objects} 对象定义数组
     * @return AE API 响应 JSON
     */
    @PostMapping(path = "/batch-update", consumes = MediaType.APPLICATION_JSON_VALUE)
    public JsonNode batchUpdate(
            @RequestParam(required = false) String appKey,
            @RequestBody ObjectMetadataBatchUpdateVo req
    ) throws Exception {
        return objectMetadataService.batchUpdate(appKey, req);
    }

    /**
     * 批量删除对象元数据。
     * <p>
     * @param appKey AE 应用键，可空（使用 primary）
     * @param req    请求体：{@code object_api_names}（内部会规范为对端 {@code api_names}）
     * @return AE API 响应 JSON
     */
    @PostMapping(path = "/batch-delete", consumes = MediaType.APPLICATION_JSON_VALUE)
    public JsonNode batchDelete(
            @RequestParam(required = false) String appKey,
            @RequestBody ObjectMetadataBatchDeleteVo req
    ) throws Exception {
        return objectMetadataService.batchDelete(appKey, req);
    }
}
