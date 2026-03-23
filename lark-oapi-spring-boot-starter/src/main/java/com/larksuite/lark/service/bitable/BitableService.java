package com.larksuite.lark.service.bitable;

import com.lark.oapi.Client;
import com.lark.oapi.service.bitable.v1.model.GetAppReq;
import com.lark.oapi.service.bitable.v1.model.GetAppResp;
import com.larksuite.lark.oapi.spring.OapiClientRegistry;
import com.larksuite.lark.support.ApiExecutor;

/**
 * 多维表格（智能表格）Bitable v1：应用元数据等；返回完整 SDK Resp。
 * <p>
 * 与飞书「<a href="https://open.feishu.cn/document/server-docs/docs/bitable-v1/app/get">获取多维表格元数据</a>」对应。
 */
public class BitableService {

    private final OapiClientRegistry registry;
    private final ApiExecutor executor;

    public BitableService(OapiClientRegistry registry, ApiExecutor executor) {
        this.registry = registry;
        this.executor = executor;
    }

    /** GET /open-apis/bitable/v1/apps/{app_token} */
    public GetAppResp getApp(String appKey, String appToken) throws Exception {
        Client client = resolveClient(appKey);
        GetAppReq req = GetAppReq.newBuilder()
                .appToken(appToken)
                .build();
        return executor.execute(() -> client.bitable().v1().app().get(req));
    }

    private Client resolveClient(String appKey) {
        if (appKey == null || appKey.isBlank()) {
            return registry.primary();
        }
        return registry.get(appKey);
    }
}
