package com.larksuite.lark.web;

import com.larksuite.lark.api.dto.ApiResponse;
import com.larksuite.lark.core.token.TenantAccessTokenProvider;
import com.larksuite.lark.oapi.spring.OapiClientRegistry;
import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** 多应用 OAPI Client 注册信息与 token 探测（不含密钥）。 */
@RestController
@ConditionalOnProperty(prefix = "lark.api", name = "enabled", havingValue = "true", matchIfMissing = true)
@RequestMapping(path = "/api/lark/oapi", produces = MediaType.APPLICATION_JSON_VALUE)
public class OapiController {

    private final OapiClientRegistry clientRegistry;
    private final TenantAccessTokenProvider tokenProvider;

    public OapiController(OapiClientRegistry clientRegistry, TenantAccessTokenProvider tokenProvider) {
        this.clientRegistry = clientRegistry;
        this.tokenProvider = tokenProvider;
    }

    /** 列出 primaryKey 与全部 appKey。 */
    @GetMapping("/apps")
    public ApiResponse apps() {
        return ApiResponse.success(Map.of(
                "primaryKey", clientRegistry.primaryKey(),
                "appKeys", clientRegistry.clients().keySet()
        ));
    }

    /** 当前 primary 应用是否已缓存 tenant token（布尔，不含明文）。 */
    @GetMapping("/tenant-access-token")
    public ApiResponse tenantAccessToken() {
        try {
            String token = tokenProvider.getToken();
            return ApiResponse.success(Map.of("tokenPresent", token != null && !token.isBlank()));
        } catch (Exception e) {
            return ApiResponse.failure(e.getClass().getSimpleName(), e.getMessage());
        }
    }

    /** 校验指定 appKey 能否解析为 Client。 */
    @GetMapping("/check-app")
    public ApiResponse checkApp(@RequestParam @NotBlank String appKey) {
        try {
            clientRegistry.get(appKey);
            return ApiResponse.success(Map.of("appKey", appKey));
        } catch (Exception e) {
            return ApiResponse.failure("CHECK_APP_FAILED", e.getMessage());
        }
    }
}

