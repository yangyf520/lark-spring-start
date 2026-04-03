package com.larksuite.lark.sdk.controller;

import com.larksuite.lark.common.annotation.LarkApi;
import com.larksuite.lark.sdk.core.ClientRegistry;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 多应用 OAPI Client 注册信息与 token 探测（不含密钥）。
 * <p>
 * 成功与异常由全局 Advice 统一处理。
 */
@LarkApi
@RestController
@RequestMapping(path = "/lark/oapi", produces = MediaType.APPLICATION_JSON_VALUE)
public class OapiController {

    private final ClientRegistry clientRegistry;

    /**
     * 构造注入。
     * <p>
     * @param clientRegistry 多应用 Client 注册表
     */
    public OapiController(ClientRegistry clientRegistry) {
        this.clientRegistry = clientRegistry;
    }

    /**
     * 列出主应用 key 与已注册 appKey。
     * <p>
     * @return {@code primaryKey}、{@code appKeys}
     */
    @GetMapping("/apps")
    public Map<String, Object> apps() {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("primaryKey", clientRegistry.primaryKey());
        m.put("appKeys", clientRegistry.clients().keySet());
        return m;
    }

    /**
     * 校验指定 appKey 是否可解析为 OAPI Client。
     * <p>
     * @param appKey 应用配置键，必填
     * @return {@code appKey} 确认信息
     */
    @GetMapping("/check-app")
    public Map<String, Object> checkApp(@RequestParam @NotBlank String appKey) {
        clientRegistry.get(appKey);
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("appKey", appKey);
        return m;
    }
}
