package com.larksuite.lark.sdk.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.lark.oapi.service.authen.v1.model.CreateAccessTokenResp;
import com.lark.oapi.service.authen.v1.model.CreateRefreshAccessTokenResp;
import com.larksuite.lark.common.annotation.LarkApi;
import com.larksuite.lark.sdk.service.auth.AuthService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** 飞书鉴权：租户 token、用户 OAuth code 换票与刷新（成功/失败由全局 Advice 与 Service 统一处理）。 */
@LarkApi
@RestController
@RequestMapping(path = "/lark/auth", produces = MediaType.APPLICATION_JSON_VALUE)
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    public record TenantTokenReq(String appKey) {}

    public record AccessTokenReq(
            String appKey,
            @NotBlank String code,
            String grantType
    ) {}

    public record RefreshTokenReq(
            String appKey,
            @NotBlank String refreshToken,
            String grantType
    ) {}

    /** 获取租户访问令牌：使用应用凭证换取 tenant_access_token（服务端调用）。 */
    @PostMapping(path = "/tenant-access-token/internal", consumes = MediaType.APPLICATION_JSON_VALUE)
    public JsonNode tenantAccessToken(@RequestBody(required = false) TenantTokenReq req) throws Exception {
        String appKey = req == null ? null : req.appKey();
        return authService.tenantAccessTokenInternalBodyJson(appKey);
    }

    /** OAuth code 换取用户令牌：使用授权码 code 换取 user_access_token。 */
    @PostMapping(path = "/access-token", consumes = MediaType.APPLICATION_JSON_VALUE)
    public CreateAccessTokenResp accessToken(@Valid @RequestBody AccessTokenReq req) throws Exception {
        return authService.exchangeUserAccessToken(req.appKey(), req.code(), req.grantType());
    }

    /** 刷新用户访问令牌：使用 refresh_token 刷新用户 access_token。 */
    @PostMapping(path = "/refresh-access-token", consumes = MediaType.APPLICATION_JSON_VALUE)
    public CreateRefreshAccessTokenResp refreshAccessToken(@Valid @RequestBody RefreshTokenReq req) throws Exception {
        return authService.refreshUserAccessToken(req.appKey(), req.refreshToken(), req.grantType());
    }
}
