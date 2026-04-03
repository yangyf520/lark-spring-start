package com.larksuite.lark.sdk.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.lark.oapi.service.authen.v1.model.CreateAccessTokenResp;
import com.lark.oapi.service.authen.v1.model.CreateAccessTokenRespBody;
import com.lark.oapi.service.authen.v1.model.CreateRefreshAccessTokenResp;
import com.larksuite.lark.common.annotation.LarkApi;
import com.larksuite.lark.sdk.core.ClientRegistry;
import com.larksuite.lark.sdk.service.auth.AuthService;
import com.larksuite.lark.sdk.service.auth.LarkOAuthService;
import com.larksuite.lark.sdk.service.auth.LarkOAuthUserProfile;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 飞书鉴权：租户 token、用户 OAuth 换票与刷新。
 * <p>
 * 成功与异常由全局 Advice 与 Service 统一处理。
 */
@LarkApi
@RestController
@RequestMapping(path = "/lark/auth", produces = MediaType.APPLICATION_JSON_VALUE)
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final AuthService authService;
    private final ClientRegistry clientRegistry;
    private final LarkOAuthService larkOAuthService;

    /**
     * 构造注入。
     * <p>
     * @param authService       鉴权服务
     * @param clientRegistry    多应用 Client 注册表
     * @param larkOAuthService  浏览器 OAuth 成功回调（starter 提供占位 Bean，宿主可覆盖）
     */
    public AuthController(AuthService authService, ClientRegistry clientRegistry, LarkOAuthService larkOAuthService) {
        this.authService = authService;
        this.clientRegistry = clientRegistry;
        this.larkOAuthService = larkOAuthService;
    }

    /**
     * 租户 token 请求体。
     * <p>
     * @param appKey 应用配置键，可空（使用 primary）
     */
    public record TenantTokenReq(String appKey) {}

    /**
     * 用户 access token 换票请求体。
     * <p>
     * @param appKey    应用配置键，可空（使用 primary）
     * @param code      授权码，必填
     * @param grantType 授权类型，可空（默认 {@code authorization_code}）
     */
    public record AccessTokenReq(
            String appKey,
            @NotBlank String code,
            String grantType
    ) {}

    /**
     * 刷新用户 access token 请求体。
     * <p>
     * @param appKey       应用配置键，可空（使用 primary）
     * @param refreshToken 刷新令牌，必填
     * @param grantType    授权类型，可空（默认 {@code refresh_token}）
     */
    public record RefreshTokenReq(
            String appKey,
            @NotBlank String refreshToken,
            String grantType
    ) {}

    /**
     * 浏览器 OAuth 回调：用 query 中 {@code code} 换 user_access_token，再交给 {@link LarkOAuthService}。
     * 飞书应用「重定向 URL」须配置为本地址（含 context-path）。
     * <p>
     * @param code    授权码，必填
     * @param state   OAuth {@code state}，可选
     * @param appKey  应用配置键（{@code lark.oapi.apps}）；多应用且未配置 primary 时必填
     * @param request 当前 HTTP 请求
     * @return {@link LarkOAuthService#onAuthorized} 的返回值（宿主未覆盖时默认实现会抛错，见接口说明）
     */
    @GetMapping("/authorize")
    public Object authorize(@RequestParam(name = "code", required = false) String code,
                            @RequestParam(name = "state", required = false) String state,
                            @RequestParam(name = "appKey", required = false) String appKey,
                            HttpServletRequest request) throws Exception {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("missing lark oauth code");
        }

        String effectiveAppKey = resolveAppKeyOrThrow(appKey);

        CreateAccessTokenResp resp = authService.exchangeUserAccessToken(effectiveAppKey, code, "authorization_code");
        log.info("lark oauth authorize, appKey={}, success={}, code={}, msg={}",
                effectiveAppKey, resp != null && resp.success(),
                resp == null ? null : resp.getCode(), resp == null ? null : resp.getMsg());

        if (resp == null) {
            throw new IllegalStateException("飞书返回为空");
        }
        if (!resp.success()) {
            throw new IllegalStateException(
                    "飞书 code 换 token 失败：code=" + resp.getCode() + ", msg=" + resp.getMsg());
        }
        CreateAccessTokenRespBody data = resp.getData();
        if (data == null) {
            throw new IllegalStateException("飞书返回 data 为空");
        }

        return larkOAuthService.onAuthorized(effectiveAppKey, request, state, new LarkOAuthUserProfile(data), resp);
    }

    /**
     * 应用凭证换取 {@code tenant_access_token}（服务端调用）。
     * <p>
     * @param req 请求体，可空；{@code appKey} 指定应用，空则使用 primary
     * @return 飞书 HTTP body 解析后的 JSON（含 token、过期时间等）
     */
    @PostMapping(path = "/tenant-access-token/internal", consumes = MediaType.APPLICATION_JSON_VALUE)
    public JsonNode tenantAccessToken(@RequestBody(required = false) TenantTokenReq req) throws Exception {
        String key = req == null ? null : req.appKey();
        return authService.tenantAccessTokenInternalBodyJson(key);
    }

    /**
     * 授权码换取 {@code user_access_token}。
     * <p>
     * @param req 请求体，含 {@code appKey}、{@code code}、可选 {@code grantType}
     * @return 飞书 SDK {@link CreateAccessTokenResp}
     */
    @PostMapping(path = "/access-token", consumes = MediaType.APPLICATION_JSON_VALUE)
    public CreateAccessTokenResp accessToken(@Valid @RequestBody AccessTokenReq req) throws Exception {
        return authService.exchangeUserAccessToken(req.appKey(), req.code(), req.grantType());
    }

    /**
     * 使用 {@code refresh_token} 刷新用户 {@code access_token}。
     * <p>
     * @param req 请求体，含 {@code appKey}、{@code refreshToken}、可选 {@code grantType}
     * @return 飞书 SDK {@link CreateRefreshAccessTokenResp}
     */
    @PostMapping(path = "/refresh-access-token", consumes = MediaType.APPLICATION_JSON_VALUE)
    public CreateRefreshAccessTokenResp refreshAccessToken(@Valid @RequestBody RefreshTokenReq req) throws Exception {
        return authService.refreshUserAccessToken(req.appKey(), req.refreshToken(), req.grantType());
    }

    /**
     * 解析 query 中的 {@code appKey}；未传则使用 primary（仅单应用配置时可用）。
     * <p>
     * @param appKey 调用方传入的 appKey，可空
     * @return 非空的生效 appKey
     */
    private String resolveAppKeyOrThrow(String appKey) {
        if (appKey != null && !appKey.isBlank()) {
            return appKey.trim();
        }
        String primary = clientRegistry.primaryKey();
        if (primary == null || primary.isBlank()) {
            throw new IllegalArgumentException(
                    "appKey is required when lark.oapi.apps has multiple entries (or pass appKey query param)");
        }
        return primary;
    }
}
