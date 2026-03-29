package com.larksuite.lark.core.token;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.larksuite.lark.oapi.spring.OapiProperties;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

/** 租户 access_token：内存缓存，供管理接口等探测用。 */
public class TenantAccessTokenProvider {

    private static final Duration REFRESH_SKEW = Duration.ofMinutes(5);

    private final HttpClient http;
    private final OapiProperties.App app;
    private final ObjectMapper objectMapper;
    private final Clock clock = Clock.systemUTC();

    private final AtomicReference<CachedToken> cache = new AtomicReference<>(CachedToken.empty());

    public TenantAccessTokenProvider(HttpClient http, OapiProperties props, ObjectMapper objectMapper) {
        this.http = http;
        this.objectMapper = objectMapper;
        this.app = resolvePrimaryApp(props);
    }

    /** 返回有效 tenant_access_token，必要时刷新。 */
    public String getToken() throws IOException {
        CachedToken current = cache.get();
        if (current.isUsableAt(Instant.now(clock))) {
            return current.token();
        }
        synchronized (this) {
            CachedToken again = cache.get();
            if (again.isUsableAt(Instant.now(clock))) {
                return again.token();
            }
            CachedToken refreshed = fetchNewToken();
            cache.set(refreshed);
            return refreshed.token();
        }
    }

    private CachedToken fetchNewToken() throws IOException {
        String domain = baseUrlFrom(app).replaceAll("/+$", "");
        String url = domain + "/open-apis/auth/v3/tenant_access_token/internal";

        JsonNode body = objectMapper.createObjectNode()
                .put("app_id", app.getAppId())
                .put("app_secret", app.getAppSecret());

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(30))
                .header("Content-Type", "application/json; charset=utf-8")
                .POST(HttpRequest.BodyPublishers.ofString(body.toString()))
                .build();

        HttpResponse<String> response;
        try {
            response = http.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Interrupted while fetching tenant_access_token", e);
        }

        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new IOException("Failed to fetch tenant_access_token, http=" + response.statusCode());
        }

        String raw = Objects.requireNonNull(response.body());
        JsonNode json = objectMapper.readTree(raw);
        int code = json.path("code").asInt(-1);
        if (code != 0) {
            throw new IOException("Failed to fetch tenant_access_token, code=" + code + ", msg=" + json.path("msg").asText());
        }
        String token = json.path("tenant_access_token").asText();
        if (token == null || token.isBlank()) {
            throw new IOException("tenant_access_token is empty");
        }
        long expireSec = json.path("expire").asLong(0);
        Instant expiresAt = Instant.now(clock).plusSeconds(expireSec);
        return new CachedToken(token, expiresAt);
    }

    private static OapiProperties.App resolvePrimaryApp(OapiProperties props) {
        if (props == null || props.getApps() == null || props.getApps().isEmpty()) {
            throw new IllegalStateException("No Lark apps configured. Please set lark.oapi.apps.*");
        }
        String primary = props.getPrimary();
        if (primary != null && !primary.isBlank()) {
            OapiProperties.App app = props.getApps().get(primary);
            if (app == null) {
                throw new IllegalStateException("lark.oapi.primary='" + primary + "' not found in lark.oapi.apps");
            }
            return app;
        }
        if (props.getApps().size() == 1) {
            return props.getApps().values().iterator().next();
        }
        throw new IllegalStateException("Multiple Lark apps configured but no primary specified. Please set lark.oapi.primary");
    }

    private static String baseUrlFrom(OapiProperties.App app) {
        String baseUrl = app.getBaseUrl();
        if (baseUrl == null || baseUrl.isBlank()) {
            return "https://open.feishu.cn";
        }
        return baseUrl;
    }

    private record CachedToken(String token, Instant expiresAt) {
        static CachedToken empty() {
            return new CachedToken("", Instant.EPOCH);
        }

        boolean isUsableAt(Instant now) {
            if (token == null || token.isBlank()) {
                return false;
            }
            return now.isBefore(expiresAt.minus(REFRESH_SKEW));
        }
    }
}

