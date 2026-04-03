package com.larksuite.lark.core.advice;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lark.oapi.core.httpclient.IHttpTransport;
import com.lark.oapi.core.request.RawRequest;
import com.lark.oapi.core.response.RawResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

/**
 * 飞书 oapi-sdk 与 AE OpenAPI（{@code java.net.http.HttpClient}）共用的 HTTP 访问日志：
 * 每条成功响应一条 INFO（method、uri 路径、client 标识、耗时、HTTP 状态、body 内 code/msg、requestId），
 * 不打印请求/响应全文，避免泄露 token。
 * <p>
 * oapi-sdk 侧：创建 {@code Client} 时调用 {@link #wrapOapiTransport(IHttpTransport)} 作为 {@code httpTransport}。
 */
public final class HttpAccessLogger {

    private static final Logger LOG = LoggerFactory.getLogger(HttpAccessLogger.class);
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final int MAX_JSON_PREFIX = 8192;

    private HttpAccessLogger() {
    }

    /** 包装 SDK 默认 {@link IHttpTransport}，在工厂里传给 {@code Client.Builder#httpTransport}。 */
    public static IHttpTransport wrapOapiTransport(IHttpTransport delegate) {
        return new OapiSdkTransport(delegate);
    }

    /** 从完整 URL 中取出 path（不含 query），用于日志。 */
    public static String pathOnly(String reqUrl) {
        if (reqUrl == null || reqUrl.isBlank()) {
            return "-";
        }
        try {
            URI u = URI.create(reqUrl.trim());
            String p = u.getRawPath();
            return (p == null || p.isBlank()) ? reqUrl : p;
        } catch (Exception e) {
            return reqUrl;
        }
    }

    /**
     * SDK 侧：{@code client} 一般为飞书应用 appId。
     */
    public static void logSuccess(
            String method,
            String fullUrl,
            String client,
            long costMs,
            int httpStatus,
            byte[] body,
            boolean parseJsonBody,
            String requestId) {
        String path = pathOnly(fullUrl);
        String code = "";
        String msg = "";
        if (parseJsonBody && body != null && body.length > 0) {
            ParsedHead p = parseFeishuJsonHead(body);
            code = p.code;
            msg = p.msg;
        }
        String rid = requestId == null ? "" : requestId;
        LOG.info(
                "feishu http method={} uri={} client={} costMs={} httpStatus={} code={} msg={} requestId={}",
                method == null ? "-" : method,
                path,
                client == null ? "" : client,
                costMs,
                httpStatus,
                code,
                msg,
                rid);
    }

    /**
     * AE OpenAPI 侧：{@code client} 一般为 starter 中的 appKey；响应体为字符串。
     */
    public static void logSuccess(
            String method,
            String fullUrl,
            String client,
            long costMs,
            int httpStatus,
            String bodyText,
            String requestId) {
        byte[] bytes = (bodyText == null || bodyText.isEmpty()) ? null : bodyText.getBytes(StandardCharsets.UTF_8);
        logSuccess(method, fullUrl, client, costMs, httpStatus, bytes, true, requestId);
    }

    public static void logFailure(String method, String fullUrl, String client, long costMs, Throwable e) {
        String path = pathOnly(fullUrl);
        LOG.warn(
                "feishu http !! method={} uri={} client={} costMs={} error={}",
                method == null ? "-" : method,
                path,
                client == null ? "" : client,
                costMs,
                e.toString());
    }

    /** 从 {@link HttpResponse} 取飞书常见 request id 头（若有）。 */
    public static String requestIdFromHeaders(HttpResponse<?> resp) {
        if (resp == null) {
            return "";
        }
        Optional<String> a = resp.headers().firstValue("x-tt-logid");
        if (a.isPresent() && !a.get().isBlank()) {
            return a.get();
        }
        Optional<String> b = resp.headers().firstValue("x-request-id");
        return b.orElse("");
    }

    public static ParsedHead parseFeishuJsonHead(byte[] body) {
        ParsedHead out = new ParsedHead();
        if (body == null || body.length == 0) {
            return out;
        }
        if (body[0] != '{') {
            return out;
        }
        int n = Math.min(body.length, MAX_JSON_PREFIX);
        String prefix = new String(body, 0, n, StandardCharsets.UTF_8);
        try {
            JsonNode root = MAPPER.readTree(prefix);
            if (root.hasNonNull("code")) {
                out.code = root.get("code").asText();
            }
            if (root.hasNonNull("msg")) {
                out.msg = root.get("msg").asText();
            }
        } catch (Exception ignored) {
            // 非 JSON 或截断
        }
        return out;
    }

    /** 飞书/AE 常见响应 JSON 头部的 code/msg。 */
    public static final class ParsedHead {
        public String code = "";
        public String msg = "";
    }

    private static final class OapiSdkTransport implements IHttpTransport {
        private final IHttpTransport delegate;

        OapiSdkTransport(IHttpTransport delegate) {
            this.delegate = delegate;
        }

        @Override
        public RawResponse execute(RawRequest request) throws Exception {
            long startNs = System.nanoTime();
            String method = request.getHttpMethod() == null ? "-" : request.getHttpMethod();
            String client = "";
            if (request.getConfig() != null && request.getConfig().getAppId() != null) {
                client = request.getConfig().getAppId();
            }
            String fullUrl = request.getReqUrl();
            try {
                RawResponse resp = delegate.execute(request);
                long costMs = (System.nanoTime() - startNs) / 1_000_000L;
                String requestId = resp.getRequestID() == null ? "" : resp.getRequestID();
                logSuccess(
                        method,
                        fullUrl,
                        client,
                        costMs,
                        resp.getStatusCode(),
                        resp.getBody(),
                        !request.isSupportDownLoad(),
                        requestId);
                return resp;
            } catch (Exception e) {
                long costMs = (System.nanoTime() - startNs) / 1_000_000L;
                logFailure(method, fullUrl, client, costMs, e);
                throw e;
            }
        }
    }
}
