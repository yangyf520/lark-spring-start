package com.larksuite.lark.web;

import com.larksuite.lark.api.dto.ApiResponse;
import com.larksuite.lark.service.LarkAuthService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** 飞书鉴权：租户 token、用户 OAuth code 换票与刷新。 */
@RestController
@ConditionalOnProperty(prefix = "lark.api", name = "enabled", havingValue = "true", matchIfMissing = true)
@RequestMapping(path = "/api/lark/auth", produces = MediaType.APPLICATION_JSON_VALUE)
public class AuthController {

    private final LarkAuthService authService;

    public AuthController(LarkAuthService authService) {
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

    /** 使用 app_id/app_secret 换取 tenant_access_token（服务端）。 */
    @PostMapping(path = "/tenant-access-token/internal", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse tenantAccessToken(@RequestBody(required = false) TenantTokenReq req) {
        try {
            String appKey = req == null ? null : req.appKey();
            var resp = authService.tenantAccessTokenInternal(appKey);
            if (!resp.success()) {
                return ApiResponse.failure(String.valueOf(resp.getCode()), resp.getMsg());
            }
            return ApiResponse.success(resp.getData());
        } catch (Exception e) {
            return ApiResponse.failure(e.getClass().getSimpleName(), e.getMessage());
        }
    }

    /** OAuth code 换取 user_access_token。 */
    @PostMapping(path = "/access-token", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse accessToken(@Valid @RequestBody AccessTokenReq req) {
        try {
            var resp = authService.exchangeUserAccessToken(req.appKey(), req.code(), req.grantType());
            if (!resp.success()) {
                return ApiResponse.failure(String.valueOf(resp.getCode()), resp.getMsg());
            }
            return ApiResponse.success(resp.getData());
        } catch (Exception e) {
            return ApiResponse.failure(e.getClass().getSimpleName(), e.getMessage());
        }
    }

    /** 使用 refresh_token 刷新用户 access_token。 */
    @PostMapping(path = "/refresh-access-token", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse refreshAccessToken(@Valid @RequestBody RefreshTokenReq req) {
        try {
            var resp = authService.refreshUserAccessToken(req.appKey(), req.refreshToken(), req.grantType());
            if (!resp.success()) {
                return ApiResponse.failure(String.valueOf(resp.getCode()), resp.getMsg());
            }
            return ApiResponse.success(resp.getData());
        } catch (Exception e) {
            return ApiResponse.failure(e.getClass().getSimpleName(), e.getMessage());
        }
    }
}
