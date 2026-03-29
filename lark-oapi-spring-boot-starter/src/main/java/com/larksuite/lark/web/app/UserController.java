package com.larksuite.lark.web.app;

import com.fasterxml.jackson.databind.JsonNode;
import com.larksuite.lark.app.service.UserService;
import com.larksuite.lark.app.vo.data.user.UserCreateVo;
import com.larksuite.lark.app.vo.data.user.UserDeleteVo;
import com.larksuite.lark.app.vo.data.user.UserQueryVo;
import com.larksuite.lark.app.vo.data.user.UserRecordCreateVo;
import com.larksuite.lark.app.vo.data.user.UserUpdateVo;
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
@RequestMapping(path = "/lark/app/data/objects/_user", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 批量新增用户记录（低代码 App 数据表）。
     * @param req 请求体：records（记录数组，每条通常含 fields）
     */
    @PostMapping(path = "/records/batch-create", consumes = MediaType.APPLICATION_JSON_VALUE)
    public JsonNode batchCreate(
            @RequestParam(required = false) String appKey,
            @RequestBody UserCreateVo req
    ) throws Exception {
        return userService.batchCreate(appKey, req);
    }

    /**
     * 单条新增用户记录（低代码 App 数据表）。
     * @param req 请求体：record（单条记录对象）
     */
    @PostMapping(path = "/records/create", consumes = MediaType.APPLICATION_JSON_VALUE)
    public JsonNode createRecord(
            @RequestParam(required = false) String appKey,
            @RequestBody UserRecordCreateVo req
    ) throws Exception {
        return userService.createRecord(appKey, req);
    }

    /**
     * 批量更新用户记录（低代码 App 数据表）。
     * @param req 请求体：records（记录数组，每条通常含 id + fields）
     */
    @PatchMapping(path = "/records/batch-update", consumes = MediaType.APPLICATION_JSON_VALUE)
    public JsonNode batchUpdate(
            @RequestParam(required = false) String appKey,
            @RequestBody UserUpdateVo req
    ) throws Exception {
        return userService.batchUpdate(appKey, req);
    }

    /**
     * 批量删除用户记录（低代码 App 数据表）。
     * @param req 请求体：ids（记录 id 数组）
     */
    @DeleteMapping(path = "/records/batch-delete", consumes = MediaType.APPLICATION_JSON_VALUE)
    public JsonNode batchDelete(
            @RequestParam(required = false) String appKey,
            @RequestBody UserDeleteVo req
    ) throws Exception {
        return userService.batchDelete(appKey, req);
    }

    /**
     * 查询用户记录（低代码 App 数据表）。
     * @param req 请求体：filter/order_by/select/group_by/page_size/offset/page_token/need_total_count/query_deleted_record（按需）
     */
    @PostMapping(path = "/records/query", consumes = MediaType.APPLICATION_JSON_VALUE)
    public JsonNode query(
            @RequestParam(required = false) String appKey,
            @RequestBody UserQueryVo req
    ) throws Exception {
        return userService.query(appKey, req);
    }
}

