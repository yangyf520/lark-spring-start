package com.larksuite.lark.web;

import com.larksuite.lark.core.common.LarkApi;
import com.larksuite.lark.core.token.TenantAccessTokenProvider;
import com.larksuite.lark.oapi.spring.OapiClientRegistry;
import jakarta.validation.constraints.NotBlank;
import com.larksuite.lark.starter.condition.ConditionalOnStarterRestApi;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

/** 多应用 OAPI Client 注册信息与 token 探测（不含密钥）。 */
@LarkApi
@RestController
@ConditionalOnStarterRestApi
@RequestMapping(path = "/api/lark/oapi", produces = MediaType.APPLICATION_JSON_VALUE)
public class OapiController {

    private final OapiClientRegistry clientRegistry;
    private final TenantAccessTokenProvider tokenProvider;

    public OapiController(OapiClientRegistry clientRegistry, TenantAccessTokenProvider tokenProvider) {
        this.clientRegistry = clientRegistry;
        this.tokenProvider = tokenProvider;
    }

    /** 列出应用配置：返回主应用 key 与已注册 appKey 列表。 */
    @GetMapping("/apps")
    public Map<String, Object> apps() {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("primaryKey", clientRegistry.primaryKey());
        m.put("appKeys", clientRegistry.clients().keySet());
        return m;
    }

    /** 检测租户令牌缓存：检测默认应用是否已缓存 tenant_access_token，不返回令牌明文。 */
    @GetMapping("/tenant-access-token")
    public Map<String, Object> tenantAccessToken() throws Exception {
        String token = tokenProvider.getToken();
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("tokenPresent", token != null && !token.isBlank());
        return m;
    }

    /** 校验应用键：校验指定 appKey 是否可解析为 OAPI Client。 */
    @GetMapping("/check-app")
    public Map<String, Object> checkApp(@RequestParam @NotBlank String appKey) {
        clientRegistry.get(appKey);
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("appKey", appKey);
        return m;
    }
}
