package com.larksuite.lark.web.app;

import com.fasterxml.jackson.databind.JsonNode;
import com.larksuite.lark.app.service.DepartmentService;
import com.larksuite.lark.app.vo.data.department.DepartmentCreateVo;
import com.larksuite.lark.app.vo.data.department.DepartmentDeleteVo;
import com.larksuite.lark.app.vo.data.department.DepartmentQueryVo;
import com.larksuite.lark.app.vo.data.department.DepartmentUpdateVo;
import com.larksuite.lark.core.common.LarkApi;
import com.larksuite.lark.starter.condition.ConditionalOnStarterRestApi;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@LarkApi
@RestController
@ConditionalOnStarterRestApi
@RequestMapping(path = "/lark/app/data/objects/_department", produces = MediaType.APPLICATION_JSON_VALUE)
public class DepartmentController {

    private final DepartmentService departmentService;

    public DepartmentController(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    /**
     * 批量新增部门记录（低代码 App 数据表）。
     * @param req 请求体：records（记录数组，每条通常含 fields）
     */
    @PostMapping(path = "/records/batch-create", consumes = MediaType.APPLICATION_JSON_VALUE)
    public JsonNode batchCreate(
            @RequestParam(required = false) String appKey,
            @RequestBody DepartmentCreateVo req
    ) throws Exception {
        return departmentService.batchCreate(appKey, req);
    }

    /**
     * 批量更新部门记录（低代码 App 数据表）。
     * @param req 请求体：records（记录数组，每条通常含 id + fields）
     */
    @PatchMapping(path = "/records/batch-update", consumes = MediaType.APPLICATION_JSON_VALUE)
    public JsonNode batchUpdate(
            @RequestParam(required = false) String appKey,
            @RequestBody DepartmentUpdateVo req
    ) throws Exception {
        return departmentService.batchUpdate(appKey, req);
    }

    /**
     * 批量删除部门记录（低代码 App 数据表）。
     * @param req 请求体：ids（记录 id 数组）
     */
    @DeleteMapping(path = "/records/batch-delete", consumes = MediaType.APPLICATION_JSON_VALUE)
    public JsonNode batchDelete(
            @RequestParam(required = false) String appKey,
            @RequestBody DepartmentDeleteVo req
    ) throws Exception {
        return departmentService.batchDelete(appKey, req);
    }

    /**
     * 查询部门记录（低代码 App 数据表）。
     * @param req 请求体：filter/order_by/select/group_by/page_size/offset/page_token/need_total_count/query_deleted_record（按需）
     */
    @PostMapping(path = "/records/query", consumes = MediaType.APPLICATION_JSON_VALUE)
    public JsonNode query(
            @RequestParam(required = false) String appKey,
            @RequestBody DepartmentQueryVo req
    ) throws Exception {
        return departmentService.query(appKey, req);
    }
}

