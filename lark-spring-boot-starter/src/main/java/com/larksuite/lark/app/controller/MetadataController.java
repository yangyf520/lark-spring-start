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

@LarkApi
@RestController
@RequestMapping(path = "/lark/app/objects", produces = MediaType.APPLICATION_JSON_VALUE)
/** AE 对象元数据相关 HTTP 接口。 */
public class MetadataController {

    private final ObjectMetadataService objectMetadataService;

    public MetadataController(ObjectMetadataService objectMetadataService) {
        this.objectMetadataService = objectMetadataService;
    }

    /**
     * 批量创建对象元数据（低代码 App：对象定义）。
     * @param req 请求体：objects（对象定义数组）
     */
    @PostMapping(path = "/batch-create", consumes = MediaType.APPLICATION_JSON_VALUE)
    public JsonNode batchCreate(
            @RequestParam(required = false) String appKey,
            @RequestBody ObjectMetadataBatchCreateVo req
    ) throws Exception {
        return objectMetadataService.batchCreate(appKey, req);
    }

    /**
     * 批量更新对象元数据（低代码 App：对象定义）。
     * @param req 请求体：objects（对象定义数组）
     */
    @PostMapping(path = "/batch-update", consumes = MediaType.APPLICATION_JSON_VALUE)
    public JsonNode batchUpdate(
            @RequestParam(required = false) String appKey,
            @RequestBody ObjectMetadataBatchUpdateVo req
    ) throws Exception {
        return objectMetadataService.batchUpdate(appKey, req);
    }

    /**
     * 批量删除对象元数据（低代码 App：对象定义）。
     * @param req 请求体：object_api_names（对象 api_name 数组；内部会转为对端要求的 api_names）
     */
    @PostMapping(path = "/batch-delete", consumes = MediaType.APPLICATION_JSON_VALUE)
    public JsonNode batchDelete(
            @RequestParam(required = false) String appKey,
            @RequestBody ObjectMetadataBatchDeleteVo req
    ) throws Exception {
        return objectMetadataService.batchDelete(appKey, req);
    }
}
