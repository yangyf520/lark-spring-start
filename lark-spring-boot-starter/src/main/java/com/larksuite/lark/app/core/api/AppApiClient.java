package com.larksuite.lark.app.core.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.larksuite.lark.core.advice.HttpAccessLogger;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Iterator;

public class AppApiClient {

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final AppApiProperties.App properties;
    private final String appKey;

    private final Object tokenLock = new Object();
    private volatile String appToken;
    private volatile long appTokenExpiresAtMs;

    public AppApiClient(ObjectMapper objectMapper, AppApiProperties.App properties, String appKey) {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(15))
                .build();
        this.objectMapper = objectMapper;
        this.properties = properties;
        this.appKey = appKey;
    }

    public String namespace() {
        return properties.getNamespace();
    }

    public JsonNode request(HttpMethod method, String path, Object body) throws Exception {
        String url = buildUrl(path);
        long startNs = System.nanoTime();
        String payload = "";
        if (body != null) {
            JsonNode tree = objectMapper.valueToTree(body);
            validateMetadataFieldEntries(tree, path);
            tree = normalizeRecordsPayload(tree, path);
            tree = normalizeRecordPayload(tree, path);
            tree = normalizeMetadataObjectsPayload(tree, path);
            tree = normalizeMetadataBatchDeletePayload(tree, path);
            payload = objectMapper.writeValueAsString(tree);
        }

        String token = appToken();
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", token)
                .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .timeout(Duration.ofSeconds(30));
        if (method == HttpMethod.GET || method == HttpMethod.DELETE && body == null) {
            builder.method(method.name(), HttpRequest.BodyPublishers.noBody());
        } else {
            builder.method(method.name(), HttpRequest.BodyPublishers.ofString(payload));
        }

        HttpResponse<String> resp;
        try {
            resp = httpClient.send(builder.build(), HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            long costMs = (System.nanoTime() - startNs) / 1_000_000L;
            HttpAccessLogger.logFailure(method.name(), url, appKey, costMs, e);
            throw e;
        }
        long costMs = (System.nanoTime() - startNs) / 1_000_000L;
        HttpAccessLogger.logSuccess(
                method.name(),
                url,
                appKey,
                costMs,
                resp.statusCode(),
                resp.body(),
                HttpAccessLogger.requestIdFromHeaders(resp));

        if (resp.statusCode() < 200 || resp.statusCode() >= 300) {
            throw new IOException("App OpenAPI call failed, status=" + resp.statusCode());
        }
        if (resp.body() == null || resp.body().isBlank()) {
            return objectMapper.createObjectNode();
        }
        JsonNode json = objectMapper.readTree(resp.body());
        String code = readText(json, "code");
        if (code != null && !code.isBlank() && !"0".equals(code)) {
            String msg = readText(json, "msg");
            throw new IOException("App OpenAPI business error: code=" + code + ", msg=" + msg);
        }
        return json;
    }

    /**
     * AE OpenAPI records_batch expects record fields to be flattened at record root.
     * Some callers send {@code {"records":[{"id":"...","fields":{...}}]}}; normalize by merging fields into record.
     */
    private JsonNode normalizeRecordsPayload(JsonNode root, String path) {
        if (root == null || !root.isObject()) {
            return root;
        }
        JsonNode records = root.get("records");
        if (records == null || !records.isArray()) {
            return root;
        }
        boolean changed = false;
        com.fasterxml.jackson.databind.node.ArrayNode out = objectMapper.createArrayNode();
        for (JsonNode rec : records) {
            JsonNode normalized = rec;
            if (rec != null && rec.isObject()) {
                JsonNode fields = rec.get("fields");
                if (fields != null && fields.isObject()) {
                    com.fasterxml.jackson.databind.node.ObjectNode merged = objectMapper.createObjectNode();
                    // copy all keys except "fields"
                    Iterator<String> it = rec.fieldNames();
                    while (it.hasNext()) {
                        String k = it.next();
                        if ("fields".equals(k)) {
                            continue;
                        }
                        merged.set(k, rec.get(k));
                    }
                    // merge fields object into root
                    Iterator<String> fit = fields.fieldNames();
                    while (fit.hasNext()) {
                        String k = fit.next();
                        merged.set(k, fields.get(k));
                    }
                    normalized = merged;
                    changed = true;
                }
            }

            // Convenience mapping for built-in "_user" object: allow name/email aliases.
            if (normalized != null && normalized.isObject()
                    && path != null && path.contains("/objects/_user/")) {
                com.fasterxml.jackson.databind.node.ObjectNode obj = (com.fasterxml.jackson.databind.node.ObjectNode) normalized;

                if (!obj.has("_email") && obj.has("email") && obj.get("email").isTextual()) {
                    obj.set("_email", obj.get("email"));
                    obj.remove("email");
                    changed = true;
                }

                if (!obj.has("_name") && obj.has("name")) {
                    JsonNode name = obj.get("name");
                    if (name != null) {
                        if (name.isTextual()) {
                            com.fasterxml.jackson.databind.node.ObjectNode i18n = objectMapper.createObjectNode();
                            i18n.put("zh_cn", name.asText());
                            obj.set("_name", i18n);
                            obj.remove("name");
                            changed = true;
                        } else if (name.isObject()) {
                            // If caller already provided an i18n object, just rename key.
                            obj.set("_name", name);
                            obj.remove("name");
                            changed = true;
                        }
                    }
                }
            }
            out.add(normalized);
        }
        if (!changed) {
            return root;
        }
        com.fasterxml.jackson.databind.node.ObjectNode copy = root.deepCopy();
        copy.set("records", out);
        return copy;
    }

    /**
     * AE OpenAPI single-record create endpoint uses { "record": { ... } }.
     * Normalize by merging record.fields into record root and applying _user aliases.
     */
    private JsonNode normalizeRecordPayload(JsonNode root, String path) {
        if (root == null || !root.isObject()) {
            return root;
        }
        JsonNode record = root.get("record");
        if (record == null || !record.isObject()) {
            return root;
        }

        boolean changed = false;
        com.fasterxml.jackson.databind.node.ObjectNode recObj = (com.fasterxml.jackson.databind.node.ObjectNode) record;

        JsonNode fields = recObj.get("fields");
        if (fields != null && fields.isObject()) {
            com.fasterxml.jackson.databind.node.ObjectNode merged = objectMapper.createObjectNode();
            // copy all keys except "fields"
            Iterator<String> it = recObj.fieldNames();
            while (it.hasNext()) {
                String k = it.next();
                if ("fields".equals(k)) {
                    continue;
                }
                merged.set(k, recObj.get(k));
            }
            // merge fields
            Iterator<String> fit = fields.fieldNames();
            while (fit.hasNext()) {
                String k = fit.next();
                merged.set(k, fields.get(k));
            }
            recObj = merged;
            changed = true;
        }

        // _user aliases for single record
        if (path != null && path.contains("/objects/_user/")) {
            if (!recObj.has("_email") && recObj.has("email") && recObj.get("email").isTextual()) {
                recObj.set("_email", recObj.get("email"));
                recObj.remove("email");
                changed = true;
            }
            if (!recObj.has("_name") && recObj.has("name")) {
                JsonNode name = recObj.get("name");
                if (name != null) {
                    if (name.isTextual()) {
                        com.fasterxml.jackson.databind.node.ObjectNode i18n = objectMapper.createObjectNode();
                        i18n.put("zh_cn", name.asText());
                        recObj.set("_name", i18n);
                        recObj.remove("name");
                        changed = true;
                    } else if (name.isObject()) {
                        recObj.set("_name", name);
                        recObj.remove("name");
                        changed = true;
                    }
                }
            }
        }

        if (!changed) {
            return root;
        }

        com.fasterxml.jackson.databind.node.ObjectNode copy = root.deepCopy();
        copy.set("record", recObj);
        return copy;
    }

    /**
     * Reject common mistake: putting record row data under {@code objects[].fields[]} as {@code {"fields":{...}}} without
     * field {@code api_name}, which causes Feishu k_ec_000012 (APIName empty).
     */
    private void validateMetadataFieldEntries(JsonNode root, String path) {
        if (root == null || !root.isObject() || path == null) {
            return;
        }
        if (!path.contains("/objects/batch_create") && !path.contains("/objects/batch_update")) {
            return;
        }
        JsonNode objects = root.get("objects");
        if (objects == null || !objects.isArray()) {
            return;
        }
        for (JsonNode obj : objects) {
            if (obj == null || !obj.isObject()) {
                continue;
            }
            JsonNode fields = obj.get("fields");
            if (fields == null || !fields.isArray()) {
                continue;
            }
            for (JsonNode f : fields) {
                if (f == null || !f.isObject()) {
                    continue;
                }
                boolean hasApiName = f.hasNonNull("api_name")
                        || f.hasNonNull("field_api_name")
                        || f.hasNonNull("object_api_name");
                JsonNode nested = f.get("fields");
                if (nested != null && nested.isObject() && !hasApiName) {
                    throw new IllegalArgumentException(
                            "objects[].fields[] must be field definitions (each needs api_name, label, type, …). "
                                    + "Do not put record row values under a nested \"fields\" object here — "
                                    + "use POST /lark/app/data/objects/{objectApiName}/records/batch-create instead.");
                }
            }
        }
    }

    /**
     * AE OpenAPI objects/batch_create|batch_update expects {@code api_name} and field {@code api_name} / nested {@code type}.
     * Accept convenience aliases from older examples: {@code object_api_name}, {@code field_api_name}, {@code field_type},
     * plain string {@code label}.
     */
    private JsonNode normalizeMetadataObjectsPayload(JsonNode root, String path) {
        if (root == null || !root.isObject() || path == null) {
            return root;
        }
        if (!path.contains("/objects/batch_create") && !path.contains("/objects/batch_update")) {
            return root;
        }
        JsonNode objects = root.get("objects");
        if (objects == null || !objects.isArray() || objects.size() == 0) {
            return root;
        }
        boolean changed = false;
        ArrayNode outObjects = objectMapper.createArrayNode();
        for (JsonNode obj : objects) {
            if (obj == null || !obj.isObject()) {
                outObjects.add(obj);
                continue;
            }
            ObjectNode o = obj.deepCopy();
            if (o.has("object_api_name") && !o.has("api_name")) {
                o.set("api_name", o.remove("object_api_name"));
                changed = true;
            }
            JsonNode lab = o.get("label");
            if (lab != null && lab.isTextual()) {
                ObjectNode i18n = objectMapper.createObjectNode();
                i18n.put("zh_cn", lab.asText());
                o.set("label", i18n);
                changed = true;
            }
            JsonNode fields = o.get("fields");
            if (fields != null && fields.isArray()) {
                ArrayNode outFields = objectMapper.createArrayNode();
                for (JsonNode f : fields) {
                    if (f == null || !f.isObject()) {
                        outFields.add(f);
                        continue;
                    }
                    ObjectNode field = f.deepCopy();
                    if (field.has("field_api_name") && !field.has("api_name")) {
                        field.set("api_name", field.remove("field_api_name"));
                        changed = true;
                    }
                    JsonNode flab = field.get("label");
                    if (flab != null && flab.isTextual()) {
                        ObjectNode fi18n = objectMapper.createObjectNode();
                        fi18n.put("zh_cn", flab.asText());
                        field.set("label", fi18n);
                        changed = true;
                    }
                    if (!field.has("type") && field.has("field_type") && field.get("field_type").isTextual()) {
                        ObjectNode typeObj = objectMapper.createObjectNode();
                        typeObj.put("name", field.remove("field_type").asText());
                        field.set("type", typeObj);
                        changed = true;
                    }
                    outFields.add(field);
                }
                o.set("fields", outFields);
            }
            outObjects.add(o);
        }
        if (!changed) {
            return root;
        }
        ObjectNode copy = root.deepCopy();
        copy.set("objects", outObjects);
        return copy;
    }

    /** AE OpenAPI objects/batch_delete expects {@code api_names}; accept {@code object_api_names} from our VO / scripts. */
    private JsonNode normalizeMetadataBatchDeletePayload(JsonNode root, String path) {
        if (root == null || !root.isObject() || path == null || !path.contains("/objects/batch_delete")) {
            return root;
        }
        ObjectNode o = (ObjectNode) root;
        if (!o.has("object_api_names") || o.has("api_names")) {
            return root;
        }
        ObjectNode copy = o.deepCopy();
        copy.set("api_names", copy.remove("object_api_names"));
        return copy;
    }

    private String appToken() throws Exception {
        String configured = normalizeAuthorization(properties.getToken());
        if (configured != null) {
            return configured;
        }

        long now = System.currentTimeMillis();
        String token = appToken;
        if (token != null && now < appTokenExpiresAtMs) {
            return token;
        }
        synchronized (tokenLock) {
            // Double check inside lock.
            now = System.currentTimeMillis();
            token = appToken;
            if (token != null && now < appTokenExpiresAtMs) {
                return token;
            }
            refreshAppToken();
            return appToken;
        }
    }

    private void refreshAppToken() throws Exception {
        if (properties.getId() == null || properties.getId().isBlank()) {
            throw new IllegalStateException("lark.apass.apps." + appKey + ".id is required");
        }
        if (properties.getSecret() == null || properties.getSecret().isBlank()) {
            throw new IllegalStateException("lark.apass.apps." + appKey + ".secret is required");
        }

        // AE OpenAPI /auth/v1/appToken expects camelCase JSON keys (clientId / clientSecret), not snake_case.
        // Put returned token directly in Authorization header as "T:...".
        String path = properties.getAppTokenPath();
        Object body = java.util.Map.of(
                "clientId", properties.getId(),
                "clientSecret", properties.getSecret()
        );
        String payload = objectMapper.writeValueAsString(body);
        String url = buildUrl(path);

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .timeout(Duration.ofSeconds(30))
                .POST(HttpRequest.BodyPublishers.ofString(payload))
                .build();

        long startNs = System.nanoTime();
        HttpResponse<String> resp;
        try {
            resp = httpClient.send(req, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            long costMs = (System.nanoTime() - startNs) / 1_000_000L;
            HttpAccessLogger.logFailure("POST", url, appKey, costMs, e);
            throw e;
        }
        long costMs = (System.nanoTime() - startNs) / 1_000_000L;
        HttpAccessLogger.logSuccess(
                "POST",
                url,
                appKey,
                costMs,
                resp.statusCode(),
                resp.body(),
                HttpAccessLogger.requestIdFromHeaders(resp));

        if (resp.statusCode() < 200 || resp.statusCode() >= 300) {
            throw new IOException("Fetch appToken failed, status=" + resp.statusCode());
        }
        String respBody = resp.body();
        if (respBody == null || respBody.isBlank()) {
            throw new IOException("Fetch appToken failed: empty body");
        }
        JsonNode json = objectMapper.readTree(respBody);
        String code = readText(json, "code");
        if (code != null && !code.isBlank() && !"0".equals(code)) {
            String msg = readText(json, "msg");
            throw new IOException("Fetch appToken failed: code=" + code + ", msg=" + msg);
        }
        long now = System.currentTimeMillis();
        String token = normalizeAuthorization(extractToken(json));
        if (token == null || token.isBlank()) {
            throw new IOException("Fetch appToken failed: token not found");
        }

        long expiresMs = extractExpireSeconds(json) * 1000L;
        if (expiresMs <= 0) {
            // Fallback: appToken for OpenAPI is typically valid for a couple of hours.
            expiresMs = Duration.ofHours(2).toMillis();
        }
        // Renew a bit earlier.
        expiresMs = Math.max(60_000L, expiresMs - 60_000L);

        this.appToken = token;
        this.appTokenExpiresAtMs = now + expiresMs;
    }

    private String normalizeAuthorization(String token) {
        if (token == null) {
            return null;
        }
        String t = token.trim();
        if (t.isEmpty()) {
            return null;
        }
        // AE OpenAPI commonly expects Authorization like "T:xxxxx".
        // If caller provides raw token without prefix, best-effort add it.
        if (t.startsWith("T:") || t.startsWith("t:")) {
            return "T:" + t.substring(2);
        }
        if (t.startsWith("Bearer ") || t.startsWith("bearer ")) {
            return t;
        }
        return "T:" + t;
    }

    private String extractToken(JsonNode json) {
        if (json == null) {
            return null;
        }
        if (json.hasNonNull("appToken")) {
            return json.get("appToken").asText();
        }
        if (json.hasNonNull("token")) {
            return json.get("token").asText();
        }
        if (json.hasNonNull("accessToken")) {
            return json.get("accessToken").asText();
        }
        if (json.has("data")) {
            JsonNode data = json.get("data");
            if (data != null) {
                if (data.hasNonNull("appToken")) {
                    return data.get("appToken").asText();
                }
                if (data.hasNonNull("token")) {
                    return data.get("token").asText();
                }
                if (data.hasNonNull("apiToken")) {
                    return data.get("apiToken").asText();
                }
                if (data.hasNonNull("accessToken")) {
                    return data.get("accessToken").asText();
                }
                if (data.isTextual()) {
                    return data.asText();
                }
            }
        }
        return null;
    }

    private String readText(JsonNode json, String field) {
        if (json == null || field == null || field.isBlank()) {
            return null;
        }
        JsonNode v = json.get(field);
        if (v == null || v.isNull()) {
            return null;
        }
        return v.isTextual() ? v.asText() : v.toString();
    }

    private long extractExpireSeconds(JsonNode json) {
        if (json == null) {
            return 0;
        }
        if (json.hasNonNull("expire")) {
            return json.get("expire").asLong();
        }
        if (json.hasNonNull("expireTime")) {
            // Some AE OpenAPI responses return absolute expire time in milliseconds.
            long expireTimeMs = json.get("expireTime").asLong();
            long nowMs = System.currentTimeMillis();
            long deltaMs = Math.max(0L, expireTimeMs - nowMs);
            return deltaMs / 1000L;
        }
        if (json.has("data")) {
            JsonNode data = json.get("data");
            if (data != null) {
                if (data.hasNonNull("expire")) {
                    return data.get("expire").asLong();
                }
                if (data.hasNonNull("expireTime")) {
                    long expireTimeMs = data.get("expireTime").asLong();
                    long nowMs = System.currentTimeMillis();
                    long deltaMs = Math.max(0L, expireTimeMs - nowMs);
                    return deltaMs / 1000L;
                }
            }
        }
        return 0;
    }

    private String buildUrl(String path) {
        if (path.startsWith("http://") || path.startsWith("https://")) {
            return path;
        }
        String base = properties.getBaseUrl();
        if (base.endsWith("/") && path.startsWith("/")) {
            return base.substring(0, base.length() - 1) + path;
        }
        if (!base.endsWith("/") && !path.startsWith("/")) {
            return base + "/" + path;
        }
        return base + path;
    }
}
