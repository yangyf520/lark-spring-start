package com.larksuite.lark.backend.web;

import com.larksuite.lark.core.token.TenantAccessTokenProvider;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;

/** 本机运维：健康与 tenant token 是否拉取成功。 */
@RestController
@RequestMapping(path = "/admin", produces = MediaType.APPLICATION_JSON_VALUE)
public class AdminController {

    private final TenantAccessTokenProvider tokenProvider;

    public AdminController(TenantAccessTokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    /** 健康检查：返回服务可用性与 tenant access token 缓存状态（不含明文）。 */
    @GetMapping("/health")
    public Map<String, Object> health() {
        try {
            String token = tokenProvider.getToken();
            return Map.of(
                    "ok", true,
                    "time", Instant.now().toString(),
                    "tatPresent", token != null && !token.isBlank()
            );
        } catch (Exception e) {
            return Map.of(
                    "ok", false,
                    "time", Instant.now().toString(),
                    "tatPresent", false,
                    "error", e.getClass().getSimpleName(),
                    "message", e.getMessage() == null ? "" : e.getMessage()
            );
        }
    }
}
