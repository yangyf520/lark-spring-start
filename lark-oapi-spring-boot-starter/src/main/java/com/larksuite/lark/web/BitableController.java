package com.larksuite.lark.web;

import com.lark.oapi.service.bitable.v1.model.GetAppResp;
import com.larksuite.lark.core.common.LarkApi;
import com.larksuite.lark.service.bitable.BitableService;
import com.larksuite.lark.starter.condition.ConditionalOnStarterRestApi;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 多维表格（智能表格）：读取应用元数据等。
 * <p>
 * 元数据接口与飞书文档
 * <a href="https://open.feishu.cn/document/server-docs/docs/bitable-v1/app/get">获取多维表格元数据</a> 一致；
 * 需在开放平台为应用开通 bitable 相关权限。
 */
@LarkApi
@RestController
@ConditionalOnStarterRestApi
@RequestMapping(path = "/api/lark/bitable", produces = MediaType.APPLICATION_JSON_VALUE)
public class BitableController {

    private final BitableService bitableService;

    public BitableController(BitableService bitableService) {
        this.bitableService = bitableService;
    }

    /**
     * 获取多维表格元数据：path 中 {@code appToken} 为多维表格 app_token（非应用 App ID）。
     */
    @GetMapping("/apps/{appToken}")
    public GetAppResp getApp(
            @PathVariable String appToken,
            @RequestParam(required = false) String appKey
    ) throws Exception {
        return bitableService.getApp(appKey, appToken);
    }
}
