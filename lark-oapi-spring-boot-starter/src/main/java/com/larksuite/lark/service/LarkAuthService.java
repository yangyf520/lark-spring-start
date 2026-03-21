package com.larksuite.lark.service;

import com.lark.oapi.Client;
import com.lark.oapi.service.auth.v3.model.InternalTenantAccessTokenReq;
import com.lark.oapi.service.auth.v3.model.InternalTenantAccessTokenReqBody;
import com.lark.oapi.service.auth.v3.model.InternalTenantAccessTokenResp;
import com.lark.oapi.service.authen.v1.model.CreateAccessTokenReq;
import com.lark.oapi.service.authen.v1.model.CreateAccessTokenReqBody;
import com.lark.oapi.service.authen.v1.model.CreateAccessTokenResp;
import com.lark.oapi.service.authen.v1.model.CreateRefreshAccessTokenReq;
import com.lark.oapi.service.authen.v1.model.CreateRefreshAccessTokenReqBody;
import com.lark.oapi.service.authen.v1.model.CreateRefreshAccessTokenResp;
import com.larksuite.lark.oapi.spring.OapiClientRegistry;
import com.larksuite.lark.oapi.spring.OapiProperties;

/** auth / authen：租户 token、用户 OAuth 换票。 */
public class LarkAuthService {

    private final OapiClientRegistry clientRegistry;
    private final OapiProperties oapiProperties;

    public LarkAuthService(OapiClientRegistry clientRegistry, OapiProperties oapiProperties) {
        this.clientRegistry = clientRegistry;
        this.oapiProperties = oapiProperties;
    }

    /** 服务端 internal 换取 tenant_access_token。 */
    public InternalTenantAccessTokenResp tenantAccessTokenInternal(String appKey) throws Exception {
        OapiProperties.App app = resolveAppConfig(appKey);
        Client client = resolveClient(appKey);
        InternalTenantAccessTokenReq req = InternalTenantAccessTokenReq.newBuilder()
                .internalTenantAccessTokenReqBody(InternalTenantAccessTokenReqBody.newBuilder()
                        .appId(app.getAppId())
                        .appSecret(app.getAppSecret())
                        .build())
                .build();
        return client.auth().v3().tenantAccessToken().internal(req);
    }

    /** authorization_code 换 user_access_token。 */
    public CreateAccessTokenResp exchangeUserAccessToken(String appKey, String code, String grantType) throws Exception {
        Client client = resolveClient(appKey);
        CreateAccessTokenReq req = CreateAccessTokenReq.newBuilder()
                .createAccessTokenReqBody(CreateAccessTokenReqBody.newBuilder()
                        .grantType(grantType == null || grantType.isBlank() ? "authorization_code" : grantType)
                        .code(code)
                        .build())
                .build();
        return client.authen().v1().accessToken().create(req);
    }

    /** refresh_token 刷新用户 access_token。 */
    public CreateRefreshAccessTokenResp refreshUserAccessToken(String appKey, String refreshToken, String grantType) throws Exception {
        Client client = resolveClient(appKey);
        CreateRefreshAccessTokenReq req = CreateRefreshAccessTokenReq.newBuilder()
                .createRefreshAccessTokenReqBody(CreateRefreshAccessTokenReqBody.newBuilder()
                        .grantType(grantType == null || grantType.isBlank() ? "refresh_token" : grantType)
                        .refreshToken(refreshToken)
                        .build())
                .build();
        return client.authen().v1().refreshAccessToken().create(req);
    }

    private Client resolveClient(String appKey) {
        if (appKey == null || appKey.isBlank()) {
            return clientRegistry.primary();
        }
        return clientRegistry.get(appKey);
    }

    private OapiProperties.App resolveAppConfig(String appKey) {
        if (appKey == null || appKey.isBlank()) {
            String primary = clientRegistry.primaryKey();
            if (primary == null || primary.isBlank()) {
                throw new IllegalStateException("No primary app configured (set lark.oapi.primary)");
            }
            OapiProperties.App app = oapiProperties.getApps().get(primary);
            if (app == null) {
                throw new IllegalStateException("Primary app config not found: " + primary);
            }
            return app;
        }
        OapiProperties.App app = oapiProperties.getApps().get(appKey);
        if (app == null) {
            throw new IllegalArgumentException("Unknown lark.oapi app key: " + appKey);
        }
        return app;
    }
}
