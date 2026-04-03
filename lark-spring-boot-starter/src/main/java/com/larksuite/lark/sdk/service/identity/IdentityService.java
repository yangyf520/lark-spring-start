package com.larksuite.lark.sdk.service.identity;

import com.lark.oapi.Client;
import com.lark.oapi.core.request.RequestOptions;
import com.lark.oapi.service.authen.v1.model.GetUserInfoResp;
import com.larksuite.lark.sdk.core.ClientRegistry;
import com.larksuite.lark.common.support.ApiExecutor;

/** 用户身份：需 user_access_token；返回完整 SDK Resp。 */
public class IdentityService {

    private final ClientRegistry registry;
    private final ApiExecutor executor;

    public IdentityService(ClientRegistry registry, ApiExecutor executor) {
        this.registry = registry;
        this.executor = executor;
    }

    public GetUserInfoResp getUserInfo(String appKey, String userAccessToken) throws Exception {
        Client client = resolveClient(appKey);
        RequestOptions options = RequestOptions.newBuilder()
                .userAccessToken(userAccessToken)
                .build();
        return executor.execute("authen.v1.userInfo.get", appKey, "userAccessToken=***",
                () -> client.authen().v1().userInfo().get(options));
    }

    private Client resolveClient(String appKey) {
        if (appKey == null || appKey.isBlank()) {
            return registry.primary();
        }
        return registry.get(appKey);
    }
}
