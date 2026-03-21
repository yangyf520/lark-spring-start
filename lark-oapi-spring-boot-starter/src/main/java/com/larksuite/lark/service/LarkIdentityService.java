package com.larksuite.lark.service;

import com.lark.oapi.Client;
import com.lark.oapi.core.request.RequestOptions;
import com.lark.oapi.service.authen.v1.model.GetUserInfoResp;
import com.larksuite.lark.oapi.spring.OapiClientRegistry;
import com.larksuite.lark.support.LarkApiExecutor;

/** 用户身份：需 user_access_token；返回完整 SDK Resp。 */
public class LarkIdentityService {

    private final OapiClientRegistry registry;
    private final LarkApiExecutor executor;

    public LarkIdentityService(OapiClientRegistry registry, LarkApiExecutor executor) {
        this.registry = registry;
        this.executor = executor;
    }

    public GetUserInfoResp getUserInfo(String appKey, String userAccessToken) throws Exception {
        Client client = resolveClient(appKey);
        RequestOptions options = RequestOptions.newBuilder()
                .userAccessToken(userAccessToken)
                .build();
        return executor.execute(() -> client.authen().v1().userInfo().get(options));
    }

    private Client resolveClient(String appKey) {
        if (appKey == null || appKey.isBlank()) {
            return registry.primary();
        }
        return registry.get(appKey);
    }
}
