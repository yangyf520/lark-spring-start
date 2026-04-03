package com.larksuite.lark.app.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.larksuite.lark.app.service.DepartmentService;
import com.larksuite.lark.app.vo.data.department.DepartmentCreateVo;
import com.larksuite.lark.app.vo.data.department.DepartmentDeleteVo;
import com.larksuite.lark.app.vo.data.department.DepartmentQueryVo;
import com.larksuite.lark.app.vo.data.department.DepartmentUpdateVo;
import com.larksuite.lark.common.annotation.LarkApi;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * AE 内置部门表（{@code _department}）数据接口。
 * <p>
 * 成功与异常由全局 Advice 与 Service 统一处理。
 */
@LarkApi
@RestController
@RequestMapping(path = "/lark/app/data/objects/_department", produces = MediaType.APPLICATION_JSON_VALUE)
public class DepartmentController {

    private final DepartmentService departmentService;

    /**
     * 构造注入。
     * <p>
     * @param departmentService 部门数据服务
     */
    public DepartmentController(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    /**
     * 批量新增部门记录。
     * <p>
     * @param appKey AE 应用键，可空（使用 primary）
     * @param req    请求体：{@code records} 等
     * @return AE API 响应 JSON
     */
    @PostMapping(path = "/records/batch-create", consumes = MediaType.APPLICATION_JSON_VALUE)
    public JsonNode batchCreate(
            @RequestParam(required = false) String appKey,
            @RequestBody DepartmentCreateVo req
    ) throws Exception {
        return departmentService.batchCreate(appKey, req);
    }

    /**
     * 批量更新部门记录。
     * <p>
     * @param appKey AE 应用键，可空（使用 primary）
     * @param req    请求体：{@code records}（含 id + fields）
     * @return AE API 响应 JSON
     */
    @PatchMapping(path = "/records/batch-update", consumes = MediaType.APPLICATION_JSON_VALUE)
    public JsonNode batchUpdate(
            @RequestParam(required = false) String appKey,
            @RequestBody DepartmentUpdateVo req
    ) throws Exception {
        return departmentService.batchUpdate(appKey, req);
    }

    /**
     * 批量删除部门记录。
     * <p>
     * @param appKey AE 应用键，可空（使用 primary）
     * @param req    请求体：{@code ids}
     * @return AE API 响应 JSON
     */
    @DeleteMapping(path = "/records/batch-delete", consumes = MediaType.APPLICATION_JSON_VALUE)
    public JsonNode batchDelete(
            @RequestParam(required = false) String appKey,
            @RequestBody DepartmentDeleteVo req
    ) throws Exception {
        return departmentService.batchDelete(appKey, req);
    }

    /**
     * 条件查询部门记录。
     * <p>
     * @param appKey AE 应用键，可空（使用 primary）
     * @param req    请求体：filter / order_by / select / 分页等
     * @return AE API 响应 JSON
     */
    @PostMapping(path = "/records/query", consumes = MediaType.APPLICATION_JSON_VALUE)
    public JsonNode query(
            @RequestParam(required = false) String appKey,
            @RequestBody DepartmentQueryVo req
    ) throws Exception {
        return departmentService.query(appKey, req);
    }
}
