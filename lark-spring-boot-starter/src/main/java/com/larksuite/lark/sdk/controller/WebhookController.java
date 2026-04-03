package com.larksuite.lark.sdk.controller;

import com.lark.oapi.core.request.EventReq;
import com.lark.oapi.core.response.EventResp;
import com.larksuite.lark.sdk.core.ClientRegistry;
import com.larksuite.lark.sdk.core.EventDispatcherRegistry;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 飞书事件 HTTP 回调：验签解密后交给 SDK {@link com.lark.oapi.event.EventDispatcher}。
 * <p>
 * 响应为飞书/SDK 原生格式，不经 {@link com.larksuite.lark.common.annotation.LarkApi} 统一包装。
 */
@RestController
@RequestMapping(path = "/lark", produces = MediaType.APPLICATION_JSON_VALUE)
public class WebhookController {

    private final EventDispatcherRegistry dispatcherRegistry;
    private final ClientRegistry clientRegistry;

    /**
     * 构造注入。
     * <p>
     * @param dispatcherRegistry 按 appKey 的事件分发器
     * @param clientRegistry     多应用 Client 注册表
     */
    public WebhookController(EventDispatcherRegistry dispatcherRegistry, ClientRegistry clientRegistry) {
        this.dispatcherRegistry = dispatcherRegistry;
        this.clientRegistry = clientRegistry;
    }

    /**
     * 接收飞书事件订阅回调并交给 SDK 处理。
     * <p>
     * @param appKey   路径中的 appKey，可空（使用 primary 或 {@code default}）
     * @param request  原始 HTTP 请求
     * @param response 原始 HTTP 响应
     */
    @PostMapping(path = {"/webhook", "/webhook/{appKey}"})
    public void webhook(
            @PathVariable(required = false) String appKey,
            HttpServletRequest request,
            HttpServletResponse response
    ) throws Throwable {
        EventReq eventReq = new EventReq();
        eventReq.setHttpPath(request.getRequestURI());
        eventReq.setHeaders(headerMap(request));
        eventReq.setBody(readBodyBytes(request));

        EventResp eventResp = dispatcherRegistry.get(appKeyOrDefault(appKey)).handle(eventReq);
        writeResponse(response, eventResp);
    }

    /**
     * 解析路径 appKey；未传时使用 primary，否则 {@code default}。
     * <p>
     * @param appKey 路径变量，可空
     * @return 用于选取 {@link com.larksuite.lark.sdk.core.EventDispatcherRegistry} 的 key
     */
    private String appKeyOrDefault(String appKey) {
        if (appKey != null && !appKey.isBlank()) {
            return appKey;
        }
        String pk = clientRegistry.primaryKey();
        if (pk != null && !pk.isBlank()) {
            return pk;
        }
        return "default";
    }

    /**
     * 将 Servlet 请求头转为多值 Map。
     * <p>
     * @param request 当前请求
     * @return 头名 → 值列表
     */
    private static Map<String, List<String>> headerMap(HttpServletRequest request) {
        Enumeration<String> names = request.getHeaderNames();
        if (names == null) {
            return Collections.emptyMap();
        }
        Map<String, List<String>> headers = new LinkedHashMap<>();
        while (names.hasMoreElements()) {
            String name = names.nextElement();
            headers.put(name, Collections.list(request.getHeaders(name)));
        }
        return headers;
    }

    /**
     * 读取请求体字节。
     * <p>
     * @param request 当前请求
     * @return UTF-8 字节
     */
    private static byte[] readBodyBytes(HttpServletRequest request) throws IOException {
        String body = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        return body.getBytes(StandardCharsets.UTF_8);
    }

    /**
     * 将 SDK {@link EventResp} 写回 Servlet 响应。
     * <p>
     * @param response Servlet 响应
     * @param eventResp SDK 事件响应
     */
    private static void writeResponse(HttpServletResponse response, EventResp eventResp) throws IOException {
        response.setStatus(eventResp.getStatusCode());
        if (eventResp.getHeaders() != null) {
            for (Map.Entry<String, List<String>> e : eventResp.getHeaders().entrySet()) {
                if (e.getValue() == null) continue;
                for (String v : e.getValue()) {
                    response.addHeader(e.getKey(), v);
                }
            }
        }
        byte[] body = eventResp.getBody();
        if (body != null && body.length > 0) {
            response.getOutputStream().write(body);
        }
    }
}
