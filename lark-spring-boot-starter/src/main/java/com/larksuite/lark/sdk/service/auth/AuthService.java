package com.larksuite.lark.sdk.service.auth;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import com.larksuite.lark.common.support.ApiExecutor;
import com.larksuite.lark.sdk.core.ClientRegistry;
import com.larksuite.lark.sdk.core.SdkProperties;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/** auth / authen：租户 token、用户 OAuth 换票；返回完整 SDK Resp（HTTP 由 Advice 解包）。 */
public class AuthService {

    private final ClientRegistry clientRegistry;
    private final SdkProperties sdkProperties;
    private final ObjectMapper objectMapper;
    private final ApiExecutor executor;

    public AuthService(ClientRegistry clientRegistry, SdkProperties sdkProperties, ObjectMapper objectMapper, ApiExecutor executor) {
        this.clientRegistry = clientRegistry;
        this.sdkProperties = sdkProperties;
        this.objectMapper = objectMapper;
        this.executor = executor;
    }

    public InternalTenantAccessTokenResp tenantAccessTokenInternal(String appKey) throws Exception {
        SdkProperties.App app = resolveAppConfig(appKey);
        Client client = resolveClient(appKey);
        InternalTenantAccessTokenReq req = InternalTenantAccessTokenReq.newBuilder()
                .internalTenantAccessTokenReqBody(InternalTenantAccessTokenReqBody.newBuilder()
                        .appId(app.getAppId())
                        .appSecret(app.getAppSecret())
                        .build())
                .build();
        return executor.execute("auth.v3.tenantAccessToken.internal", appKey, "",
                () -> client.auth().v3().tenantAccessToken().internal(req));
    }

    /**
     * 获取租户访问令牌：返回飞书 HTTP body 的 JSON（code/msg/tenant_access_token/expire），
     * SDK rawResponse.body 若为 base64，则在此处解码。
     */
    public JsonNode tenantAccessTokenInternalBodyJson(String appKey) throws Exception {
        InternalTenantAccessTokenResp resp = tenantAccessTokenInternal(appKey);
        if (!resp.success()) {
            throw new IllegalStateException("feishu tenant_access_token internal failed: " + resp.getCode() + " " + resp.getMsg());
        }
        String body = extractRawBody(resp);
        if (body == null || body.isBlank()) {
            throw new IllegalStateException("feishu tenant_access_token internal returned empty body");
        }
        return objectMapper.readTree(decodeIfBase64(body.trim()));
    }

    public CreateAccessTokenResp exchangeUserAccessToken(String appKey, String code, String grantType) throws Exception {
        Client client = resolveClient(appKey);
        CreateAccessTokenReq req = CreateAccessTokenReq.newBuilder()
                .createAccessTokenReqBody(CreateAccessTokenReqBody.newBuilder()
                        .grantType(grantType == null || grantType.isBlank() ? "authorization_code" : grantType)
                        .code(code)
                        .build())
                .build();
        return executor.execute("authen.v1.accessToken.create", appKey,
                "grantType=" + (grantType == null || grantType.isBlank() ? "authorization_code" : grantType),
                () -> client.authen().v1().accessToken().create(req));
    }

    public CreateRefreshAccessTokenResp refreshUserAccessToken(String appKey, String refreshToken, String grantType) throws Exception {
        Client client = resolveClient(appKey);
        CreateRefreshAccessTokenReq req = CreateRefreshAccessTokenReq.newBuilder()
                .createRefreshAccessTokenReqBody(CreateRefreshAccessTokenReqBody.newBuilder()
                        .grantType(grantType == null || grantType.isBlank() ? "refresh_token" : grantType)
                        .refreshToken(refreshToken)
                        .build())
                .build();
        return executor.execute("authen.v1.refreshAccessToken.create", appKey,
                "grantType=" + (grantType == null || grantType.isBlank() ? "refresh_token" : grantType),
                () -> client.authen().v1().refreshAccessToken().create(req));
    }

    private static String extractRawBody(InternalTenantAccessTokenResp resp) {
        if (resp == null || resp.getRawResponse() == null) {
            return null;
        }
        Object b = resp.getRawResponse().getBody();
        if (b instanceof byte[] bytes) {
            return new String(bytes, StandardCharsets.UTF_8);
        }
        if (b instanceof String s) {
            return s;
        }
        return b == null ? null : String.valueOf(b);
    }

    private static String decodeIfBase64(String s) {
        try {
            byte[] bytes = Base64.getDecoder().decode(s);
            String decoded = new String(bytes, StandardCharsets.UTF_8).trim();
            if (!decoded.isEmpty() && (decoded.startsWith("{") || decoded.startsWith("["))) {
                return decoded;
            }
        } catch (Exception ignored) {
            // not base64
        }
        return s;
    }

    private Client resolveClient(String appKey) {
        if (appKey == null || appKey.isBlank()) {
            return clientRegistry.primary();
        }
        return clientRegistry.get(appKey);
    }

    private SdkProperties.App resolveAppConfig(String appKey) {
        if (appKey == null || appKey.isBlank()) {
            String primary = clientRegistry.primaryKey();
            if (primary == null || primary.isBlank()) {
                throw new IllegalStateException("No primary app: configure a single lark.oapi.apps entry or pass appKey");
            }
            SdkProperties.App app = sdkProperties.getApps().get(primary);
            if (app == null) {
                throw new IllegalStateException("Primary app config not found: " + primary);
            }
            return app;
        }
        SdkProperties.App app = sdkProperties.getApps().get(appKey);
        if (app == null) {
            throw new IllegalArgumentException("Unknown lark.oapi app key: " + appKey);
        }
        return app;
    }
}
