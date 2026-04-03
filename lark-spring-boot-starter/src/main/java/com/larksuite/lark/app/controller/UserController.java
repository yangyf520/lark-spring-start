package com.larksuite.lark.app.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.larksuite.lark.app.service.UserService;
import com.larksuite.lark.app.vo.data.user.UserCreateVo;
import com.larksuite.lark.app.vo.data.user.UserDeleteVo;
import com.larksuite.lark.app.vo.data.user.UserQueryVo;
import com.larksuite.lark.app.vo.data.user.UserRecordCreateVo;
import com.larksuite.lark.app.vo.data.user.UserUpdateVo;
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
 * AE 内置用户表（{@code _user}）数据接口。
 * <p>
 * 成功与异常由全局 Advice 与 Service 统一处理。
 */
@LarkApi
@RestController
@RequestMapping(path = "/lark/app/data/objects/_user", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserController {

    private final UserService userService;

    /**
     * 构造注入。
     * <p>
     * @param userService 用户数据服务
     */
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 批量新增用户记录。
     * <p>
     * @param appKey AE 应用键，可空（使用 primary）
     * @param req    请求体：{@code records} 等
     * @return AE API 响应 JSON
     */
    @PostMapping(path = "/records/batch-create", consumes = MediaType.APPLICATION_JSON_VALUE)
    public JsonNode batchCreate(
            @RequestParam(required = false) String appKey,
            @RequestBody UserCreateVo req
    ) throws Exception {
        return userService.batchCreate(appKey, req);
    }

    /**
     * 单条新增用户记录。
     * <p>
     * @param appKey AE 应用键，可空（使用 primary）
     * @param req    请求体：{@code record}
     * @return AE API 响应 JSON
     */
    @PostMapping(path = "/records/create", consumes = MediaType.APPLICATION_JSON_VALUE)
    public JsonNode createRecord(
            @RequestParam(required = false) String appKey,
            @RequestBody UserRecordCreateVo req
    ) throws Exception {
        return userService.createRecord(appKey, req);
    }

    /**
     * 批量更新用户记录。
     * <p>
     * @param appKey AE 应用键，可空（使用 primary）
     * @param req    请求体：{@code records}（含 id + fields）
     * @return AE API 响应 JSON
     */
    @PatchMapping(path = "/records/batch-update", consumes = MediaType.APPLICATION_JSON_VALUE)
    public JsonNode batchUpdate(
            @RequestParam(required = false) String appKey,
            @RequestBody UserUpdateVo req
    ) throws Exception {
        return userService.batchUpdate(appKey, req);
    }

    /**
     * 批量删除用户记录。
     * <p>
     * @param appKey AE 应用键，可空（使用 primary）
     * @param req    请求体：{@code ids}
     * @return AE API 响应 JSON
     */
    @DeleteMapping(path = "/records/batch-delete", consumes = MediaType.APPLICATION_JSON_VALUE)
    public JsonNode batchDelete(
            @RequestParam(required = false) String appKey,
            @RequestBody UserDeleteVo req
    ) throws Exception {
        return userService.batchDelete(appKey, req);
    }

    /**
     * 条件查询用户记录。
     * <p>
     * @param appKey AE 应用键，可空（使用 primary）
     * @param req    请求体：filter / order_by / select / 分页等
     * @return AE API 响应 JSON
     */
    @PostMapping(path = "/records/query", consumes = MediaType.APPLICATION_JSON_VALUE)
    public JsonNode query(
            @RequestParam(required = false) String appKey,
            @RequestBody UserQueryVo req
    ) throws Exception {
        return userService.query(appKey, req);
    }
}
