package com.larksuite.lark.bot.webhook;

import com.lark.oapi.core.request.EventReq;
import com.lark.oapi.core.response.EventResp;
import com.larksuite.lark.oapi.spring.OapiEventDispatcherRegistry;
import com.larksuite.lark.oapi.spring.OapiProperties;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.larksuite.lark.starter.condition.ConditionalOnStarterWebhook;
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

/** 飞书事件 HTTP 回调：验签解密后交给 SDK {@link com.lark.oapi.event.EventDispatcher}。 */
@RestController
@ConditionalOnStarterWebhook
@RequestMapping(path = "/lark", produces = MediaType.APPLICATION_JSON_VALUE)
public class WebhookController {

    private final OapiEventDispatcherRegistry dispatcherRegistry;
    private final OapiProperties oapiProperties;

    public WebhookController(OapiEventDispatcherRegistry dispatcherRegistry, OapiProperties oapiProperties) {
        this.dispatcherRegistry = dispatcherRegistry;
        this.oapiProperties = oapiProperties;
    }

    /** 飞书事件回调入口：接收飞书事件订阅回调并交给 SDK 处理。 */
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

    private String appKeyOrDefault(String appKey) {
        if (appKey != null && !appKey.isBlank()) {
            return appKey;
        }
        String primary = oapiProperties.getPrimary();
        if (primary != null && !primary.isBlank()) {
            return primary;
        }
        if (oapiProperties.getApps() != null && oapiProperties.getApps().size() == 1) {
            return oapiProperties.getApps().keySet().iterator().next();
        }
        return "default";
    }

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

    private static byte[] readBodyBytes(HttpServletRequest request) throws IOException {
        String body = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        return body.getBytes(StandardCharsets.UTF_8);
    }

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

