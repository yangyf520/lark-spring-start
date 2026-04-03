package com.larksuite.lark.sdk.controller;

import com.lark.oapi.service.bitable.v1.model.GetAppResp;
import com.lark.oapi.service.bitable.v1.model.SearchAppTableRecordReqBody;
import com.lark.oapi.service.bitable.v1.model.SearchAppTableRecordResp;
import com.larksuite.lark.common.annotation.LarkApi;
import com.larksuite.lark.sdk.service.bitable.BitableService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 多维表格（智能表格）：读取应用元数据、查询记录。
 * <p>
 * 元数据与
 * <a href="https://open.feishu.cn/document/server-docs/docs/bitable-v1/app/get">获取多维表格元数据</a>
 * 一致；需在开放平台开通 bitable 相关权限。
 */
@LarkApi
@RestController
@RequestMapping(path = "/lark/bitable", produces = MediaType.APPLICATION_JSON_VALUE)
public class BitableController {

    private final BitableService bitableService;

    /**
     * 构造注入。
     * <p>
     * @param bitableService 多维表格服务
     */
    public BitableController(BitableService bitableService) {
        this.bitableService = bitableService;
    }

    /**
     * 获取多维表格元数据。
     * <p>
     * @param appToken path 中多维表格 app_token（非应用 App ID）
     * @param appKey   应用配置键，可空（使用 primary）
     * @return 飞书 SDK {@link GetAppResp}
     */
    @GetMapping("/apps/{appToken}")
    public GetAppResp getApp(
            @PathVariable String appToken,
            @RequestParam(required = false) String appKey
    ) throws Exception {
        return bitableService.getApp(appKey, appToken);
    }

    /**
     * 按视图、筛选、排序与分页查询数据表记录。
     * <p>
     * @param appToken  多维表格 app_token
     * @param tableId   数据表 ID
     * @param appKey    应用配置键，可空（使用 primary）
     * @param userIdType 用户 ID 类型，可空
     * @param pageToken 分页标记，可空
     * @param pageSize  分页大小，可空
     * @param body      查询体，可空
     * @return 飞书 SDK {@link SearchAppTableRecordResp}
     */
    @PostMapping(path = "/apps/{appToken}/tables/{tableId}/records/search", consumes = MediaType.APPLICATION_JSON_VALUE)
    public SearchAppTableRecordResp searchRecords(
            @PathVariable String appToken,
            @PathVariable String tableId,
            @RequestParam(required = false) String appKey,
            @RequestParam(required = false) String userIdType,
            @RequestParam(required = false) String pageToken,
            @RequestParam(required = false) Integer pageSize,
            @RequestBody(required = false) SearchAppTableRecordReqBody body
    ) throws Exception {
        return bitableService.searchRecords(appKey, appToken, tableId, userIdType, pageToken, pageSize, body);
    }
}
