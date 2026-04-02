package com.larksuite.lark.sdk.controller;

import com.larksuite.lark.common.annotation.LarkApi;
import com.larksuite.lark.sdk.core.OapiClientRegistry;
import jakarta.validation.constraints.NotBlank;
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
@RequestMapping(path = "/lark/oapi", produces = MediaType.APPLICATION_JSON_VALUE)
public class OapiController {

    private final OapiClientRegistry clientRegistry;

    public OapiController(OapiClientRegistry clientRegistry) {
        this.clientRegistry = clientRegistry;
    }

    /** 列出应用配置：返回主应用 key 与已注册 appKey 列表。 */
    @GetMapping("/apps")
    public Map<String, Object> apps() {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("primaryKey", clientRegistry.primaryKey());
        m.put("appKeys", clientRegistry.clients().keySet());
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
